package ru.kata.blockchain.infrastructure.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import ru.kata.blockchain.domain.block.Block;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Класс {@code BlockMapper} отвечает за сериализацию и десериализацию блоков {@link Block}
 * между объектами Java и JSON-представлением.
 *
 * <p>Используется для чтения блокчейна из файла и сохранения его обратно.
 * Основан на библиотеке Jackson и поддерживает Guava-коллекции
 * (например, {@code ImmutableList}) через модуль {@code GuavaModule}.</p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *     <li>{@link #readBlocks(byte[])} — десериализует JSON-массив байтов в список блоков</li>
 *     <li>{@link #writeToFile(Path, List)} — сериализует список блоков в JSON и сохраняет в файл</li>
 * </ul>
 */
public final class BlockMapper {
    private final ObjectMapper objectMapper;

    public BlockMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new GuavaModule()); // для поддержки ImmutableList
        this.objectMapper.registerModule(new JavaTimeModule()); //для поддержки Instant
    }

    /**
     * Преобразует JSON в список блоков.
     */

    public List<Block> readBlocks(byte[] json) throws IOException {
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    /**
     * Сохраняет список блоков в файл, сериализуя его в отформатированный JSON.
     */

    public void writeToFile(Path tempPath, List<Block> blockchain) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempPath.toFile(), blockchain);
    }
}
