package ru.kata.blockchain.infrastructure.service;

import ru.kata.blockchain.domain.service.AddressService;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PublicKey;

public class AddressServiceImpl implements AddressService {
    @Override
    public boolean deriveAddress(WalletAddress walletAddress, PublicKey publicKey) {
        return true; // TODO: add logic
    }
}
