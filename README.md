# 🏥 Farmácia Bem Estar API

API REST desenvolvida com **Spring Boot 3.4.5** para gerenciamento de uma farmácia, com foco em **autenticação JWT**, **controle de acesso baseado em roles (RBAC)** e **proteção contra ataques de força bruta (Rate Limiting)**. Este projeto oferece uma solução robusta e segura para o gerenciamento de produtos e categorias de uma farmácia, garantindo a integridade e a confidencialidade dos dados.

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura do Projeto](#-arquitetura-do-projeto)
- [Funcionalidades de Segurança](#-funcionalidades-de-segurança)
- [Endpoints da API](#-endpoints-da-api)
  - [Usuários](#usuários)
  - [Produtos](#produtos)
  - [Categorias](#categorias)
- [Como Executar o Projeto Localmente](#-como-executar-o-projeto-localmente)
- [Autor](#-autor)

## 💡 Sobre o Projeto

A **Farmácia Bem Estar API** é um sistema backend completo para a gestão de uma farmácia. Ele permite o controle de **produtos** e **categorias**, oferecendo uma interface segura e eficiente para operações CRUD (Create, Read, Update, Delete). A segurança é um pilar fundamental, com a implementação de autenticação via **JWT**, um sistema de **refresh token** para manter sessões persistentes e um mecanismo de **rate limiting** para prevenir tentativas de login maliciosas. [1]

## ✨ Funcionalidades Principais

- **Gerenciamento de Produtos:** Adição, listagem, busca e remoção de produtos.
- **Gerenciamento de Categorias:** Criação, listagem, busca e atualização de categorias.
- **Autenticação e Autorização:**
  - Autenticação de usuários via JWT.
  - Geração e validação de refresh tokens para sessões prolongadas.
  - Controle de acesso baseado em roles (ADMIN e USER).
- **Segurança Robusta:**
  - Proteção contra ataques de força bruta com rate limiting.
  - Criptografia de senhas com BCrypt.

## 🛠️ Tecnologias Utilizadas

| Tecnologia      | Versão   |
| :-------------- | :------- |
| Java            | 21       |
| Spring Boot     | 3.4.5    |
| Spring Security | 6.x      |
| Spring Data JPA | 3.x      |
| MySQL           | 8+       |
| jjwt (JWT)      | 0.12+    |
| BCrypt          | —        |
| Maven           | 3.x      |

## 🏗️ Arquitetura do Projeto

O projeto segue a arquitetura em camadas padrão do Spring Boot, promovendo a separação de responsabilidades e a manutenibilidade do código. A estrutura de pacotes é organizada da seguinte forma:

```
src/main/java/com/generation/farmacia/
├── controller/        # Camada de entrada — recebe as requisições HTTP
│   ├── CategoriaController.java
│   ├── ProdutoController.java
│   └── UsuarioController.java
├── model/             # Entidades JPA — mapeamento com o banco de dados
│   ├── Categoria.java
│   ├── Produto.java
│   ├── Usuario.java   # Inclui enum Role (ADMIN/USER)
│   └── UsuarioLogin.java
├── repository/        # Acesso ao banco — Spring Data JPA
│   ├── CategoriaRepository.java
│   ├── ProdutoRepository.java
│   └── UsuarioRepository.java
├── service/           # Regras de negócio
│   └── UsuarioService.java
└── security/          # Configuração de segurança e filtros JWT
    ├── BasicSecurityConfig.java
    ├── JwtAuthFilter.java
    ├── JwtService.java
    ├── LoginRateLimiter.java
    ├── UserDetailsImpl.java
    └── UserDetailsServiceImpl.java
```

## 🔒 Funcionalidades de Segurança

1.  **Autenticação JWT**
    *   Token de acesso com validade de 1 hora.
    *   A role do usuário (ADMIN ou USER) é embutida como `claim` dentro do token.
    *   Algoritmo HS256 com chave secreta configurável via `application.properties`.

2.  **Refresh Token**
    *   UUID gerado a cada login, válido por 7 dias.
    *   Armazenado na tabela `tb_usuarios` (campo `refresh_token`).
    *   Rota dedicada para renovação do token sem necessidade de novo login.

3.  **Rate Limiting (Proteção contra Força Bruta)**
    *   Máximo de 5 tentativas de login com credenciais inválidas.
    *   Após o limite, a conta fica bloqueada por 15 minutos.
    *   A resposta informa quantas tentativas restam antes do bloqueio.
    *   Implementado em memória com `ConcurrentHashMap`, sendo substituível por Redis em produção.

4.  **Controle de Acesso por Role (RBAC)**
    *   Acesso diferenciado para usuários com roles `ADMIN` e `USER`.
    *   Novos usuários são criados com role `USER` por padrão. Para promover a `ADMIN`, execute no banco:

    ```sql
    UPDATE tb_usuarios SET role = 'ADMIN' WHERE usuario = 'email@email.com';
    ```

## 🌐 Endpoints da API

### Usuários

| Método | Rota                 | Descrição                 | Auth     |
| :----- | :------------------- | :------------------------ | :------- |
| `POST` | `/usuarios/cadastrar`| Cria novo usuário         | Público  |
| `POST` | `/usuarios/logar`    | Login e retorna JWT + Refresh Token | Público  |
| `POST` | `/usuarios/refresh`  | Renova o access token     | Público  |

**Exemplo de Corpo de Requisição para Cadastro:**

```json
{
  "nome": "João Silva",
  "usuario": "joao@farmacia.com",
  "senha": "senha123"
}
```

**Exemplo de Resposta de Login:**

```json
{
  "id": 1,
  "nome": "João Silva",
  "usuario": "joao@farmacia.com",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "role": "USER",
  "senha": ""
}
```

### Produtos

| Método   | Rota                 | Descrição             | Auth    |
| :------- | :------------------- | :-------------------- | :------ |
| `GET`    | `/produtos`          | Lista todos os produtos | Público |
| `GET`    | `/produtos/{id}`     | Busca por ID          | Público |
| `GET`    | `/produtos/nome/{nome}` | Busca por nome        | Público |
| `POST`   | `/produtos`          | Cria produto          | ADMIN   |
| `PUT`    | `/produtos`          | Atualiza produto      | ADMIN   |
| `DELETE` | `/produtos/{id}`     | Remove produto        | ADMIN   |

### Categorias

| Método   | Rota                 | Descrição             | Auth    |
| :------- | :------------------- | :-------------------- | :------ |
| `GET`    | `/categorias`        | Lista todas as categorias | Público |
| `GET`    | `/categorias/{id}`   | Busca por ID          | Público |\n| `GET`    | `/categorias/nome/{nome}` | Busca por nome        | Público |
| `POST`   | `/categorias`        | Cria categoria        | ADMIN   |
| `PUT`    | `/categorias`        | Atualiza categoria    | ADMIN   |
| `DELETE` | `/categorias/{id}`   | Remove categoria      | ADMIN   |

## 🚀 Como Executar o Projeto Localmente

Para configurar e executar a API em seu ambiente local, siga os passos abaixo:

### Pré-requisitos

-   Java 21+
-   Maven 3.x
-   MySQL 8+

### 1. Clonar o repositório

```bash
git clone https://github.com/bruna-dsmendes/projeto_final_bloco_02.git
cd projeto_final_bloco_02
```

### 2. Configurar o banco de dados

Crie o banco de dados no MySQL:

```sql
CREATE DATABASE db_farmacia_bem_estar;
```

Execute o script de migração para adicionar as colunas de segurança:

```sql
USE db_farmacia_bem_estar;

ALTER TABLE tb_usuarios
ADD COLUMN IF NOT EXISTS role VARCHAR(10) NOT NULL DEFAULT 'USER',
ADD COLUMN IF NOT EXISTS refresh_token VARCHAR(255) DEFAULT NULL;
```

### 3. Configurar o `application.properties`

Atualize o arquivo `src/main/resources/application.properties` com as configurações do seu banco de dados e a chave secreta JWT. Certifique-se de substituir `sua_senha` pela senha do seu usuário MySQL.

```properties
spring.application.name=projeto_final_bloco_02

spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost/db_farmacia_bem_estar?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.show-sql=true
spring.jpa.open-in-view=true
spring.jpa.properties.hibernate.jdbc.time_zone=America/Sao_Paulo

spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=America/Sao_Paulo

# Chave secreta JWT (substitua em produção!)
jwt.secret=5367566B59703373673639792F423F45284B2B625165546857605A71347437
```

### 4. Rode a aplicação

Navegue até a raiz do projeto e execute o comando Maven:

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🧑‍💻 Autor

**Bruna Mendes** - [Projeto Final Bloco 02](https://github.com/bruna-dsmendes/projeto_final_bloco_02) - Junho / 2026

## 📚 Referências

[1] Repositório GitHub: [bruna-dsmendes/projeto_final_bloco_02](https://github.com/bruna-dsmendes/projeto_final_bloco_02/tree/features)
