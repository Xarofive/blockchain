package ru.kata.blockchain.adapters.dto;

import java.security.PrivateKey;

/**
 * DTO для запроса на создание транзакции.
 * <p>
 * Содержит данные транзакции и приватный ключ отправителя, необходимый для подписи.
 * Используется на уровне API для передачи данных от клиента к серверу.
 */
public record CreateTransactionRequestDto(TransactionDto transactionDto, PrivateKey privateKey) {
}
