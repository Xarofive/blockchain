package ru.kata.blockchain.infrastructure.service;

import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * Класс реализует сервис для сериализации данных транзакций.
 * <p>
 * Предоставляет методы для преобразования данных транзакции в массивы байтов, пригодные для дальнейшей обработки.
 * Используется в системе обработки транзакций для формирования сериализованного представления транзакций
 * и неподписанной полезной нагрузки.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Сериализация полной транзакции через {@link #getSerializedTransaction(Transaction)};</li>
 *     <li>Сериализация неподписанных данных транзакции через {@link #getUnassignedPayload(WalletAddress, WalletAddress, Amount)}.</li>
 * </ul>
 * <p>
 * Для формирования строкового представления данные объединяются с использованием разделителя {@code |},
 * который обеспечивает чёткое разделение полей (адресов отправителя и получателя, суммы, подписи и публичного ключа)
 * и предотвращает их слияние при сериализации. Полученные строки преобразуются в байты в кодировке UTF-8.
 *
 * @author cranstongit
 */
public class SerializerServiceImpl implements SerializerService {
    @Override
    public byte[] getSerializedTransaction(Transaction transaction) {
        return (transaction.from().value() + "|" + transaction.to().value() + "|" + transaction.amount().value() +
                "|" + Arrays.toString(transaction.signature()) + "|" + transaction.publicKey()).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] getUnassignedPayload(WalletAddress from, WalletAddress to, Amount amount) {
        return (from.value() + "|" + to.value() + "|" + amount.value()).getBytes(StandardCharsets.UTF_8);
    }
}