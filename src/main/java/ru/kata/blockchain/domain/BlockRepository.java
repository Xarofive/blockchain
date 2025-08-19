package ru.kata.blockchain.domain;

import java.util.List;

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
    Block findLatest();
}
