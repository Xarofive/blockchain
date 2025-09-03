package ru.kata.blockchain.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kata.blockchain.domain.block.Block;
import ru.kata.blockchain.domain.block.BlockRepository;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Hash;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;

class AddBlockUseCaseTest {

    private BlockRepository blockRepository;
    private AddBlockUseCase addBlockUseCase;
    private Block latestBlock;
    private Hash latestHash;

    @BeforeEach
    void setUp() {
        blockRepository = mock(BlockRepository.class);
        addBlockUseCase = new AddBlockUseCase(blockRepository);
        latestHash = mock(Hash.class);
        latestBlock = new Block(
                1L,
                Instant.now(),
                ImmutableList.of(mock(Transaction.class)),
                mock(Hash.class),
                100L,
                latestHash
        );

        when(blockRepository.findLatest()).thenReturn(Optional.ofNullable(latestBlock));
    }

    @Test
    void validBlockShouldBeSaved() {
        final Hash newHash = mock(Hash.class);
        final Block validBlock = new Block(
                2L,
                Instant.now(),
                ImmutableList.of(mock(Transaction.class)),
                latestHash,
                222L,
                newHash
        );

        addBlockUseCase.addBlock(validBlock);
        verify(blockRepository).save(validBlock);
    }

    @Test
    void blockWithWrongPreviousHashShouldNotBeSaved() {
        final Hash wrongHash = mock(Hash.class);

        final Block invalidBlock = new Block(
                2L,
                Instant.now(),
                ImmutableList.of(mock(Transaction.class)),
                wrongHash,
                222L,
                mock(Hash.class)
        );

        addBlockUseCase.addBlock(invalidBlock);
        verify(blockRepository, never()).save(any());
    }

    @Test
    void blockWithWrongIndexShouldNotBeSaved() {
        final Block invalidBlock = new Block(
                5L,
                Instant.now(),
                ImmutableList.of(mock(Transaction.class)),
                latestHash,
                200L,
                mock(Hash.class)
        );

        addBlockUseCase.addBlock(invalidBlock);
        verify(blockRepository, never()).save(any());
    }
}