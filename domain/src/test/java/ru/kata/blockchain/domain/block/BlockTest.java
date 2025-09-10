package ru.kata.blockchain.domain.block;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Hash;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BlockTest {

    private long index;
    private Instant timestamp;
    private ImmutableList<Transaction> transactions;
    private Hash previousHash;
    private long nonce;
    private Hash hash;

    @BeforeEach
    void setUp() {
        index = 1L;
        timestamp = Instant.now();
        transactions = ImmutableList.of(mock(Transaction.class));
        previousHash = mock(Hash.class);
        nonce = 123L;
        hash = mock(Hash.class);
    }

    @Test
    void createBlockWithValidParameters() {
        final Block block = new Block(index, timestamp, transactions, previousHash, nonce, hash);
        assertNotNull(block);
        assertEquals(index, block.index());
        assertEquals(timestamp, block.timestamp());
        assertEquals(transactions, block.transactions());
        assertEquals(previousHash, block.previousHash());
        assertEquals(nonce, block.nonce());
        assertEquals(hash, block.hash());
    }

    @Test
    void nullTimestampShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Block(index, null, transactions, previousHash, nonce, hash));
    }

    @Test
    void nullTransactionsListShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Block(index, timestamp, null, previousHash, nonce, hash));
    }

    @Test
    void nullPreviousHashShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Block(index, timestamp, transactions, null, nonce, hash));
    }

    @Test
    void nullHashShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Block(index, timestamp, transactions, previousHash, nonce, null));
    }
}