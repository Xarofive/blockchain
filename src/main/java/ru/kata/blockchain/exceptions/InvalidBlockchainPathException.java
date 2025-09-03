package ru.kata.blockchain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidBlockchainPathException extends CryptoException {

    public InvalidBlockchainPathException(String message) {
        super(message);
        log.error("InvalidBlockchainPathException occurred: {}", message);
    }

    public InvalidBlockchainPathException(String message, Throwable cause) {
        super(message, cause);
        log.error("InvalidBlockchainPathException occurred: {}", message, this);
    }
}
