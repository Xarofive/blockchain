package ru.kata.blockchain.application.usecase;

import lombok.extern.slf4j.Slf4j;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PrivateKey;
import java.security.PublicKey;


/**
 * UseCase для создания транзакции
 * Класс обращается к интерфейсу домена CryptoService для получения подписи
 *
 * Подпись с privateKey
 * Подпись создается через интерфейс домена CryptoService и его реализацию в инфраструктуре
 *
 * Возврат Transaction
 * Возвращает объект транзакции с подписью
 *
 * Проверка корректности
 * Проверяем данные на null, проверка на положительность баланса в Amount, верификация подписи реализована
 *
 */
@Slf4j
public class CreateTransactionUseCase {
    private final CryptoService cryptoService;
    private final SerializerService serializerService;

    public CreateTransactionUseCase(CryptoService cryptoService, SerializerService serializerService) {
        this.cryptoService = cryptoService;
        this.serializerService = serializerService;
    }

    public Transaction createTransaction(WalletAddress from, WalletAddress to, Amount amount,
                                         PrivateKey privateKey, PublicKey publicKey) {
        log.info("Transaction creation attempt: from={}, to={}, amount={}", from, to, amount);

        if (from == null || to == null || amount == null || privateKey == null || publicKey == null) {
            log.error("Transaction creation error: one or more fields were null.");
            throw new IllegalArgumentException("Transaction fields must not be null");
        }

        log.debug("All transaction fields are present. Validation has been completed.");

        final byte[] signature = cryptoService.sign(serializerService.getUnassignedPayload(from, to, amount), privateKey);
        log.debug("The signature for the transaction has been successfully generated.");
        final Transaction transaction = new Transaction(from, to, amount, signature, publicKey);

        if (!transaction.isValidSignature(cryptoService, serializerService)) {
            log.error("Critical ERROR: The created transaction has an invalid signature! The transaction will be rejected.");
            throw new IllegalStateException("Invalid signature. Transaction rejected");
        }

        log.debug("Transaction signature verification was successful.");
        log.info("The transaction has been successfully created and verified.");

        return transaction;
    }
}