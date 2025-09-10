package ru.kata.blockchain.adapters.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kata.blockchain.domain.vo.Hash;
import ru.kata.blockchain.adapters.dto.BlockDto;
import com.google.common.collect.ImmutableList;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HashFactoryTest {

    private HashFactory hashFactory;

    @BeforeEach
    void setUp() {
        hashFactory = new HashFactory();
    }

    @Test
    void createValidHash() {
        final BlockDto blockDto = new BlockDto(
                1L,
                Instant.now(),
                ImmutableList.of(),
                new Hash("0000000000000000000000000000000000000000000000000000000000000000")
        );

        final Hash hash = hashFactory.createHash(blockDto);

        assertNotNull(hash);
        assertThat(hash.value()).matches(Hash.getHexPattern());
        assertThat(hash.value()).hasSize(64);
    }

    @Test
    void createHashGenerateDifferentHashesForDifferentBlocks() {
        final BlockDto block1 = new BlockDto(
                1,
                Instant.now(),
                ImmutableList.of(),
                new Hash("0000000000000000000000000000000000000000000000000000000000000000")
        );
        final BlockDto block2 = new BlockDto(
                2,
                Instant.now(),
                ImmutableList.of(),
                new Hash("0000000000000000000000000000000000000000000000000000000000000000")
        );

        final Hash hash1 = hashFactory.createHash(block1);
        final Hash hash2 = hashFactory.createHash(block2);

        assertNotEquals(hash1.value(), hash2.value(), "Хэши разных блоков должны отличаться");
    }

    @Test
    void createHashGenerateEqualsHashesForEqualsBlocks() {
        final BlockDto block = new BlockDto(
                1,
                Instant.now(),
                ImmutableList.of(),
                new Hash("0000000000000000000000000000000000000000000000000000000000000000")
        );

        final Hash hash1 = hashFactory.createHash(block);
        final Hash hash2 = hashFactory.createHash(block);

        assertEquals(hash1.value(), hash2.value(), "Хэши одинаковых блоков должны совпадать");
    }
}
