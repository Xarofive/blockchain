package ru.kata.blockchain.domain.service;

import ru.kata.blockchain.domain.vo.WalletAddress;

import java.security.PublicKey;

public interface AddressService {
    boolean deriveAddress(WalletAddress address, PublicKey publicKey);
}