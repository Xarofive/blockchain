package ru.kata.blockchain.domain.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionTest {

    private WalletAddress from;
    private WalletAddress to;
    private Amount amount;
    private byte[] signature;
    private PublicKey publicKey;
    private CryptoService cryptoService;
    private SerializerService serializerService;

    @BeforeEach
    void setUp() {
        from = new WalletAddress("qwertyuiqwertyuiqwertyuiqwertyui");
        to = new WalletAddress("nullnullnullnullnullnullnullnull");
        amount = new Amount(12345L);
        signature = new byte[]{1, 2, 3};
        publicKey = mock(PublicKey.class);
        cryptoService = mock(CryptoService.class);
        serializerService = mock(SerializerService.class);
    }

    @Test
    void createTransactionWithValidParameters() {
        final Transaction transaction = new Transaction(from, to, amount, signature, publicKey);
        assertNotNull(transaction);
        assertEquals(from, transaction.from());
        assertEquals(to, transaction.to());
        assertEquals(amount, transaction.amount());
        assertArrayEquals(signature, transaction.signature());
        assertEquals(publicKey, transaction.publicKey());
    }

    @Test
    void nullWalletAddressFromThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(null, to, amount, signature, publicKey));
    }

    @Test
    void nullWalletAddressToThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(from, null, amount, signature, publicKey));
    }

    @Test
    void nullAmountThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(from, to, null, signature, publicKey));
    }

    @Test
    void nullSignatureThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(from, to, amount, null, publicKey));
    }

    @Test
    void nullPublicKeyThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(from, to, amount, signature, null));
    }

    @Test
    void validSignatureShouldReturnTrue() {
        final Transaction transaction = new Transaction(from, to, amount, signature, publicKey);
        final byte[] unassignedPayload = new byte[]{4, 5, 6};

        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount)))
                .thenReturn(unassignedPayload);

        when(cryptoService.verify(eq(unassignedPayload), eq(signature), eq(publicKey)))
                .thenReturn(true);

        assertTrue(transaction.isValidSignature(cryptoService, serializerService));
        verify(serializerService).getUnassignedPayload(eq(from), eq(to), eq(amount));
        verify(cryptoService).verify(eq(unassignedPayload), eq(signature), eq(publicKey));
    }

    @Test
    void invalidSignatureShouldReturnFalse() {
        final Transaction transaction = new Transaction(from, to, amount, signature, publicKey);
        final byte[] unassignedPayload = new byte[]{4, 5, 6};

        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount)))
                .thenReturn(unassignedPayload);

        when(cryptoService.verify(any(byte[].class), eq(signature), eq(publicKey)))
                .thenReturn(false);

        assertFalse(transaction.isValidSignature(cryptoService, serializerService));
        verify(serializerService).getUnassignedPayload(eq(from), eq(to), eq(amount));
        verify(cryptoService).verify(eq(unassignedPayload), eq(signature), eq(publicKey));
    }
}