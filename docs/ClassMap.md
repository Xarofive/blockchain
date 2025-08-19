Примерная карта классов

📦 domain
Это самая чистая часть — никаких Spring, JSON, SQL.

✅ Entities / Value Objects

Класс	Для чего
Block	хеш, prevHash, список транзакций, nonce, timestamp
Transaction	from, to, amount, signature
WalletAddress (VO)	publicKey или хеш
Amount (VO)	value, currency (или просто long)
Hash (VO)	SHA-256
Signature (VO)	подпись

✅ Интерфейсы

Интерфейс	Для чего
BlockRepository	сохранить / получить блок
BlockchainService	проверить цепочку, longest chain
TransactionValidator	проверить подпись и баланс

🚀 application
Тут use cases — бизнес-сценарии.

✅ Use Cases

Класс	Для чего
AddBlockUseCase	добавить новый блок в цепочку
CreateTransactionUseCase	создать и подписать транзакцию
ValidateTransactionUseCase	проверить баланс и подпись
SyncBlockchainUseCase	подтянуть цепочку от других нод

🔌 infrastructure
Реализация интерфейсов из domain.

✅ Реализации

Класс	Для чего
FileBlockRepository	хранит цепочку блоков в JSON
CryptoService	подписи / валидация (BouncyCastle)
RestBlockchainClient	общение с другими нодами
InMemoryTransactionPool	хранение неподтверждённых транзакций

🌐 adapters
Это входные точки (например REST API).

✅ Контроллеры

Класс	Для чего
TransactionController	POST /transaction
BlockchainController	GET /blockchain
WalletController	генерировать кошельки, показать баланс

✅ (позже) WebSocket или KafkaConsumer

Пример итоговой схемы классов

ru.kata.blockchain
│
├── domain
│   ├── Block
│   ├── Transaction
│   ├── WalletAddress
│   ├── Amount
│   ├── Hash
│   ├── BlockRepository
│   ├── BlockchainService
│   └── TransactionValidator
│
├── application
│   ├── AddBlockUseCase
│   ├── CreateTransactionUseCase
│   ├── ValidateTransactionUseCase
│   └── SyncBlockchainUseCase
│
├── infrastructure
│   ├── FileBlockRepository
│   ├── CryptoService
│   └── RestBlockchainClient
│
└── adapters
├── TransactionController
├── BlockchainController
└── WalletController
