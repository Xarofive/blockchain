package ru.kata.blockchain.domain.vo;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletAddressTest {

    @Test
    void nullWalletAddressThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new WalletAddress(null));
    }

    @Test
    void shortWalletAddressThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new WalletAddress("null"));
    }

    @Test
    void longWalletAddressThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new WalletAddress(
                "nullnullnullnullnullnullnullnullnullnullnullnullnullnull" +
                        "nullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnull"));
    }

    @Test
    void createWalletAddressWithValidParameters() {
        final WalletAddress walletAddress = new WalletAddress("qwertyuiqwertyuiqwertyuiqwertyui");
        assertEquals("qwertyuiqwertyuiqwertyuiqwertyui", walletAddress.value());
    }
}