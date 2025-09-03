package ru.kata.blockchain.domain.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.kata.blockchain.domain.block.Block;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PublicKey;

/**
 * Представляет собой одну транзакцию (перевод средств от одного кошелька к другому) в блокчейне.
 * <p>
 * Каждая транзакция содержит:
 * - адрес отправителя {@link WalletAddress from},
 * - адрес получателя {@link WalletAddress to},
 * - сумму перевода {@link Amount amount},
 * - цифровую подпись отправителя (в виде массива байт),
 * - публичный ключ отправителя (нужен для проверки подписи).
 * <p>
 * Основная задача класса — хранить данные о переводе и уметь проверить
 * корректность подписи транзакции.
 * <p>
 * Используется внутри блоков ({@link Block}), которые объединяют несколько транзакций.
 */
public record Transaction(WalletAddress from, WalletAddress to, Amount amount, byte[] signature, PublicKey publicKey) {
    public Transaction {
        if (from == null || to == null || amount == null || signature == null || publicKey == null) {
            throw new IllegalArgumentException("Transaction fields must not be null");
        }
    }

    /**
     * В метод передается объект cryptoService для проверки подписи через verify()
     * В метод передается обеъект serializerService для получения unassignedPayload чере метод getUnassignedPayload()
     */
    @JsonIgnore
    public boolean isValidSignature(CryptoService cryptoService, SerializerService serializerService) {
        // TODO: перенести в бизнес-логику, чтобы не мешать модель с бизнес-логикой
        final byte[] unassignedPayload = serializerService.getUnassignedPayload(from, to, amount);
        return cryptoService.verify(unassignedPayload, signature, publicKey);
    }
}