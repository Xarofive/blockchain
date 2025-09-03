package ru.kata.blockchain.infrastructure.dto;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Hash;

import java.time.Instant;
/**
 * DTO для передачи информации о блоке в блокчейне.
 * <p>
 * Используется на уровне API для передачи данных между слоями приложения.
 * <p>
 * Поля:
 * <ul>
 *     <li>{@code index} — порядковый номер блока в цепочке;</li>
 *     <li>{@code timestamp} — метка времени создания блока;</li>
 *     <li>{@code transactions} — список транзакций, включенных в блок;</li>
 *     <li>{@code previousHash} — хэш предыдущего блока;</li>
 *     <li>{@code nonce} — случайное число (для POW, по умолчанию 1).</li>
 * </ul>
 */
@Getter
@Setter
public class BlockDto {
    private long index;
    private Instant timestamp;
    private ImmutableList<Transaction> transactions;
    private Hash previousHash;
    private final long nonce = 1L;

    public BlockDto(long index, Instant timestamp, ImmutableList<Transaction> transactions, Hash previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
    }
}
