package ru.kata.blockchain.application.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.service.SerializerService;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateTransactionUseCaseTest {

    private WalletAddress from;
    private WalletAddress to;
    private Amount amount;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private SerializerService serializerService;

    @InjectMocks
    private CreateTransactionUseCase createTransactionUseCase;

    @BeforeEach
    void setUp() {
        from = new WalletAddress("some wallet address amount is coming from");
        to = new WalletAddress("some wallet address amount goes to");
        amount = new Amount(12345L);
        privateKey = mock(PrivateKey.class);
        publicKey = mock(PublicKey.class);
    }

    @Test
    void falseIfFromIsNull() {
        assertThrows(IllegalArgumentException.class, () -> createTransactionUseCase.createTransaction(null, to, amount, privateKey, publicKey));
    }

    @Test
    void falseIfToIsNull() {
        assertThrows(IllegalArgumentException.class, () -> createTransactionUseCase.createTransaction(from, null, amount, privateKey, publicKey));
    }

    @Test
    void falseIfAmountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> createTransactionUseCase.createTransaction(from, to, null, privateKey, publicKey));
    }

    @Test
    void falseIfPrivateKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> createTransactionUseCase.createTransaction(from, to, amount, null, publicKey));
    }

    @Test
    void falseIfPublicKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> createTransactionUseCase.createTransaction(from, to, amount, privateKey, null));
    }

    @Test
    void shouldThrowExceptionWhenSignatureIsInvalid() {
        final byte[] fakeSignature = new byte[]{1, 2, 3};
        final byte[] unassignedPayload = new byte[]{4, 5, 6};

        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(unassignedPayload);

        when(cryptoService.sign(eq(unassignedPayload), eq(privateKey))).thenReturn(fakeSignature);
        when(cryptoService.verify(eq(unassignedPayload), eq(fakeSignature), eq(publicKey))).thenReturn(false);
        assertThrows(IllegalStateException.class, () ->
                createTransactionUseCase.createTransaction(from, to, amount, privateKey, publicKey)
        );
        verify(serializerService, times(2)).getUnassignedPayload(eq(from), eq(to), eq(amount));
        verify(cryptoService).sign(eq(unassignedPayload), eq(privateKey));
        verify(cryptoService).verify(eq(unassignedPayload), eq(fakeSignature), eq(publicKey));
    }

    @Test
    void shouldCreateTransactionWhenSignatureIsValid() {
        final byte[] fakeSignature = new byte[]{1, 2, 3};
        final byte[] unassignedPayload = new byte[]{4, 5, 6};

        when(serializerService.getUnassignedPayload(eq(from), eq(to), eq(amount))).thenReturn(unassignedPayload);
        when(cryptoService.sign(eq(unassignedPayload), eq(privateKey))).thenReturn(fakeSignature);
        when(cryptoService.verify(eq(unassignedPayload), eq(fakeSignature), eq(publicKey))).thenReturn(true);

        final Transaction actual = createTransactionUseCase.createTransaction(from, to, amount, privateKey, publicKey);
        Assertions.assertEquals(from, actual.from());
        Assertions.assertEquals(to, actual.to());
        Assertions.assertEquals(amount, actual.amount());
        Assertions.assertArrayEquals(fakeSignature, actual.signature());
        Assertions.assertEquals(publicKey, actual.publicKey());

        verify(serializerService, times(2)).getUnassignedPayload(eq(from), eq(to), eq(amount));
        verify(cryptoService).sign(eq(unassignedPayload), eq(privateKey));
        verify(cryptoService).verify(eq(unassignedPayload), eq(fakeSignature), eq(publicKey));
    }
}