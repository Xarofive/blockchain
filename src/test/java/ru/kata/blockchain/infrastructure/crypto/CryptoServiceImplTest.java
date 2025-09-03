package ru.kata.blockchain.infrastructure.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.kata.blockchain.exceptions.DataSignException;

import ru.kata.blockchain.exceptions.KeyGenerationException;
import ru.kata.blockchain.exceptions.VerificationSignatureException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class CryptoServiceImplTest {

    private CryptoServiceImpl cryptoService;

    @BeforeAll
    static void setupProvider() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl();
    }

    @Test
    void generateKeyPairsIsSuccessful() {
        final KeyPair keyPair = cryptoService.generateKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
    }

    @Test
    void whenKeyPairGeneratorFailsShouldThrowException() {
        try (MockedStatic<KeyPairGenerator> mocked = mockStatic(KeyPairGenerator.class)) {
            mocked.when(() -> KeyPairGenerator.getInstance("EC", "BC"))
                    .thenThrow(new RuntimeException("Simulated failure"));

            assertThrows(KeyGenerationException.class, () -> cryptoService.generateKeyPair());
        }
    }

    @Test
    void signAndVerifyIsSuccessful() {
        final KeyPair keyPair = cryptoService.generateKeyPair();
        final byte[] data = "test".getBytes();
        final byte[] signature = cryptoService.sign(data, keyPair.getPrivate());
        assertNotNull(signature);
        final boolean isValid = cryptoService.verify(data, signature, keyPair.getPublic());
        assertTrue(isValid);
    }

    @Test
    void signWithWrongKeyThrowsException() {
        final byte[] data = "test".getBytes();
        final PrivateKey invalidPrivateKey = mock(PrivateKey.class);
        assertThrows(DataSignException.class, () ->
                cryptoService.sign(data, invalidPrivateKey));
    }

    @Test
    void verifyWithTamperedDataReturnsFalse() {
        final KeyPair keyPair = cryptoService.generateKeyPair();
        final byte[] data = "test".getBytes();
        final byte[] signature = cryptoService.sign(data, keyPair.getPrivate());
        final byte[] tamperedData = "another test".getBytes();
        final boolean isValid = cryptoService.verify(tamperedData, signature, keyPair.getPublic());
        assertFalse(isValid);
    }

    @Test
    void verifyWithInvalidSignatureThrowsException() {
        final KeyPair keyPair = cryptoService.generateKeyPair();
        final byte[] data = "test".getBytes();
        final byte[] invalidSignature = new byte[]{1, 2, 3};
        assertThrows(VerificationSignatureException.class, () ->
                cryptoService.verify(data, invalidSignature, keyPair.getPublic()));
    }
}
