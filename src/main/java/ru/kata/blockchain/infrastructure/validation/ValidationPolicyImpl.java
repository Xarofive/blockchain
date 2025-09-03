package ru.kata.blockchain.infrastructure.validation;

import ru.kata.blockchain.domain.validation.ValidationPolicy;
import ru.kata.blockchain.domain.vo.Amount;
import ru.kata.blockchain.domain.vo.WalletAddress;

import java.util.Set;


/**
 * Класс реализует политику валидации транзакций.
 * <p>
 * Предоставляет методы для проверки адресов в списке запрещенных, получения максимальной суммы транзакции
 * и максимального размера сериализованной транзакции. Используется для обеспечения соблюдения
 * правил валидации в системе обработки транзакций.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Проверка, является ли адрес запрещённым через {@link #isForbiddenAddress(WalletAddress)};</li>
 *     <li>Получение максимальной суммы транзакции через {@link #getMaxAmount()};</li>
 *     <li>Получение максимального размера сериализованной транзакции через {@link #getMaxTxSize()}.</li>
 * </ul>
 * <p>
 * Класс инициализируется с параметрами:
 * <ul>
 *     <li>{@code value} — максимальная сумма транзакции (по умолчанию {@value #value});</li>
 *     <li>{@code txSize} — максимальный размер сериализованной транзакции (по умолчанию {@value #txSize});</li>
 *     <li>{@code blockedAddresses} — Set запрещённых адресов (либо пустой Set).</li>
 * </ul>
 *
 * @author cranstongit
 */
public class ValidationPolicyImpl implements ValidationPolicy {
    private long value = 1000;
    private long txSize = 10000;
    private Set<WalletAddress> blockedAddresses;

    public ValidationPolicyImpl(long value, long txSize, Set<WalletAddress> blockedAddresses) {
        this.value = value;
        this.txSize = txSize;
        if (blockedAddresses == null) {
            this.blockedAddresses = Set.of();
        } else {
            this.blockedAddresses = Set.copyOf(blockedAddresses);
        }
    }

    @Override
    public boolean isForbiddenAddress(WalletAddress address) {
        if (address == null) {
            return false;
        }
        return blockedAddresses.contains(address);
    }

    @Override
    public Amount getMaxAmount() {
        return new Amount(value);
    }

    @Override
    public long getMaxTxSize() {
        return txSize;
    }
}