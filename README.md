# Usuario Service — Tech Challenge Fase 1

Sistema de cadastro de usuarios (Cliente e Dono de Restaurante) para uma
plataforma compartilhada de gestao de restaurantes. Esta fase cobre
exclusivamente o dominio de Usuario.

## Stack

- Java 21
- Spring Boot 3.3
- Spring Data JPA + PostgreSQL
- BCrypt (spring-security-crypto) para hash de senha
- springdoc-openapi (Swagger UI)
- Docker + Docker Compose

## Decisoes de arquitetura

**Modelagem de Cliente / Dono do Restaurante:** optamos por um campo
discriminador (`enum TipoUsuario`) em vez de heranca de classes. Nesta
fase os dois papeis nao possuem atributos ou comportamento proprios --
a diferenca e' apenas de papel no sistema. Essa decisao evita tabelas e
joins desnecessarios e pode ser revisitada se, em fases futuras, cada
tipo passar a acumular dados proprios (ex: Dono ganhar uma lista de
restaurantes).

**Endereco como Value Object (`@Embeddable`):** o endereco nao tem
identidade nem ciclo de vida proprio, entao e' modelado como um objeto
de valor embutido na propria tabela de usuario, nao como uma entidade
separada.

**Senha:** nunca trafega nem e' armazenada em texto puro. E' sempre
convertida em hash BCrypt antes de tocar o banco. A troca de senha tem
um endpoint proprio, separado da atualizacao geral de dados, porque tem
regras de validacao diferentes (confirmacao de senha) e porque misturar
os dois deixaria o endpoint de atualizacao com responsabilidade dupla.

**Tratamento de erro:** centralizado num `GlobalExceptionHandler` usando
`ProblemDetail` (RFC 7807), suportado nativamente pelo Spring Boot 3.
Toda resposta de erro da API sai no mesmo formato.

## Como rodar (com Docker — recomendado)

Pre-requisito: Docker e Docker Compose instalados.

```bash
docker compose up --build
```

Isso sobe dois containers: o banco Postgres e a aplicacao. A aplicacao
so inicia depois que o banco estiver pronto para aceitar conexoes
(healthcheck).

A API fica disponivel em `http://localhost:8080`.
O Swagger UI fica em `http://localhost:8080/swagger-ui.html`.

Para derrubar tudo:

```bash
docker compose down
```

Para derrubar e apagar tambem os dados do banco:

```bash
docker compose down -v
```

## Como rodar localmente (sem Docker)

Pre-requisito: JDK 21, Maven, e um Postgres rodando localmente na porta
5432 com um banco chamado `techchallenge` (usuario/senha `techchallenge`,
ou ajuste as variaveis de ambiente abaixo).

```bash
mvn spring-boot:run
```

Variaveis de ambiente aceitas (todas com valor padrao):
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`.

## Endpoints

| Metodo | Rota                          | Descricao                          |
|--------|--------------------------------|-------------------------------------|
| POST   | `/v1/usuarios`                 | Cadastra um novo usuario            |
| PUT    | `/v1/usuarios/{id}`            | Atualiza dados (exceto senha)       |
| DELETE | `/v1/usuarios/{id}`            | Exclui um usuario                   |
| GET    | `/v1/usuarios/{id}`            | Busca por id                        |
| GET    | `/v1/usuarios?nome=Joao`       | Busca por nome (parcial)            |
| GET    | `/v1/usuarios`                 | Lista todos                         |
| POST   | `/v1/usuarios/{id}/senha`      | Troca de senha                      |
| POST   | `/v1/login`                    | Autenticacao                        |

## Repositorio

`[preencher com o link do repositorio publico no GitHub]`

## Postman

A collection de testes fica em `postman/tech-challenge-usuario.postman_collection.json`,
cobrindo os cenarios de sucesso e falha de cadastro, login, busca, atualizacao,
troca de senha e exclusao.

## Proximos passos (fora do escopo desta fase)

- Testes automatizados
- JWT real no login (hoje o login apenas valida credenciais)
