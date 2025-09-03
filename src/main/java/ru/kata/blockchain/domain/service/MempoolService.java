package ru.kata.blockchain.domain.service;

public interface MempoolService {
    boolean isMempoolContainsTxId(String txId);
}
