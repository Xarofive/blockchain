package ru.kata.blockchain.domain.service;

import ru.kata.blockchain.domain.vo.WalletAddress;

public interface AccountStateService {
    double getBalance(WalletAddress walletAddress);
    double getPendingAmount(WalletAddress walletAddress);
}
