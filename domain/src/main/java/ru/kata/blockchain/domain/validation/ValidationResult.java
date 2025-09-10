package ru.kata.blockchain.domain.validation;

import ru.kata.blockchain.domain.error.ErrorCodes;

import java.util.List;

public record ValidationResult(String status, String txId, List<ErrorCodes> errors) {
}
