package ru.kata.blockchain.domain.vo;


/**
 * Представляет количество средств, передаваемых в транзакцию.
 * </p>
 * Поля:
 * {@code value} — числовое значение суммы перевода, должно быть положительным.
 */
public record Amount(long value) {

    public Amount {
        if (value <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
