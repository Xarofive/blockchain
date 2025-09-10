package ru.kata.blockchain.domain.service;


public interface TxIdCalculatorService {
    String calculateTxId(byte[] unassignedPayload);
}
