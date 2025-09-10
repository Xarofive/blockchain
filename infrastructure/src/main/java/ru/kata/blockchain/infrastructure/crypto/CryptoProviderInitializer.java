package ru.kata.blockchain.infrastructure.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * Утилитарный класс для инициализации BouncyCastle-провайдера.
 *
 * Предоставляет статический метод для регистрации провайдера с самым высоким приоритетом - 1.
 * Может вызываться как из Spring-конфигурации, так и напрямую, например в тестах.
 *
 * Выделен отдельно для повторного использования и независимости от Spring,
 * чтобы гарантировать доступность провайдера в любых сценариях.
 */
public final class CryptoProviderInitializer {
    private CryptoProviderInitializer() {
    }

    public static void initBouncyCastle() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) != null) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        }
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}

