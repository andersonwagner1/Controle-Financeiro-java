# Controle-financerio

API Java 17 com Spring Boot, JPA, PostgreSQL e Swagger para o sistema Angular `Controle-Financeiro`.

## Pré-requisitos

- Java 17
- Maven 3.9+
- PostgreSQL 9.12 ou superior e pgAdmin 4

Crie um banco chamado `controle_financeiro` no pgAdmin e configure, se necessário:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/controle_financeiro"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua-senha"
```

## Executar

```powershell
mvn spring-boot:run
```

A API fica em `http://localhost:8080/api` e o Swagger em `http://localhost:8080/swagger-ui.html`.

Endpoints principais: `/bancos`, `/contas-base`, `/contas`, `/vinculos`, `/categorias`, `/lancamentos`,
`/lancamentos-cartao` e `/transferencias`.

O Angular deve usar `http://localhost:8080/api` como base URL. O CORS para `http://localhost:4200` já está habilitado.

## Organização do código

- `controller`: recebe requisições HTTP e delega as operações.
- `service`: concentra regras de negócio, transações e acesso aos repositórios.
- `dto`: define os contratos de entrada e saída da API, isolando as entidades JPA.

Novas regras financeiras devem ser implementadas em `FinanceiroService`; novos contratos HTTP devem ser criados em `dto` antes de serem usados pelo controller.

## Cartões de crédito

`POST /api/cartoes-credito` e `PUT /api/cartoes-credito/{id}` suportam o formulário com `nome`, `vinculoId`,
`limite`, `diaFechamento` e `diaVencimento`. A resposta inclui `limiteDisponivel`.

Para registrar uma compra, use `POST /api/lancamentos-cartao` com `cartaoCreditoId`, `contaId` igual ao
`vinculoId` do cartão, `tipo: "debito"` e valor positivo. A compra não reduz o saldo da conta e é recusada
se exceder o limite. Esses registros são armazenados separadamente em `Lancamento_cartao`.
O script PostgreSQL está em `src/main/resources/db/migration/V1__cartoes_credito.sql`.
