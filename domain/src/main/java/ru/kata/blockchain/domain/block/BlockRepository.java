package ru.kata.blockchain.domain.block;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для хранения и доступа к блокам блокчейна.
 */
public interface BlockRepository {

    /**
     * Добавляет новый блок в цепочку и сохраняет всю цепочку в файл.
     */
    void save(Block block);

    /**
     * Возвращает текущую цепочку блоков
     */
    List<Block> findAll();

    /**
     * Возвращает последний добавленный блок.
     */
    Optional<Block> findLatest();
}
