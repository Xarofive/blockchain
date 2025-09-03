package ru.kata.blockchain.domain.validation;

import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

public interface ValidationPolicy {
    boolean isForbiddenAddress(WalletAddress address);
    Amount getMaxAmount();
    long getMaxTxSize();
}