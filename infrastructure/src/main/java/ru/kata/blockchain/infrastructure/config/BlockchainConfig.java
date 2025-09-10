package ru.kata.blockchain.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kata.blockchain.application.usecase.AddBlockUseCase;
import ru.kata.blockchain.application.usecase.CreateTransactionUseCase;
import ru.kata.blockchain.application.usecase.ValidateTransactionUseCase;
import ru.kata.blockchain.domain.block.BlockRepository;
import ru.kata.blockchain.domain.crypto.CryptoService;
import ru.kata.blockchain.domain.service.*;
import ru.kata.blockchain.domain.validation.ValidationPolicy;
import ru.kata.blockchain.domain.vo.WalletAddress;
import ru.kata.blockchain.infrastructure.FileBlockRepository;
import ru.kata.blockchain.infrastructure.crypto.CryptoServiceImpl;
import ru.kata.blockchain.infrastructure.service.*;
import ru.kata.blockchain.infrastructure.validation.ValidationPolicyImpl;

import java.util.Set;

/**
 * Конфигурация Spring для блокчейн-приложения.
 * <p>
 * Регистрирует все необходимые бины для работы блокчейна:
 * репозиторий блоков, сервисы, правила валидации, а также use case для работы с транзакциями и блоками.
 */
@Configuration
public class BlockchainConfig {

    @Bean
    public BlockRepository blockRepository() {
        return new FileBlockRepository("blockchain.json");
    }

    @Bean
    public AccountStateService accountStateService() {
        return new AccountStateServiceImpl();
    }

    @Bean
    public AddressService addressService() {
        return new AddressServiceImpl(); // твоя реализация
    }

    @Bean
    public ValidationPolicy validationPolicy() {
        final long value = 1000L;
        final long txSize = 10000L;
        final Set<WalletAddress> blockedAddresses = Set.of();

        return new ValidationPolicyImpl(value, txSize, blockedAddresses);
    }

    @Bean
    public CryptoService cryptoService() {
        return new CryptoServiceImpl();
    }

    @Bean
    public MempoolService mempoolService() {
        return new MempoolServiceImpl();
    }

    @Bean
    public TxIdCalculatorService txIdCalculatorService() {
        return new TxIdCalculatorServiceImpl();
    }

    @Bean
    public SerializerService serializerService() {
        return new SerializerServiceImpl();
    }

    @Bean
    public AddBlockUseCase addBlockUseCase(BlockRepository blockRepository) {
        return new AddBlockUseCase(blockRepository);
    }

    @Bean
    public ValidateTransactionUseCase validateTransactionUseCase(AddressService addressService,
                                                                 ValidationPolicy validationPolicy,
                                                                 CryptoService cryptoService,
                                                                 MempoolService mempoolService,
                                                                 TxIdCalculatorService txIdCalculatorService,
                                                                 SerializerService serializerService,
                                                                 AccountStateService accountStateService) {
        return new ValidateTransactionUseCase(addressService,
                validationPolicy,
                cryptoService,
                mempoolService,
                txIdCalculatorService,
                serializerService,
                accountStateService);
    }

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(CryptoService cryptoService, SerializerService serializerService) {
        return new CreateTransactionUseCase(cryptoService, serializerService);
    }
}
