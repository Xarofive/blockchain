package ru.kata.blockchain.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.error.ErrorCodes;
import ru.kata.blockchain.domain.service.AccountStateService;
import ru.kata.blockchain.domain.service.AddressService;
import ru.kata.blockchain.domain.service.MempoolService;
import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.service.TxIdCalculatorService;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.validation.ValidationPolicy;
import ru.kata.blockchain.domain.validation.ValidationResult;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PublicKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidateTransactionUseCaseTest {
    private WalletAddress from;
    private WalletAddress to;
    private Amount amount;
    private byte[] signature;
    private PublicKey publicKey;
    private Transaction transaction;

    @Mock private AddressService addressService;
    @Mock private ValidationPolicy validationPolicy;
    @Mock private CryptoService cryptoService;
    @Mock private MempoolService mempoolService;
    @Mock private TxIdCalculatorService txIdCalculatorService;
    @Mock private SerializerService serializerService;
    @Mock private AccountStateService accountStateService;

    @InjectMocks
    private ValidateTransactionUseCase validateTransactionUseCase;

    @BeforeEach
    void setUp() {
        from = new WalletAddress("some wallet address amount is coming from");
        to = new WalletAddress("some wallet address amount goes to");
        amount = new Amount(1234L);
        signature = new byte[]{1, 2, 3};
        publicKey = mock(PublicKey.class);
        transaction = new Transaction(from, to, amount, signature, publicKey);
    }

    @Test
    void shouldReturnSuccessWhenAllValidationsPass() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(2000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("OK", result.status());
        assertEquals("tx123", result.txId());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void shouldReturnErrorWhenFromAddressIsInvalid() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(false);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(2000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.INVALID_ADDRESS_FROM), result.errors());
    }

    @Test
    void shouldReturnErrorWhenToAddressIsForbidden() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(true);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(2000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.FORBIDDEN_ADDRESS), result.errors());
    }

    @Test
    void shouldReturnErrorWhenAmountIsTooLarge() {
        final Transaction largeTx = new Transaction(from, to, new Amount(20000L), signature, publicKey);
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(largeTx))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(new Amount(20000L)))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(30000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(largeTx);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.AMOUNT_TOO_LARGE), result.errors());
    }

    @Test
    void shouldReturnErrorWhenTransactionSizeIsTooLarge() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[1500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(2000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.TX_TOO_LARGE), result.errors());
    }

    @Test
    void shouldReturnErrorWhenSignatureIsInvalid() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(false);
        when(accountStateService.getBalance(eq(from))).thenReturn(2000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.INVALID_SIGNATURE), result.errors());
    }

    @Test
    void shouldReturnErrorWhenInsufficientFunds() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(500.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(false);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.INSUFFICIENT_FUNDS), result.errors());
    }

    @Test
    void shouldReturnErrorWhenTransactionIsDuplicate() {
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(true);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(false);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(transaction))).thenReturn(new byte[500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(true);
        when(accountStateService.getBalance(eq(from))).thenReturn(2000.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(0.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(true);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(transaction);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(ErrorCodes.DUPLICATE_TRANSACTION), result.errors());
    }

    @Test
    void shouldReturnMultipleErrorsWhenMultipleValidationsFail() {
        final Transaction invalidTx = new Transaction(from, to, new Amount(1L), signature, publicKey);
        when(addressService.deriveAddress(eq(from), eq(publicKey))).thenReturn(false);
        when(validationPolicy.isForbiddenAddress(eq(to))).thenReturn(true);
        when(validationPolicy.getMaxAmount()).thenReturn(new Amount(10000L));
        when(validationPolicy.getMaxTxSize()).thenReturn(1000L);
        when(serializerService.getSerializedTransaction(eq(invalidTx))).thenReturn(new byte[1500]);
        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(new Amount(1L)))).thenReturn(new byte[]{4, 5, 6});
        when(cryptoService.verify(eq(new byte[]{4, 5, 6}), eq(signature), eq(publicKey))).thenReturn(false);
        when(accountStateService.getBalance(eq(from))).thenReturn(500.0);
        when(accountStateService.getPendingAmount(eq(from))).thenReturn(500.0);
        when(txIdCalculatorService.calculateTxId(eq(new byte[]{4, 5, 6}))).thenReturn("tx123");
        when(mempoolService.isMempoolContainsTxId(eq("tx123"))).thenReturn(true);

        final ValidationResult result = validateTransactionUseCase.validateTransaction(invalidTx);

        assertEquals("ERROR", result.status());
        assertNull(result.txId());
        assertEquals(List.of(
                ErrorCodes.INVALID_ADDRESS_FROM,
                ErrorCodes.FORBIDDEN_ADDRESS,
                // ErrorCodes.AMOUNT_NOT_POSITIVE, the error is commented, because the check is in the Amount constructor
                ErrorCodes.TX_TOO_LARGE,
                ErrorCodes.INVALID_SIGNATURE,
                ErrorCodes.INSUFFICIENT_FUNDS,
                ErrorCodes.DUPLICATE_TRANSACTION
        ), result.errors());
    }
}