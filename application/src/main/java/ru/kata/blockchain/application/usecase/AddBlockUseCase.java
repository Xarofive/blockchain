package ru.kata.blockchain.application.usecase;

import lombok.extern.slf4j.Slf4j;
import ru.kata.blockchain.domain.block.BlockRepository;
import ru.kata.blockchain.domain.block.Block;

/**
 * Use case для добавления нового блока в блокчейн.
 * <p>
 * Проверяет корректность предыдущего хеша и индекса нового блока
 * перед сохранением его в репозиторий.
 * </p>
 *
 * <p><b>Правила валидации:</b></p>
 * <ul>
 *     <li>Поле {@code previousHash} нового блока должно совпадать с {@code hash} последнего блока.</li>
 *     <li>Индекс нового блока должен быть на единицу больше индекса последнего блока.</li>
 * </ul>
 *
 * <p>
 * Если условия выполняются — блок сохраняется в {@link BlockRepository}, иначе — игнорируется.
 * </p>
 */
@Slf4j
public class AddBlockUseCase {
    private final BlockRepository blockRepository;

    public AddBlockUseCase(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public void addBlock(Block block) {
        log.info("Attempt to add a new block with the index: {}", block.index());
        final Block latestBlock = blockRepository.findLatest().get();
        log.debug("Last block in the chain: index={}, hash={}", latestBlock.index(), latestBlock.hash());
        if (latestBlock.hash().equals(block.previousHash()) &&
                latestBlock.index() + 1 == block.index()) {
            blockRepository.save(block);
            log.info("The block with the index {} has been successfully validated and saved.", block.index());
        }
    }
}
