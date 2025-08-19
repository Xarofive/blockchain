package ru.kata.blockchain.domain.vo;

import lombok.NonNull;
import lombok.Value;

/**
 * Представляет адрес кошелька в системе блокчейн.
 * </p>
 * Поля:
 * {@code value} — строковое значение адреса кошелька, должно быть длиной более 30 символов и менее 128 символов.
 */
public record WalletAddress(String value) {
    static int MIN_LENGTH = 30;
    static int MAX_LENGTH = 128;

    public WalletAddress {
        if (value == null || value.length() <= MIN_LENGTH || value.length() >= MAX_LENGTH) {
            throw new IllegalArgumentException("WalletAddress must be longer 30 symbols and shorter 128 symbols");
        }
    }
}
