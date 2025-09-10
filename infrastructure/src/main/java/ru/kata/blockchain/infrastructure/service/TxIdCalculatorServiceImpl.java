package ru.kata.blockchain.infrastructure.service;

import ru.kata.blockchain.domain.service.TxIdCalculatorService;
import ru.kata.blockchain.domain.vo.Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Класс реализует сервис для вычисления идентификатора транзакции (TxId).
 * <p>
 * Предоставляет метод для расчёта идентификатора транзакции на основе переданного массива байтов
 * с использованием алгоритма SHA-256. Используется в системе обработки транзакций для генерации
 * уникальных идентификаторов транзакций.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Вычисление идентификатора транзакции через {@link #calculateTxId(byte[])};</li>
 *     <li>Внутренняя реализация хеширования SHA-256 через {@link #sha256(byte[])}.</li>
 * </ul>
 * <p>
 * Класс проверяет корректность формата хеша, требуя, чтобы результат был строкой из 64 шестнадцатеричных символов.
 *
 * @author cranstongit
 */
public class TxIdCalculatorServiceImpl implements TxIdCalculatorService {
    @Override
    public String calculateTxId(byte[] unassignedPayload) {
        final String txId = sha256(unassignedPayload);
        if (!Hash.getHexPattern().matcher(txId).matches()) {
            throw new IllegalArgumentException("Invalid hash format: value must not be null or must be 64 hex characters [0-9a-f]");
        }
        return txId;
    }

    /**
     * Метод выполняет хеширование массива байтов с использованием алгоритма SHA-256.
     * <p>
     * Преобразует входной массив байтов unassignedPayload в хеш с помощью {@code MessageDigest}, затем
     * преобразует результат в строку из шестнадцатеричных символов. Если длина
     * шестнадцатеричного представления байта равна 1, добавляется ведущий ноль.
     *
     * @param unassignedPayload массив байтов для хеширования
     * @return строка, представляющая SHA-256 хеш в шестнадцатеричном формате
     * @throws RuntimeException если алгоритм SHA-256 не поддерживается
     */
    private String sha256(byte[] unassignedPayload) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] bit256Hash = digest.digest(unassignedPayload);
            final StringBuilder hexString = new StringBuilder();
            for (byte b : bit256Hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }
}