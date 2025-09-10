package ru.kata.blockchain.domain.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AmountTest {

    @Test
    void zeroAmountValueThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Amount(0));
    }

    @Test
    void negativeAmountValueThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Amount(-5));
    }

    @Test
    void positiveAmountValueCreateObject() {
        final Amount amount = new Amount(10);
        assertEquals(10, amount.value());
    }
}
