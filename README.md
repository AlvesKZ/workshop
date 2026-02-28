# Workshop API

API RESTful desenvolvida com Spring Boot para gerenciamento de pedidos, produtos, categorias e usuários.

## Tecnologias

- Java 25
- Spring Boot 4.0.2
- Spring Data JPA / Hibernate
- PostgreSQL 17 (perfil `dev`)
- H2 Database (perfil `test`)
- Maven

## Modelo de Domínio

```
User ──< Order >── OrderItem >── Product >── Category
              └── Payment
```

| Entidade    | Tabela               | Descrição                                        |
|-------------|----------------------|--------------------------------------------------|
| `User`      | `tb_user`            | Usuário/cliente da plataforma                    |
| `Order`     | `tb_order`           | Pedido realizado por um usuário                  |
| `OrderItem` | —                    | Item de um pedido (produto + quantidade + preço) |
| `Product`   | `tb_product`         | Produto disponível para compra                   |
| `Category`  | —                    | Categoria de produtos (relação N:N com produto)  |
| `Payment`   | —                    | Pagamento associado a um pedido (1:1)            |

### Status do Pedido (`OrderStatus`)

| Código | Status          |
|--------|-----------------|
| 1      | WAITING_PAYMENT |
| 2      | PAID            |
| 3      | SHIPPED         |
| 4      | DELIVERED       |
| 5      | CANCELED        |

## Endpoints

### Usuários — `/users`

| Método   | Endpoint       | Descrição         | Status |
|----------|----------------|-------------------|--------|
| `GET`    | `/users`       | Listar todos      | 200    |
| `GET`    | `/users/{id}`  | Buscar por ID     | 200    |
| `POST`   | `/users`       | Criar usuário     | 201    |
| `PUT`    | `/users/{id}`  | Atualizar usuário | 200    |
| `DELETE` | `/users/{id}`  | Deletar usuário   | 204    |

### Pedidos — `/orders`

| Método | Endpoint        | Descrição     | Status |
|--------|-----------------|---------------|--------|
| `GET`  | `/orders`       | Listar todos  | 200    |
| `GET`  | `/orders/{id}`  | Buscar por ID | 200    |

### Produtos — `/products`

| Método | Endpoint          | Descrição     | Status |
|--------|-------------------|---------------|--------|
| `GET`  | `/products`       | Listar todos  | 200    |
| `GET`  | `/products/{id}`  | Buscar por ID | 200    |

### Categorias — `/categories`

| Método | Endpoint            | Descrição     | Status |
|--------|---------------------|---------------|--------|
| `GET`  | `/categories`       | Listar todas  | 200    |
| `GET`  | `/categories/{id}`  | Buscar por ID | 200    |

## Perfis de Configuração

O perfil ativo é definido em `application.properties`:

```properties
spring.profiles.active=dev
```

| Perfil | Banco         | Uso                       |
|--------|---------------|---------------------------|
| `dev`  | PostgreSQL 17 | Desenvolvimento local     |
| `test` | H2 (memória)  | Testes / validação rápida |

### Perfil `test` — H2 Console

Ao rodar com o perfil `test`, o console do H2 fica disponível em:

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Usuário:  sa
Senha:    (vazia)
```

## Como Executar

### Pré-requisitos

- Java 25+
- Maven
- Docker (para o perfil `dev`)

### 1. Subir o banco de dados

```bash
docker compose up -d
```

O `docker-compose.yaml` já está configurado com as credenciais do perfil `dev`:

```yaml
services:
  postgres:
    image: postgres:17
    container_name: postgres
    environment:
      POSTGRES_DB: workshop
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 1234567
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### 2. Rodar a aplicação

```bash
# Perfil dev (PostgreSQL) — padrão
./mvnw spring-boot:run

# Perfil test (H2)
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

A API ficará disponível em `http://localhost:8080`.

### 3. Executar os testes

```bash
./mvnw test
```

## Tratamento de Erros

A API retorna erros no seguinte formato padrão:

```json
{
  "timestamp": "2024-01-01T00:00:00Z",
  "status": 404,
  "error": "Resource not found",
  "message": "...",
  "path": "/users/99"
}
```

| Exceção                     | HTTP | Situação                                |
|-----------------------------|------|-----------------------------------------|
| `ResourceNotFoundException` | 404  | Recurso não encontrado pelo ID          |
| `DatabaseException`         | 400  | Violação de integridade (ex: FK em uso) |

## Estrutura do Projeto

```
src/main/java/com/noxus/workshop/
├── config/              # Seed de dados (perfil test)
├── entities/            # Entidades JPA
│   ├── enums/           # OrderStatus
│   └── pk/              # Chave composta OrderItemPK
├── repositories/        # Interfaces Spring Data JPA
├── resources/           # Controllers REST
│   └── exceptions/      # Handler global de exceções
└── services/            # Regras de negócio
    └── exceptions/      # Exceções de domínio
```