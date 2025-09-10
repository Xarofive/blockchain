package ru.kata.blockchain.domain.service;

import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

public interface SerializerService {
    byte[] getSerializedTransaction(Transaction transaction);
    byte[] getUnassignedPayload(WalletAddress from, WalletAddress to, Amount amount);
}