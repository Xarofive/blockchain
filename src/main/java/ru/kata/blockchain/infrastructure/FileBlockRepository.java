package ru.kata.blockchain.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import lombok.extern.slf4j.Slf4j;
import ru.kata.blockchain.domain.Block;
import ru.kata.blockchain.domain.BlockRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public FileBlockRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        objectMapper.registerModule(new GuavaModule()); // для поддержки ImmutableList
        initFileIfMissing();                            // создаёт файл, если его нет
        loadBlockchainFromFile();                       // загружает блоки в память
    }

    @Override
    public synchronized void save(Block block) {
        blockchain.add(block);
        saveBlockchainToFile();
        log.info("Block saved: {}", block);
    }

    @Override
    public synchronized List<Block> findAll() {
        return Collections.unmodifiableList(blockchain);
    }

    @Override
    public synchronized Block findLatest() {
        return blockchain.getLast();
    }

    /**
     * Проверяет наличие файла блокчейна, и если он отсутствует — создаёт его
     * и записывает пустой JSON-массив
     */
    private void initFileIfMissing() {
        try {
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
     * Загружает блоки из JSON-файла в память.
     * Используется при инициализации класса.
     */
    private void loadBlockchainFromFile() {
        try {
            final byte[] json = Files.readAllBytes(filePath);
            if (json.length > 0) {
                final List<Block> blocks = objectMapper.readValue(json, new TypeReference<>() {
                });
                blockchain.addAll(blocks);
                log.info("Loaded {} block(s) from file", blockchain.size());
            }
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
}