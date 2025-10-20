# Park Assembly API

API REST para gerenciamento de estacionamento de carros desenvolvida com Spring Boot e Java 21.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (desenvolvimento)
- Maven
- Lombok

## Funcionalidades

- Registrar entrada de carros no estacionamento
- Listar todos os carros estacionados
- Buscar carro por ID
- Buscar carro por placa
- Atualizar informações de um carro
- Registrar saída de um carro
- Remover registro de um carro

## Modelo de Dados

### Car (Carro)

```json
{
  "id": 1,
  "modelo": "Honda Civic",
  "cor": "Preto",
  "placa": "ABC-1234",
  "nomeProprietario": "João Silva",
  "dataEntrada": "2025-10-20T10:30:00",
  "dataSaida": null
}
```

## Endpoints da API

### Base URL
```
http://localhost:9090/api/cars
```

### 1. Listar todos os carros
```http
GET /api/cars
```

**Resposta:** `200 OK`
```json
[
  {
    "id": 1,
    "modelo": "Honda Civic",
    "cor": "Preto",
    "placa": "ABC-1234",
    "nomeProprietario": "João Silva",
    "dataEntrada": "2025-10-20T10:30:00",
    "dataSaida": null
  }
]
```

### 2. Buscar carro por ID
```http
GET /api/cars/{id}
```

**Resposta:** `200 OK`
```json
{
  "id": 1,
  "modelo": "Honda Civic",
  "cor": "Preto",
  "placa": "ABC-1234",
  "nomeProprietario": "João Silva",
  "dataEntrada": "2025-10-20T10:30:00",
  "dataSaida": null
}
```

### 3. Buscar carro por placa
```http
GET /api/cars/placa/{placa}
```

**Resposta:** `200 OK`

### 4. Registrar entrada de carro
```http
POST /api/cars
Content-Type: application/json

{
  "modelo": "Honda Civic",
  "cor": "Preto",
  "placa": "ABC-1234",
  "nomeProprietario": "João Silva"
}
```

**Resposta:** `201 CREATED`
```json
{
  "id": 1,
  "modelo": "Honda Civic",
  "cor": "Preto",
  "placa": "ABC-1234",
  "nomeProprietario": "João Silva",
  "dataEntrada": "2025-10-20T10:30:00",
  "dataSaida": null
}
```

### 5. Atualizar informações do carro
```http
PUT /api/cars/{id}
Content-Type: application/json

{
  "modelo": "Honda Civic EX",
  "cor": "Preto",
  "placa": "ABC-1234",
  "nomeProprietario": "João Silva"
}
```

**Resposta:** `200 OK`

### 6. Registrar saída do carro
```http
PATCH /api/cars/{id}/exit
```

**Resposta:** `200 OK`
```json
{
  "id": 1,
  "modelo": "Honda Civic",
  "cor": "Preto",
  "placa": "ABC-1234",
  "nomeProprietario": "João Silva",
  "dataEntrada": "2025-10-20T10:30:00",
  "dataSaida": "2025-10-20T15:45:00"
}
```

### 7. Remover carro do registro
```http
DELETE /api/cars/{id}
```

**Resposta:** `204 NO CONTENT`

## Tratamento de Erros

### Carro não encontrado (404)
```json
{
  "status": 404,
  "message": "Carro não encontrado com ID: 1",
  "timestamp": "2025-10-20T10:30:00"
}
```

### Placa duplicada (409)
```json
{
  "status": 409,
  "message": "Já existe um carro registrado com a placa: ABC-1234",
  "timestamp": "2025-10-20T10:30:00"
}
```

### Erro de validação (400)
```json
{
  "status": 400,
  "errors": {
    "modelo": "Modelo é obrigatório",
    "placa": "Placa é obrigatória"
  },
  "timestamp": "2025-10-20T10:30:00"
}
```

## Como Executar

### Pré-requisitos
- Java 21 instalado
- Maven instalado

### Executar a aplicação

1. Clone o repositório ou navegue até o diretório do projeto

2. Execute o comando Maven:
```bash
mvn spring-boot:run
```

3. A aplicação estará disponível em: `http://localhost:9090`

### Console H2 Database

Para acessar o console do banco de dados H2 durante o desenvolvimento:

URL: `http://localhost:9090/h2-console`

Configurações de conexão:
- JDBC URL: `jdbc:h2:mem:parkingdb`
- Username: `sa`
- Password: (deixar em branco)

## Exemplos de Uso com cURL

### Registrar um carro
```bash
curl -X POST http://localhost:9090/api/cars \
  -H "Content-Type: application/json" \
  -d '{
    "modelo": "Honda Civic",
    "cor": "Preto",
    "placa": "ABC-1234",
    "nomeProprietario": "João Silva"
  }'
```

### Listar todos os carros
```bash
curl http://localhost:9090/api/cars
```

### Buscar carro por ID
```bash
curl http://localhost:9090/api/cars/1
```

### Buscar carro por placa
```bash
curl http://localhost:9090/api/cars/placa/ABC-1234
```

### Atualizar carro
```bash
curl -X PUT http://localhost:9090/api/cars/1 \
  -H "Content-Type: application/json" \
  -d '{
    "modelo": "Honda Civic EX",
    "cor": "Preto",
    "placa": "ABC-1234",
    "nomeProprietario": "João Silva"
  }'
```

### Registrar saída
```bash
curl -X PATCH http://localhost:9090/api/cars/1/exit
```

### Deletar carro
```bash
curl -X DELETE http://localhost:9090/api/cars/1
```

## Estrutura do Projeto

```
park_assembly/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── parking/
│   │   │           └── api/
│   │   │               ├── controller/
│   │   │               │   └── CarController.java
│   │   │               ├── exception/
│   │   │               │   ├── DuplicatePlacaException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── ResourceNotFoundException.java
│   │   │               ├── model/
│   │   │               │   └── Car.java
│   │   │               ├── repository/
│   │   │               │   └── CarRepository.java
│   │   │               ├── service/
│   │   │               │   └── CarService.java
│   │   │               └── ParkAssemblyApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## Licença

Este projeto é livre para uso educacional e comercial.
