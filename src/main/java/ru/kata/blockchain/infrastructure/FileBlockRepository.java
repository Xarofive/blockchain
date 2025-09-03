package ru.kata.blockchain.infrastructure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import ru.kata.blockchain.domain.block.Block;
import ru.kata.blockchain.domain.block.BlockRepository;
import ru.kata.blockchain.exceptions.InvalidBlockchainPathException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link BlockRepository}, которая хранит блокчейн в виде списка {@link Block}
 * в обычном JSON-файле на диске.
 * <p>
 * Используется для реализации персистентного хранения блоков
 * без подключения к базе данных. Все блоки хранятся в памяти в {@code blockchain}
 * и сериализуются в JSON-файл при каждом добавлении нового блока.
 * </p>
 *
 * <p><b>Основные особенности:</b></p>
 * <ul>
 *   <li>При создании проверяет, существует ли файл, и создаёт его, если нет</li>
 *   <li>Загружает всю цепочку блоков из файла при инициализации</li>
 *   <li>Сохраняет весь список блоков в JSON-файл каждый раз при добавлении нового блока</li>
 *   <li>Поддерживает потокобезопасный доступ к методам</li>
 * </ul>
 */
@Slf4j
public class FileBlockRepository implements BlockRepository {

    private final Path filePath;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Block> blockchain = new ArrayList<>();
    private static final String NULL_ERROR_MASSAGE = "The file name must not be null";
    private static final String EMPTY_ERROR_MASSAGE = "The file name must not be empty";
    private static final String ABSOLUTE_PATH_ERROR_MASSAGE = "Absolute paths are not allowed: ";
    private static final String DIRECTORY_ATTACK_ERROR_MASSAGE = "Directory traversal attack detected: ";
    private static final String INVALID_NAME_ERROR_MASSAGE = "Invalid file name: ";
    private static final int MAX_FILE_NAME_LENGTH = 100;
    private static final String FILE_NAME_LENGTH_ERROR_MASSAGE = "The file name should not exceed "
            + MAX_FILE_NAME_LENGTH + " characters in length: ";
    private static final String FILE_NAME_PATTERN = "^[a-zA-Z0-9_.-]+\\.json$";
    private static final Path BASE_DIR = Paths.get("data/blockchain");

    public FileBlockRepository(String fileName) {
        validateFilePath(fileName);                     // валидация на атаку через обход каталога
        this.filePath = BASE_DIR.resolve(fileName.trim()).normalize();
        objectMapper.registerModule(new GuavaModule()); // для поддержки ImmutableList
        initFileIfMissing();                            // создаёт файл, если его нет
        loadBlockchainFromFile();                       // загружает блоки в память
    }

    @VisibleForTesting
    FileBlockRepository(Path filePath) {
        this.filePath = filePath;
        objectMapper.registerModule(new GuavaModule());
        initFileIfMissing();
        loadBlockchainFromFile();
    }

    @Override
    public synchronized void save(Block block) {
        blockchain.add(block);
        try {
            saveBlockchainToFile();
            log.info("Block saved: {}", block);
        } catch (RuntimeException e) {
            blockchain.remove(block);
            throw e;
        }
    }

    @Override
    public synchronized List<Block> findAll() {
        return Collections.unmodifiableList(blockchain);
    }

    /**
     * Находит последний блок в цепочке блокчейна.
     * <p>
     * Возвращает {@code Optional}, содержащий последний блок, если цепочка не пуста.
     * Если цепочка пуста, возвращается пустой {@code Optional}. Метод синхронизирован для
     * обеспечения потокобезопасного доступа к цепочке блокчейна.
     *
     * @return {@code Optional<Block>} с последним блоком или пустой, если цепочка пуста
     */
    @Override
    public synchronized Optional<Block> findLatest() {
        if (blockchain.isEmpty()) {
            log.info("No block found");
            return Optional.empty();
        }
        return Optional.of(blockchain.getLast());
    }

    /**
     * Проверяет наличие файла блокчейна, и если он отсутствует — создаёт его
     * и записывает пустой JSON-массив
     * Создание директории при инициализации.
     */
    private void initFileIfMissing() {
        try {
            Files.createDirectories(BASE_DIR);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, "[]".getBytes());
                log.info("Created new blockchain file: {}", filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize blockchain file: " + filePath, e);
        }
    }

    /**
     * Метод загружает цепочку блоков из JSON-файла в список {@code blockchain}.
     * <p>
     * Использует потоковый парсер Jackson для последовательного чтения JSON-массива блоков без полной загрузки файла в память.
     * Каждый блок десериализуется и добавляется в коллекцию.
     * <p>
     * Формат файла должен быть JSON-массивом, где каждый элемент — объект Block.
     * <p>
     * В случае ошибки чтения файла или неправильного формата JSON метод выбрасывает {@link RuntimeException}.
     */
    private void loadBlockchainFromFile() {
        try (InputStream inputStream = Files.newInputStream(filePath);
             JsonParser parser = objectMapper.getFactory().createParser(inputStream)) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new RuntimeException("Blockchain file should contain JSON array");
            }

            while (parser.nextToken() != JsonToken.END_ARRAY) {
                final Block block = objectMapper.readValue(parser, Block.class);
                blockchain.add(block);
            }
            log.info("Loaded {} block(s) from file", blockchain.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load blockchain from file: " + filePath, e);
        }
    }

    /**
     * Сохраняет всю цепочку блоков в файл.
     * Сначала записывает данные во временный файл, затем
     * атомарно заменяет старый файл новым.
     */
    private void saveBlockchainToFile() {
        try {
            final Path tempPath = Paths.get(filePath.toString() + ".tmp");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempPath.toFile(), blockchain);
            Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save blockchain to file: " + filePath, e);
        }
    }

    /**
     * Данный метод отвечает за валидацию файла.
     * Для начала исключаются моменты с null и пустым именем, чтобы не поймать NullPointerException.
     * Далее идет проверка на абсолютный путь.
     * Проверка имени файла по регулярному выражению + строго задан формат файла, а именно .json.
     * Проверка длины имени файла, чтобы не ловить edge‑кейсы ФС.
     * Проверка гарантии на то, чтобы путь остается в безопасной директории data/blockchain.
     */
    private void validateFilePath(String fileName) {
        if (fileName == null) {
            throw new InvalidBlockchainPathException(NULL_ERROR_MASSAGE);
        }

        if (fileName.isBlank()) {
            throw new InvalidBlockchainPathException(EMPTY_ERROR_MASSAGE);
        }

        final String trimmedFileName = fileName.trim();
        final Path validateFile = Paths.get(trimmedFileName);

        if (validateFile.isAbsolute() || trimmedFileName.startsWith("/")) {
            throw new InvalidBlockchainPathException(ABSOLUTE_PATH_ERROR_MASSAGE + trimmedFileName);
        }

        final Path resolvedPath = BASE_DIR.resolve(trimmedFileName).normalize();
        if (!resolvedPath.startsWith(BASE_DIR)) {
            throw new InvalidBlockchainPathException(DIRECTORY_ATTACK_ERROR_MASSAGE + trimmedFileName);
        }

        if (!trimmedFileName.matches(FILE_NAME_PATTERN)) {
            throw new InvalidBlockchainPathException(INVALID_NAME_ERROR_MASSAGE + trimmedFileName);
        }

        if (trimmedFileName.length() > MAX_FILE_NAME_LENGTH) {
            throw new InvalidBlockchainPathException(FILE_NAME_LENGTH_ERROR_MASSAGE + trimmedFileName);
        }
    }
}