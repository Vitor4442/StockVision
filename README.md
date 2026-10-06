# StockVision — Inventory Service

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge\&logo=postgresql\&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-231F20?style=for-the-badge\&logo=apachekafka\&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24.x-2496ED?style=for-the-badge\&logo=docker\&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=for-the-badge\&logo=flyway\&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct-1.6.x-ED8B00?style=for-the-badge)

Microserviço responsável pelo gerenciamento de estoque e registro de movimentações de inventário do **StockVision**, uma plataforma de monitoramento de estoque em tempo real utilizando processamento de imagens e câmeras.

---

## Arquitetura

O StockVision utiliza uma arquitetura baseada em **microsserviços e comunicação orientada a eventos**.

![Arquitetura do StockVision](docs/architecture/stockvision-architecture.png)

### Fluxo de movimentação

```text
Camera
   │
   ▼
Inventory Service
   │
   ├── PostgreSQL
   │
   └── Kafka
         │
         ▼
    Stock Service
         │
         ▼
   Atualização do estoque
```

A movimentação é inicialmente registrada pelo **Inventory Service**. Após persistir a operação, um evento `StockMovementCreatedEvent` é publicado no Kafka.

O **Stock Service** consome esse evento e atualiza o estoque correspondente de forma assíncrona.

---

## Tecnologias

| Tecnologia      | Utilização                    |
| --------------- | ----------------------------- |
| Java            | Linguagem principal           |
| Spring Boot     | Framework principal           |
| Spring Data JPA | Persistência                  |
| Spring Kafka    | Comunicação assíncrona        |
| PostgreSQL      | Banco de dados                |
| Flyway          | Migrations                    |
| MapStruct       | Mapeamento DTO ↔ Entity       |
| Lombok          | Redução de boilerplate        |
| Docker          | Containerização               |
| Maven           | Gerenciamento de dependências |

---
