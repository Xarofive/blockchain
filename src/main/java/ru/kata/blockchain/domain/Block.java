package ru.kata.blockchain.domain;

import com.google.common.collect.ImmutableList;
import ru.kata.blockchain.domain.vo.Hash;

import java.time.Instant;

/**
 * Представляет блок в блокчейне. Каждый блок содержит список транзакций и связан с предыдущим блоком.
 * <p>
 * Поля:
 * - {@code index} — номер блока в цепочке (0 — первый блок),
 * - {@code timestamp} — момент времени создания блока,
 * - {@code transactions} — список транзакций в этом блоке,
 * - {@code previousHash} — хеш предыдущего блока,
 * - {@code nonce} — специальное число для майнинга (PoW),
 * - {@code hash} — хэш текущего блока.
 * <p>
 * Блоки являются неизменяемыми (immutable). Хэш вычисляется автоматически на основе содержимого блока.
 * Используются в цепочке блоков, которая хранится и синхронизируется между узлами.
 */

public record Block(long index, Instant timestamp, ImmutableList<Transaction> transactions, Hash previousHash,
                    long nonce, Hash hash) {

    public Block {
        if (timestamp == null || transactions == null || previousHash == null || hash == null) {
            throw new IllegalArgumentException("Block fields must not be null");
        }
    }
}
