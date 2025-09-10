package ru.kata.blockchain.adapters.factory;

import ru.kata.blockchain.domain.vo.Hash;
import ru.kata.blockchain.adapters.dto.BlockDto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Фабрика для генерации криптографических хэшей блоков.
 * <p>
 * Назначение класса — создание объекта {@link Hash},
 * который представляет собой SHA-256 хэш, вычисленный на
 * основании данных блока.
 *
 * <p>Работа метода {@link #createHash(BlockDto)}:</p>
 * <ol>
 *     <li>Из объекта {@link BlockDto} берутся поля:
 *     index, timestamp, previousHash, nonce.</li>
 *     <li>Значения этих полей объединяются в хэш-число с помощью {@link Objects#hash(Object...)}.</li>
 *     <li>Результат дополнительно хэшируется алгоритмом {@code SHA-256}.</li>
 *     <li>Байтовый массив переводится в шестнадцатеричную строку длиной 64 символа.</li>
 *     <li>Возвращается объект {@link Hash}.</li>
 * </ol>
 */
public class HashFactory {

    public Hash createHash(BlockDto blockDto) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final Integer newHash = Objects.hash(blockDto.getIndex(), blockDto.getTimestamp(), blockDto.getTimestamp(),
                    blockDto.getPreviousHash(), blockDto.getNonce());
            final byte[] hashBytes = digest.digest(newHash.toString().getBytes());

            final StringBuilder stringBuilderToHexHash = new StringBuilder();
            for (byte b : hashBytes) {
                stringBuilderToHexHash.append(String.format("%02x", b));
            }
            return new Hash(stringBuilderToHexHash.toString());

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}

