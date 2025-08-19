package ru.kata.blockchain.domain.vo;

import java.util.regex.Pattern;

/**
 * Представляет хэш — неизменяемое строковое значение, состоящее из 64 шестнадцатеричных символов.
 * Хэш используется как уникальный идентификатор блока, транзакции или любого другого объекта,
 * чья неизменность должна быть подтверждена криптографически.
 * <p>
 * Поля:
 * {@code HEX_PATTERN} - регулярное выражение для проверки валидности хэша: 64 символа в шестнадцатеричном формате [0-9a-f]
 * {@code value} - строковое представление хэша
 * <p>
 * Этот класс иммутабелен: после создания значение нельзя изменить.
 */
public record Hash(String value) {
    static Pattern HEX_PATTERN = Pattern.compile("^[0-9a-f]{64}$");

    public Hash {
        if (value == null || !HEX_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid hash format: value must not be null or must be 64 hex characters [0-9a-f]");
        }
    }
}
