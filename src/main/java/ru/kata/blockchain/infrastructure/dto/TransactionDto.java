package ru.kata.blockchain.infrastructure.dto;

import java.security.PrivateKey;
import java.security.PublicKey;
/**
 * DTO для передачи данных транзакции.
 * <p>
 * Используется для создания новой транзакции и передачи информации о ней в сервисы блокчейна.
 * <p>
 * Поля:
 * <ul>
 *     <li>{@code from} — адрес отправителя;</li>
 *     <li>{@code to} — адрес получателя;</li>
 *     <li>{@code amount} — сумма перевода;</li>
 *     <li>{@code publicKey} — публичный ключ отправителя для проверки подписи.</li>
 * </ul>
 */
public record TransactionDto(String from, String to, long amount, PublicKey publicKey) {
}
