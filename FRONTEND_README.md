# Park Assembly - Sistema Completo

Sistema de gerenciamento de estacionamento com autenticacao desenvolvido com Angular 20 (Frontend) e Spring Boot (Backend).

## Visao Geral

Este sistema permite o gerenciamento completo de um estacionamento, incluindo:
- Autenticacao de usuarios
- Controle de entrada e saida de veiculos
- Gerenciamento de funcionarios (para administradores)
- Dashboard com estatisticas em tempo real

## Estrutura do Projeto

```
park_assembly/
├── src/                    # Backend Spring Boot
│   └── main/
│       ├── java/           # Codigo Java
│       └── resources/      # Configuracoes
├── frontend/               # Frontend Angular 20
│   └── src/
│       └── app/           # Aplicacao Angular
└── README.md
```

## Tecnologias Utilizadas

### Backend
- Java 21
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- Angular 20
- TypeScript
- SCSS
- RxJS

### Banco de Dados
- PostgreSQL 16 (Docker)
- Database: projetos
- Porta: 5432

## Pre-requisitos

- Java 21 ou superior
- Node.js 22.x ou superior
- npm 11.x ou superior
- PostgreSQL 16 (via Docker)
- Maven 3.x

## Configuracao do Banco de Dados

O banco de dados ja esta configurado com:

### Tabelas Criadas
1. **cars** - Gerenciamento de veiculos
2. **users** - Gerenciamento de usuarios

### Usuario Padrao
- **Usuario:** admin
- **Senha:** admin
- **Funcao:** ADMIN

**IMPORTANTE:** Altere a senha padrao apos o primeiro login!

## Instalacao e Execucao

### 1. Backend (Spring Boot)

```bash
# Na raiz do projeto
mvn clean install
mvn spring-boot:run
```

O backend estara disponivel em: **http://localhost:9090**

### 2. Frontend (Angular)

```bash
# Entre no diretorio frontend
cd frontend

# Instale as dependencias
npm install

# Inicie o servidor de desenvolvimento
npm start
```

O frontend estara disponivel em: **http://localhost:4200**

### 3. Acesse a Aplicacao

Abra o navegador e acesse: http://localhost:4200

## Credenciais de Acesso

### Usuario Administrador
- **Usuario:** admin
- **Senha:** admin

Apos o primeiro acesso, voce pode:
1. Alterar a senha do admin
2. Criar novos usuarios
3. Gerenciar funcionarios

## Funcionalidades

### Para Todos os Usuarios

#### Gerenciamento de Estacionamento
- Registrar entrada de veiculos (modelo, cor, placa, proprietario)
- Registrar saida de veiculos
- Listar todos os veiculos
- Buscar veiculo por placa
- Editar informacoes de veiculos ativos
- Excluir registros
- Filtrar por status (Ativo/Saida Registrada)
- Visualizar estatisticas (total, ativos, saidas)

### Apenas para Administradores

#### Gerenciamento de Funcionarios
- Listar todos os funcionarios
- Criar novo funcionario
- Editar informacoes de funcionarios
- Alterar senha de funcionarios
- Excluir funcionarios
- Definir funcoes (USER/ADMIN)

## API Endpoints

### Autenticacao
- `POST /api/auth/login` - Login no sistema

### Usuarios
- `GET /api/users` - Listar usuarios
- `GET /api/users/{id}` - Buscar usuario
- `POST /api/users` - Criar usuario
- `PUT /api/users/{id}` - Atualizar usuario
- `PATCH /api/users/{id}/password` - Alterar senha
- `DELETE /api/users/{id}` - Excluir usuario

### Carros
- `GET /api/cars` - Listar carros
- `GET /api/cars/{id}` - Buscar carro por ID
- `GET /api/cars/placa/{placa}` - Buscar por placa
- `POST /api/cars` - Registrar entrada
- `PUT /api/cars/{id}` - Atualizar informacoes
- `PATCH /api/cars/{id}/exit` - Registrar saida
- `DELETE /api/cars/{id}` - Excluir registro

## Arquitetura

### Backend
```
com.parking.api/
├── model/              # Entidades JPA
│   ├── Car.java
│   └── User.java
├── repository/         # Repositorios JPA
│   ├── CarRepository.java
│   └── UserRepository.java
├── service/           # Logica de negocio
│   ├── CarService.java
│   └── UserService.java
├── controller/        # Controllers REST
│   ├── AuthController.java
│   ├── CarController.java
│   └── UserController.java
├── dto/              # Data Transfer Objects
└── exception/        # Tratamento de excecoes
```

### Frontend
```
app/
├── components/       # Componentes visuais
├── services/        # Servicos de API
├── guards/          # Protecao de rotas
├── models/          # Interfaces TypeScript
└── pipes/           # Pipes customizados
```

## Fluxo de Autenticacao

1. Usuario acessa `/login`
2. Insere credenciais (username/password)
3. Sistema valida no backend
4. Token/sessao armazenada no localStorage
5. Usuario redirecionado para `/dashboard`
6. Guards verificam autenticacao em cada rota

## Seguranca

### Implementado
- Autenticacao de usuarios
- Controle de acesso por funcao (ROLE)
- Guards de rota no frontend
- CORS configurado
- Validacao de dados

### Recomendacoes para Producao
- Implementar JWT tokens
- Criptografia de senhas com bcrypt
- HTTPS obrigatorio
- Rate limiting
- Tokens de refresh
- Auditoria de acoes

## Tratamento de Erros

### Backend
- `ResourceNotFoundException` - Recurso nao encontrado (404)
- `DuplicatePlacaException` - Placa ja cadastrada (409)
- Validacao automatica de campos (400)

### Frontend
- Mensagens de erro amigaveis
- Feedback visual de operacoes
- Validacao de formularios

## Testes

### Backend
```bash
mvn test
```

### Frontend
```bash
cd frontend
npm test
```

## Build para Producao

### Backend
```bash
mvn clean package
java -jar target/park-assembly-1.0.0.jar
```

### Frontend
```bash
cd frontend
npm run build
# Arquivos gerados em: frontend/dist/
```

## Configuracoes

### Backend - application.properties
```properties
server.port=9090
spring.datasource.url=jdbc:postgresql://localhost:5432/projetos
spring.jpa.hibernate.ddl-auto=update
```

### Frontend - Servicos
URL da API configurada em cada servico: `http://localhost:9090/api`

## Troubleshooting

### Problema: Backend nao inicia
- Verifique se o PostgreSQL esta rodando
- Confirme as credenciais do banco de dados
- Verifique a porta 9090

### Problema: Frontend nao conecta ao backend
- Verifique se o backend esta rodando na porta 9090
- Confirme a configuracao de CORS
- Verifique o console do navegador

### Problema: Erro de autenticacao
- Verifique as credenciais (admin/admin)
- Limpe o localStorage do navegador
- Verifique a tabela users no banco

## Proximos Passos

1. Alterar senha do usuario admin
2. Criar usuarios para os funcionarios
3. Comecar a registrar veiculos
4. Explorar as funcionalidades de relatorio

## Suporte

Para duvidas ou problemas:
- Consulte a documentacao do Spring Boot: https://spring.io/projects/spring-boot
- Consulte a documentacao do Angular: https://angular.dev/

## Licenca

Projeto de uso livre para fins educacionais e demonstrativos.

---

Desenvolvido com Spring Boot e Angular 20
