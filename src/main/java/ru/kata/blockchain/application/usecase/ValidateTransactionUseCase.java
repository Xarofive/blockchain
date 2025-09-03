package ru.kata.blockchain.application.usecase;

import lombok.extern.slf4j.Slf4j;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.error.ErrorCodes;
import ru.kata.blockchain.domain.service.AccountStateService;
import ru.kata.blockchain.domain.service.AddressService;
import ru.kata.blockchain.domain.service.MempoolService;
import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.service.TxIdCalculatorService;
import ru.kata.blockchain.domain.validation.ValidationPolicy;
import ru.kata.blockchain.domain.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

import static ru.kata.blockchain.domain.error.ErrorCodes.AMOUNT_NOT_POSITIVE;
import static ru.kata.blockchain.domain.error.ErrorCodes.AMOUNT_TOO_LARGE;
import static ru.kata.blockchain.domain.error.ErrorCodes.DUPLICATE_TRANSACTION;
import static ru.kata.blockchain.domain.error.ErrorCodes.FORBIDDEN_ADDRESS;
import static ru.kata.blockchain.domain.error.ErrorCodes.INSUFFICIENT_FUNDS;
import static ru.kata.blockchain.domain.error.ErrorCodes.INVALID_ADDRESS_FROM;
import static ru.kata.blockchain.domain.error.ErrorCodes.INVALID_SIGNATURE;
import static ru.kata.blockchain.domain.error.ErrorCodes.TX_TOO_LARGE;


/**
 * Класс, отвечающий за валидацию транзакций с учетом заданных правил и состояний.
 * <p>
 * Выполняет проверки:
 * <ul>
 *     <li>проверка адресов from и to не проводится, адреса проверяются на этапе создания;</li>
 *     <li>проверка валидности адреса по PublicKey через порт AddressService;</li>
 *     <li>проверка отсутствия адрес в списке запрещенный через порт ValidationPolicy;</li>
 *     <li>проверка корректности суммы транзакции, сумма больше нуля;</li>
 *     <li>проверка корректности суммы транзакции, сумма не должна превышать лимит в ValidationPolicy (порт);</li>
 *     <li>сериализация транзакции через порт serializerService и проверка, что транзакция не превышает лимит в ValidationPolicy (порт);</li>
 *     <li>проверка цифровой подписи транзакции через порты serializerService и cryptoService;</li>
 *     <li>проверка наличия достаточного баланса на счету отправителя через порт accountStateService;</li>
 *     <li>отсутствия дубликатов транзакций в памяти мемпула через порты serializerService и txIdCalculatorService.</li>
 * </ul>
 * <p>
 * Использует внешние сервисы для проверки адресов, политики валидации, криптографии,
 * сериализации, состояния аккаунтов и управления мемпулом.
 * Проверка возвращает объект ValidationResult со статусом и TxId/Списком ошибок.
 */
@Slf4j
public class ValidateTransactionUseCase {
    private final AddressService addressService;
    private final ValidationPolicy validationPolicy;
    private final CryptoService cryptoService;
    private final MempoolService mempoolService;
    private final TxIdCalculatorService txIdCalculatorService;
    private final SerializerService serializerService;
    private final AccountStateService accountStateService;

    public ValidateTransactionUseCase(
            AddressService addressService,
            ValidationPolicy validationPolicy,
            CryptoService cryptoService,
            MempoolService mempoolService,
            TxIdCalculatorService txIdCalculatorService,
            SerializerService serializerService,
            AccountStateService accountStateService
    ) {
        this.addressService = addressService;
        this.validationPolicy = validationPolicy;
        this.cryptoService = cryptoService;
        this.mempoolService = mempoolService;
        this.txIdCalculatorService = txIdCalculatorService;
        this.serializerService = serializerService;
        this.accountStateService = accountStateService;
    }

    public ValidationResult validateTransaction(Transaction transaction) {
        log.info("Start of transaction validation: from={}, to={}, amount={}",
                transaction.from(), transaction.to(), transaction.amount());
        final List<ErrorCodes> errors = new ArrayList<>();

        log.debug("Address verification");
        if (!addressService.deriveAddress(transaction.from(), transaction.publicKey())) { // ЗАГЛУШКА. РЕАЛИЗОВАТЬ ЛОГИКУ
            log.warn("Invalid sender's address: does not match the public key.");
            errors.add(INVALID_ADDRESS_FROM);
        }

        if (validationPolicy.isForbiddenAddress(transaction.to())) {
            log.warn("The recipient's address {} is in the prohibited list.", transaction.to());
            errors.add(FORBIDDEN_ADDRESS);
        }

        log.debug("validation of the transaction amount");
        if (transaction.amount().value() <= 0) {
            log.warn("The transaction amount is not positive: {}", transaction.amount().value());
            errors.add(AMOUNT_NOT_POSITIVE);
        }
        if (transaction.amount().value() > validationPolicy.getMaxAmount().value()) {
            log.warn("The transaction amount {} exceeds the maximum limit {}.",
                    transaction.amount().value(), validationPolicy.getMaxAmount().value());
            errors.add(AMOUNT_TOO_LARGE);
        }

        log.debug("Checking the transaction size");
        final byte[] serializedTx = serializerService.getSerializedTransaction(transaction);
        if (serializedTx.length > validationPolicy.getMaxTxSize()) {
            log.warn("Transaction size {} bytes exceeds the maximum limit {} bytes.",
                    serializedTx.length, validationPolicy.getMaxTxSize());
            errors.add(TX_TOO_LARGE);
        }

        log.debug("Digital signature verification");
        final byte[] unassignedPayload = serializerService.getUnassignedPayload(transaction.from(), transaction.to(), transaction.amount());
        if (!cryptoService.verify(unassignedPayload, transaction.signature(), transaction.publicKey())) {
            log.warn("The digital signature of the transaction is incorrect.");
            errors.add(INVALID_SIGNATURE);
        }

        log.debug("Checking sender's balance");
        final double balance = accountStateService.getBalance(transaction.from()); // ЗАГЛУШКА. РЕАЛИЗОВАТЬ ЛОГИКУ
        final double pending = accountStateService.getPendingAmount(transaction.from()); // ЗАГЛУШКА. РЕАЛИЗОВАТЬ ЛОГИКУ
        if ((balance - pending) < transaction.amount().value()) {
            log.warn("The sender does not have enough funds {}. Available: {}, required: {}",
                    transaction.from(), (balance - pending), transaction.amount().value());
            errors.add(INSUFFICIENT_FUNDS);
        }

        log.debug("Checking for duplicates in the mempool");
        final String txId = txIdCalculatorService.calculateTxId(unassignedPayload);
        if (mempoolService.isMempoolContainsTxId(txId)) {
            log.warn("Duplicate transaction detected with TxId: {}", txId);
            errors.add(DUPLICATE_TRANSACTION);
        }

        if (errors.isEmpty()) {
            log.info("The transaction has successfully passed all checks. TxId: {}", txId);
            return new ValidationResult("OK", txId, List.of()); //TODO: сделать Enum статуса
        } else {
            log.warn("The transaction was not validated. Errors found: {}", errors);
            return new ValidationResult("ERROR", null, errors);
        }
    }
}
