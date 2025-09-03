package ru.kata.blockchain.infrastructure.crypto;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация криптопровайдера для Spring-контекста.
 *
 * Инициализирует BouncyCastle-провайдер при старте приложения через @PostConstruct.
 * Размещён в infrastructure т.к. относится к технической настройке среды.
 *
 * Провайдер требуется для работы CryptoServiceImpl и должен быть зарегистрирован
 * до использования любых криптографических операций.
 */
@Configuration
public class CryptoConfig {
    @PostConstruct
    public void initBouncyCastle() {
        CryptoProviderInitializer.initBouncyCastle();
    }
}
