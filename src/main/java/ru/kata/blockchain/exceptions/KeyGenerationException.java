package ru.kata.blockchain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyGenerationException extends CryptoException {

    public KeyGenerationException(String message) {
        super(message);
        log.error("KeyGenerationException occurred: {}", message);
    }

    public KeyGenerationException(String message, Throwable cause) {
        super(message, cause);
        log.error("KeyGenerationException occurred: {}", message, this);
    }
}
