package ru.kata.blockchain.domain;

import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

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
 * Используется внутри блоков ({@link ru.kata.blockchain.domain.Block}), которые объединяют несколько транзакций.
 */

public record Transaction(WalletAddress from, WalletAddress to, Amount amount, byte[] signature, String publicKey) {
    public Transaction {
        if (from == null || to == null || amount == null || signature == null || publicKey == null) {
            throw new IllegalArgumentException("Transaction fields must not be null");
        }
    }

    /**
     * Проверяет, что транзакция подписана корректно, и её действительно отправил владелец.
     * Сейчас всегда возвращает true (заглушка),
     * в дальнейшем будет реализована проверка подписи
     */

    public boolean isValidSignature() {
        // TODO: implement digital signature verification
        return true;
    }
}
