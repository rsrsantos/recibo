# Santo Recibo

SaaS para emissão de recibos com controle de assinaturas (planos pagos).
O usuário cadastra seus clientes, emite recibos, gera o PDF e gerencia seu
plano — tudo numa interface web.

---

## O que a aplicação faz

- **Cadastro e login** — com e-mail/senha (confirmação de e-mail obrigatória)
  ou login com Google.
- **Emissão de recibos** — com múltiplos modelos de impressão. Hoje o
  *Recibo Padrão* está disponível; outros modelos (Bobina, Nota Promissória,
  Inverso, Vale, Médico) vão sendo liberados aos poucos.
- **Geração de PDF** (JasperReports) — com valor por extenso, dados do
  emitente, logo e escolha do número de vias na impressão.
- **Emitente** — cada usuário configura seus dados e a logo que sai no recibo.
- **Planos e assinaturas** — pagamento via Mercado Pago. Sem plano ativo, o
  acesso fica bloqueado (paywall). Cada plano pode liberar modelos de recibo
  específicos (configurável no admin).
- **Área administrativa** — gestão de planos, perfis e usuários. O
  administrador acessa tudo sem precisar de plano.

## Tecnologias

- Java 17 + Spring Boot 3.3.5 (Web, Security, Data JPA, Mail, OAuth2)
- PostgreSQL 16 + Flyway (migrations versionadas)
- Thymeleaf + Tabler (UI)
- JasperReports 6.20 (PDF dos recibos)
- Docker + Docker Compose

---

## Perfis de ambiente

A aplicação roda em três perfis, definidos pela variável `SPRING_PROFILES_ACTIVE`:

| Perfil    | Uso                  | Flyway          | Templates           |
|-----------|----------------------|-----------------|---------------------|
| `dev`     | desenvolvimento local| manual          | sem cache (`src/`)  |
| `homolog` | testes em servidor   | automático (boot)| cache ligado       |
| `prod`    | produção             | automático (boot)| cache ligado       |

Configuração comum fica em `application.properties`; o que muda por ambiente
está em `application-{perfil}.properties`. Dados sensíveis **nunca** ficam no
código — vêm de variáveis de ambiente.

---

## Subindo com Docker (homologação)

Pré-requisitos: **Docker** e **Docker Compose** instalados no servidor.

### 1. Configurar as variáveis de ambiente

Copie o modelo e preencha com os valores reais:

```bash
cp .env.example .env
```

Edite o `.env`:

- `SPRING_PROFILES_ACTIVE` — use `homolog`.
- `DB_PASSWORD` — defina uma senha forte para o banco.
- `APP_URL_BASE` — URL pública da aplicação, **sem barra no final**
  (ex.: `http://meu-servidor:8080/recibo`). É usada nos links de e-mail.
- `MAIL_*` — credenciais SMTP. Para Gmail, gere uma *Senha de app*
  (não use a senha normal da conta).
- `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` — credenciais do Google OAuth2.
- `MP_ACCESS_TOKEN` / `MP_PUBLIC_KEY` — credenciais do Mercado Pago.

> O arquivo `.env` contém segredos e **não é versionado** (está no `.gitignore`).

### 2. Subir os containers

```bash
docker compose up -d --build
```

Isso sobe dois containers:

- **`recibo-db`** — PostgreSQL, com volume persistente (`recibo_db_data`).
- **`recibo-app`** — a aplicação. No boot, o Flyway aplica todas as migrations
  automaticamente no banco.

A aplicação fica disponível em `http://<servidor>:<APP_PORT>/recibo`
(`APP_PORT` padrão: 8080).

### 3. Comandos úteis

```bash
docker compose logs -f app      # acompanhar os logs da aplicação
docker compose ps               # status dos containers
docker compose down             # parar (mantém o volume do banco)
docker compose down -v          # parar e APAGAR o banco
docker compose up -d --build    # rebuild após mudança no código
```

---

## Rodando localmente (desenvolvimento)

Requer Java 17 e um PostgreSQL local com um banco `recibo`.

```bash
# 1. Aplicar as migrations no banco
./mvnw initialize flyway:migrate

# 2. Rodar a aplicação (perfil dev é o padrão)
./mvnw spring-boot:run
```

Acesse `http://localhost:8080/recibo`.

No perfil `dev`, os templates são servidos direto de `src/`, então mudanças
em HTML/CSS aparecem sem rebuild.

---

## Banco de dados e migrations

O schema é versionado com **Flyway**, em `src/main/resources/db/migration`
(`V1__...`, `V2__...`, etc). O Hibernate apenas **valida** o schema —
nunca o altera.

- Em `dev`: as migrations rodam manualmente (`./mvnw initialize flyway:migrate`).
- Em `homolog`/`prod`: rodam automaticamente quando o container inicia.

Para criar uma nova alteração de schema, adicione um arquivo
`V{N}__descricao.sql` com o próximo número da sequência.

---

## Estrutura do projeto

```
src/main/java/com/br/rr/
├── config/          configurações (security, OAuth, atributos globais)
├── controllers/     controllers MVC (+ admin/)
├── dto/             objetos de formulário
├── models/          entidades JPA
├── repository/      repositórios Spring Data
├── security/        autenticação
├── service/         regras de negócio (+ impl/)
└── util/            utilitários (ex.: valor por extenso)

src/main/resources/
├── db/migration/    migrations Flyway
├── reports/         templates JasperReports (.jrxml)
├── templates/       páginas Thymeleaf
└── static/          CSS, JS, imagens
```

---

## Notas de segurança

- Segredos (senhas, tokens) ficam só no `.env`, fora do versionamento.
- Recibos são imutáveis após emitidos (prova de pagamento).
- Acesso aos recibos é isolado por usuário (proteção contra IDOR).
- A validação de plano/modelo é feita também no backend, não só na tela.
