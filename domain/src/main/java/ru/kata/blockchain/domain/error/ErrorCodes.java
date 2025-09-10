package ru.kata.blockchain.domain.error;


/**
 * Enum `ErrorCodes` определяет набор ошибок, возникающих при валидации транзакций.
 * <p>
 * Содержит константы ошибок с уникальными числовыми кодами и описаниями на английском языке.
 * Используется для идентификации и обработки ошибок в процессе валидации транзакций.
 * <p>
 * Константы и их назначение:
 * <ul>
 *     <li><b>INVALID_ADDRESS_FROM</b> (1001): Неверный формат адреса отправителя;</li>
 *     <li><b>INVALID_ADDRESS_TO</b> (1002): Неверный формат адреса получателя;</li>
 *     <li><b>FORBIDDEN_ADDRESS</b> (1003): Адрес получателя находится в стоп-листе;</li>
 *     <li><b>AMOUNT_NOT_POSITIVE</b> (1004): Сумма транзакции не является положительной;</li>
 *     <li><b>AMOUNT_TOO_LARGE</b> (1005): Сумма транзакции превышает установленный лимит;</li>
 *     <li><b>TX_TOO_LARGE</b> (1006): Размер сериализованной транзакции превышает лимит;</li>
 *     <li><b>PUBLIC_KEY_MISMATCH</b> (1007): Адрес отправителя не соответствует публичному ключу;</li>
 *     <li><b>INVALID_SIGNATURE</b> (1008): Цифровая подпись транзакции неверна;</li>
 *     <li><b>DUPLICATE_TRANSACTION</b> (1009): Транзакция уже присутствует в мемпуле;</li>
 *     <li><b>INSUFFICIENT_FUNDS</b> (1010): Недостаточно средств на счету отправителя.</li>
 * </ul>
 * <p>
 * Поля:
 * <ul>
 *     <li><b>code</b>: Уникальный числовой идентификатор ошибки;</li>
 *     <li><b>message</b>: Текстовое описание ошибки на английском языке.</li>
 * </ul>
 * <p>
 * Методы:
 * <ul>
 *     <li><b>getCode()</b>: Возвращает числовой код ошибки;</li>
 *     <li><b>getMessage()</b>: Возвращает текстовое описание ошибки.</li>
 * </ul>
 * Примечания:
 * <ul>
 *     <li>Перечисление неизменяемо, новые ошибки добавляются только через изменение кода;</li>
 * </ul>
 */
public enum ErrorCodes {
    INVALID_ADDRESS_FROM(1001, "Invalid address format for sender"),
    INVALID_ADDRESS_TO(1002, "Invalid address format for recipient"),
    FORBIDDEN_ADDRESS(1003, "Recipient address is in the blocklist"),
    AMOUNT_NOT_POSITIVE(1004, "Transaction amount must be positive"),
    AMOUNT_TOO_LARGE(1005, "Transaction amount exceeds the limit"),
    TX_TOO_LARGE(1006, "Transaction size exceeds the limit"),
    PUBLIC_KEY_MISMATCH(1007, "Sender address does not match public key"),
    INVALID_SIGNATURE(1008, "Invalid transaction signature"),
    DUPLICATE_TRANSACTION(1009, "Transaction already exists in mempool"),
    INSUFFICIENT_FUNDS(1010, "Insufficient funds for transaction");

    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}