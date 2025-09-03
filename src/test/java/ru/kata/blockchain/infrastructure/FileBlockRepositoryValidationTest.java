package ru.kata.blockchain.infrastructure;

import org.junit.jupiter.api.Test;
import ru.kata.blockchain.exceptions.InvalidBlockchainPathException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBlockRepositoryValidationTest {

    @Test
    void shouldThrowExceptionIfFileNameIsNull() {
        assertThrows(InvalidBlockchainPathException.class, () -> {
            new FileBlockRepository((String) null);
        });
    }

    @Test
    void shouldThrowExceptionIfFileNameIsEmpty() {
        assertThrows(InvalidBlockchainPathException.class, () -> {
            new FileBlockRepository(" ");
        });
    }

    @Test
    void shouldThrowExceptionIfFileNameIsAbsolute() {
        assertThrows(InvalidBlockchainPathException.class, () -> {
            new  FileBlockRepository("/etc/passwd");
        });
    }

    @Test
    void shouldThrowExceptionIfFileNameContainsInvalidChars() {
        assertThrows(InvalidBlockchainPathException.class, () -> {
            new  FileBlockRepository("test$123.json");
        });
    }

    @Test
    void shouldThrowExceptionIfFileNameTooLong() {
        assertThrows(InvalidBlockchainPathException.class, () -> {
            new FileBlockRepository("t".repeat(101) + ".json");
        });
    }

    @Test
    void shouldThrowExceptionIfFileNameDirectoryAttack() {
        assertThrows(InvalidBlockchainPathException.class, () -> {
            new  FileBlockRepository("../../etc/passwd");
        });
    }

    @Test
    void shouldAcceptValidFileName() {
        assertDoesNotThrow(() -> {
            new  FileBlockRepository("test123.json");
        });
    }

    @Test
    void shouldAcceptValidFileNameWithUnderscore() {
        assertDoesNotThrow(() -> {
            new  FileBlockRepository("test_123.json");
        });
    }

    @Test
    void shouldAcceptValidFileNameWithDashes(){
        assertDoesNotThrow(() -> {
            new  FileBlockRepository("test-123.json");
        });
    }
}
