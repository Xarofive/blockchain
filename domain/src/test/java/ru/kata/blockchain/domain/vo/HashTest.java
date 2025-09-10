package ru.kata.blockchain.domain.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HashTest {
    @Test
    void validHashIsAccepted() {
        final String validHash = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        final Hash hash = new Hash(validHash);
        assertEquals(validHash, hash.value());
    }

    @Test
    void nullHashThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Hash(null));
    }

    @Test
    void tooShortHashThrowsException() {
        final String shortHash = "abc123";
        assertThrows(IllegalArgumentException.class, () -> new Hash(shortHash));
    }

    @Test
    void tooLongHashThrowsException() {
        final String longHash = "a".repeat(65); // 65 символов вместо 64
        assertThrows(IllegalArgumentException.class, () -> new Hash(longHash));
    }

    @Test
    void hashWithUppercaseLettersThrowsException() {
        final String upperCaseHash = "ABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCD";
        assertThrows(IllegalArgumentException.class, () -> new Hash(upperCaseHash));
    }

    @Test
    void hashWithNonHexSymbolsThrowsException() {
        final String incorrectHash = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
        assertThrows(IllegalArgumentException.class, () -> new Hash(incorrectHash));
    }

    @Test
    void hashToStringIsReadable() {
        final String validHash = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        final Hash hash = new Hash(validHash);
        assertTrue(hash.toString().contains(validHash));
    }

    @Test
    void hashEqualityWorks() {
        final String hashValue = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        final Hash h1 = new Hash(hashValue);
        final Hash h2 = new Hash(hashValue);
        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());
    }
}