# Docker Setup - Park Assembly

Este documento descreve como executar a aplicação completa usando Docker Compose.

## Arquitetura

A aplicação é composta por 3 serviços:

1. **PostgreSQL 16** - Banco de dados
2. **Backend** - API Spring Boot (porta 9090)
3. **Frontend** - Aplicação Angular (porta 4200)

## Pré-requisitos

- Docker
- Docker Compose

## Configuração Inicial

1. Copie o arquivo de exemplo de variáveis de ambiente:

```bash
cp .env.example .env
```

2. (Opcional) Edite o arquivo `.env` para customizar as credenciais do banco:

```env
POSTGRES_DB=park_assembly
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

## Executar a aplicação

### Iniciar todos os serviços

```bash
docker-compose up -d
```

### Verificar status dos containers

```bash
docker-compose ps
```

### Ver logs

```bash
# Todos os serviços
docker-compose logs -f

# Apenas um serviço específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Parar os serviços

```bash
docker-compose down
```

### Parar e remover volumes (apaga dados do banco)

```bash
docker-compose down -v
```

## Acessar a aplicação

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:9090
- **PostgreSQL**: localhost:5432

## Credenciais padrão

### Usuário Admin da Aplicação

- **Username**: `admin`
- **Password**: `admin`

### Banco de Dados

- **Database**: `park_assembly`
- **User**: `postgres`
- **Password**: `postgres`

**IMPORTANTE**: Altere essas credenciais em ambiente de produção!

## Estrutura de arquivos

```
.
├── docker-compose.yml           # Configuração dos serviços
├── Dockerfile                   # Build do backend
├── frontend/
│   ├── Dockerfile              # Build do frontend
│   └── nginx.conf              # Configuração do Nginx
└── docker/
    └── postgres/
        └── init/
            └── 01-init.sql     # Script de inicialização do banco
```

## Troubleshooting

### Backend não conecta ao banco

Verifique se o PostgreSQL está healthy:

```bash
docker-compose ps postgres
```

### Rebuild da aplicação

Se fez alterações no código, rebuild os containers:

```bash
docker-compose up -d --build
```

### Limpar tudo e recomeçar

```bash
docker-compose down -v
docker-compose up -d --build
```

## Desenvolvimento

### Conectar ao banco de dados

```bash
docker exec -it park-assembly-postgres psql -U postgres -d park_assembly
```

### Executar comandos no backend

```bash
docker exec -it park-assembly-api sh
```

### Executar comandos no frontend

```bash
docker exec -it park-assembly-frontend sh
```

## Volumes

Os dados do PostgreSQL são persistidos no volume Docker `postgres_data`. Para backup:

```bash
# Backup
docker exec park-assembly-postgres pg_dump -U postgres park_assembly > backup.sql

# Restore
cat backup.sql | docker exec -i park-assembly-postgres psql -U postgres park_assembly
```
