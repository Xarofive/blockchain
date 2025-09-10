package ru.kata.blockchain.infrastructure.service;

import ru.kata.blockchain.domain.service.MempoolService;

public class MempoolServiceImpl implements MempoolService {
    @Override
    public boolean isMempoolContainsTxId(String txId) {
        return false; // РЕАЛИЗОВАТЬ ЛОГИКУ
    }
}
