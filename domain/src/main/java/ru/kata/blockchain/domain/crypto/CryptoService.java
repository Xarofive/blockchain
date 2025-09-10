package ru.kata.blockchain.domain.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoService {
    KeyPair generateKeyPair();
    byte[] sign(byte[] data, PrivateKey privateKey);
    boolean verify(byte[] data, byte[] signature, PublicKey publicKey);
}
