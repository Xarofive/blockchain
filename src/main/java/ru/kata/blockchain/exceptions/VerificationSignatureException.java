package ru.kata.blockchain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerificationSignatureException extends CryptoException {
    public VerificationSignatureException(String message) {
        super(message);
        log.error("VerificationSignatureException occurred: {}", message);
    }

    public VerificationSignatureException(String message, Throwable cause) {
        super(message, cause);
        log.error("VerificationSignatureException occurred: {}", message, this);
    }
}
