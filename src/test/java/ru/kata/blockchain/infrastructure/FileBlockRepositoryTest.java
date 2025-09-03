package ru.kata.blockchain.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.blockchain.domain.block.Block;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FileBlockRepositoryTest {
    private FileBlockRepository fileBlockRepository;
    private Block block1;
    private Block block2;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws Exception {
        final Path filePath = tempDir.resolve("test-blockchain.json");
        fileBlockRepository = new FileBlockRepository(filePath);
        block1 = mock(Block.class);
        block2 = mock(Block.class);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenJsonIsNotArray(@TempDir Path tempDir) throws Exception {
        final Path filePath = tempDir.resolve("test-blockchain.json");
        Files.writeString(filePath, "{}");
        assertThrows(RuntimeException.class, () -> new FileBlockRepository(filePath));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFileAccessFails(@TempDir Path tempDir) throws Exception {
        final Path filePath = tempDir.resolve("test-blockchain.json");
        Files.createFile(filePath);
        filePath.toFile().setReadable(false);
        if (!filePath.toFile().canRead()) {
            assertThrows(RuntimeException.class, () -> new FileBlockRepository(filePath));
        }
    }

    @Test
    void shouldLoadAllBlocksFromFile(@TempDir Path tempDir) {
        final Path filePath = tempDir.resolve("test-blockchain.json");
        final FileBlockRepository repository = new FileBlockRepository(filePath);
        final Block block1 = mock(Block.class);
        final Block block2 = mock(Block.class);
        repository.save(block1);
        repository.save(block2);

        final Optional<Block> latest = repository.findLatest();
        assertTrue(latest.isPresent());
        assertEquals(block2, latest.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenBlockchainIsEmpty() {
        final Optional<Block> result = fileBlockRepository.findLatest();
        assertTrue(result.isEmpty());
    }

    @Test
    void methodSaveSavesBlockToBlockchain() {
        fileBlockRepository.save(block1);

        final Optional<Block> result = fileBlockRepository.findLatest();
        assertTrue(result.isPresent());
        assertEquals(block1, result.get());
    }

    @Test
    void shouldReturnLastBlockWhenBlockchainIsNotEmpty() {
        fileBlockRepository.save(block1);
        fileBlockRepository.save(block2);

        final Optional<Block> result = fileBlockRepository.findLatest();
        assertTrue(result.isPresent());
        assertEquals(block2, result.get());
    }
}