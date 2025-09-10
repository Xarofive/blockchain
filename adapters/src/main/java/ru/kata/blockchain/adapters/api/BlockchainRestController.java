package ru.kata.blockchain.adapters.api;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.blockchain.application.usecase.AddBlockUseCase;
import ru.kata.blockchain.application.usecase.CreateTransactionUseCase;
import ru.kata.blockchain.application.usecase.ValidateTransactionUseCase;
import ru.kata.blockchain.domain.block.Block;
import ru.kata.blockchain.domain.block.BlockRepository;
import ru.kata.blockchain.domain.service.AccountStateService;
import ru.kata.blockchain.domain.transaction.Transaction;
import ru.kata.blockchain.domain.validation.ValidationResult;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;
import ru.kata.blockchain.adapters.dto.BlockDto;
import ru.kata.blockchain.adapters.dto.CreateTransactionRequestDto;
import ru.kata.blockchain.adapters.dto.TransactionDto;
import ru.kata.blockchain.adapters.factory.HashFactory;

import java.time.Instant;
import java.util.List;

/**
 * REST-контроллер для работы с блокчейном.
 * <p>
 * Данный контроллер предоставляет API для взаимодействия с системой:
 * <ul>
 *     <li>{@code POST /api/transaction} — создание транзакции, её валидация и добавление в новый блок.</li>
 *     <li>{@code GET /api/blockchain} — получение полной цепочки блоков.</li>
 *     <li>{@code GET /api/balance/{address}} — получение текущего баланса кошелька по адресу.</li>
 * </ul>
 *
 * - Контроллер использует DTO для обмена данными с клиентом: {@link CreateTransactionRequestDto}, {@link TransactionDto}, {@link BlockDto}.
 * <p>
 * - Внутри метода {@code createTransaction} формируется новый блок на основе последнего блока в цепочке.
 * <p>
 * - Для вычисления хэша блока используется {@link HashFactory}.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class BlockchainRestController {

    private final AddBlockUseCase addBlockUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final ValidateTransactionUseCase validateTransactionUseCase;
    private final AccountStateService accountStateService;
    private final BlockRepository blockRepository;

    @Autowired
    public BlockchainRestController(AddBlockUseCase addBlockUseCase,
                                    CreateTransactionUseCase createTransactionUseCase,
                                    ValidateTransactionUseCase validateTransactionUseCase,
                                    AccountStateService accountStateService,
                                    BlockRepository blockRepository) {
        this.addBlockUseCase = addBlockUseCase;
        this.createTransactionUseCase = createTransactionUseCase;
        this.validateTransactionUseCase = validateTransactionUseCase;
        this.accountStateService = accountStateService;
        this.blockRepository = blockRepository;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionRequestDto request) {
        log.info("Received transaction request: from={}, to={}, amount={}",
                request.transactionDto().from(), request.transactionDto().to(), request.transactionDto().amount());

        try {
            final TransactionDto transactionDto = request.transactionDto();
            final Transaction createdTransaction = createTransactionUseCase.createTransaction(
                    new WalletAddress(transactionDto.from()),
                    new WalletAddress(transactionDto.to()),
                    new Amount(transactionDto.amount()),
                    request.privateKey(),
                    transactionDto.publicKey()
            );
            log.debug("Transaction created successfully: {}", createdTransaction);

            final ValidationResult result = validateTransactionUseCase.validateTransaction(createdTransaction);
            if (!"OK".equals(result.status())) {
                log.warn("Transaction validation failed: {}", result);
                return ResponseEntity.badRequest().body(result);
            }
            log.debug("Transaction validation passed");

            final Block latest = blockRepository.findLatest().get();
            log.debug("Latest block retrieved: index={}", latest.index());

            final BlockDto blockDto = new BlockDto(latest.index() + 1, Instant.now(), ImmutableList.of(createdTransaction), latest.hash());
            final HashFactory hashFactory = new HashFactory();
            final Block newBlock = new Block(
                    blockDto.getIndex(),
                    blockDto.getTimestamp(),
                    blockDto.getTransactions(),
                    blockDto.getPreviousHash(),
                    blockDto.getNonce(),
                    hashFactory.createHash(blockDto)
            );
            log.debug("New block created: index={}", newBlock.index());

            addBlockUseCase.addBlock(newBlock);
            log.info("Block added successfully: index={}, hash={}", newBlock.index(), newBlock.hash());

            return ResponseEntity.ok(newBlock);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/blockchain")
    public ResponseEntity<List<Block>> getBlockchain() {
        log.info("Received request for full blockchain");
        final List<Block> blockchain = blockRepository.findAll();
        log.debug("Blockchain retrieved successfully: {}, blockchain: {}", blockchain,  blockchain.size());
        return ResponseEntity.ok(blockchain);
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<Double> getBalance(@PathVariable String address) {
        log.info("Received balance request for address: {}", address);
        final WalletAddress walletAddress = new WalletAddress(address);
        final double balance = accountStateService.getBalance(walletAddress);
        log.debug("Balance for address {}: {}", address, balance);
        return ResponseEntity.ok(balance);
    }
}
