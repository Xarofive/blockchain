package ru.kata.blockchain.domain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSignException extends CryptoException {

    public DataSignException(String message) {
        super(message);
        log.error("DataSignException occurred: {}", message);
    }

    public DataSignException(String message, Throwable cause) {
        super(message, cause);
        log.error("DataSignException occurred: {}", message, cause);
    }
}
