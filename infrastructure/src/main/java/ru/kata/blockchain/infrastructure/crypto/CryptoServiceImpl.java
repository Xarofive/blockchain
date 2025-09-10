package ru.kata.blockchain.infrastructure.crypto;

import org.springframework.stereotype.Service;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.exceptions.DataSignException;
import ru.kata.blockchain.domain.exceptions.KeyGenerationException;
import ru.kata.blockchain.domain.exceptions.VerificationSignatureException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

/**
 * Через статический блок добавлен BouncyCastle в качестве провайдера
 *
 * generateKeyPair()
 * Реализован метод generateKeyPair() с использованием BouncyCastle и secp256k1
 *
 * sign(byte[] data, PrivateKey privateKey)
 * verify(byte[] data, byte[] signature, PublicKey publicKey)
 * Реализованы методы sign и verify для подписания транзакций и валидации подписи
 */
@Service
public class CryptoServiceImpl implements CryptoService {
    @Override
    public KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
            keyPairGenerator.initialize(new ECGenParameterSpec("secp256k1"), new SecureRandom());
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new KeyGenerationException("Key pair generation failed", e);
        }
    }

    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) { //TODO:описание что за дата? для чего подпись? что делает метод?
        try {
            final Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new DataSignException("Signing failed", e);
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            final Signature sign = Signature.getInstance("SHA256withECDSA", "BC");
            sign.initVerify(publicKey);
            sign.update(data);
            return sign.verify(signature);
        } catch (Exception e) {
            throw new VerificationSignatureException("Signature verification failed", e);
        }
    }
}

