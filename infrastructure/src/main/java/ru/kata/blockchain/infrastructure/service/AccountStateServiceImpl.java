package ru.kata.blockchain.infrastructure.service;

import ru.kata.blockchain.domain.service.AccountStateService;
import ru.kata.blockchain.domain.vo.WalletAddress;


public class AccountStateServiceImpl implements AccountStateService {
    @Override
    public double getBalance(WalletAddress walletAddress) {
        return 2000; // РЕАЛИЗОВАТЬ ЛОГИКУ
    }
    @Override
    public double getPendingAmount(WalletAddress walletAddress) {
        return 0; // РЕЛИЗОВАТЬ ЛОГИКУ
    }
}
