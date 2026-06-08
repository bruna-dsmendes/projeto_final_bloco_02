# ðŸ’Š FarmÃ¡cia Bem Estar API

> API REST desenvolvida com **Spring Boot 3.4.5** para gerenciamento de uma farmÃ¡cia, com autenticaÃ§Ã£o JWT, controle de acesso por roles e proteÃ§Ã£o contra ataques de forÃ§a bruta.

---

## ðŸ“‹ Ãndice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Funcionalidades de SeguranÃ§a](#-funcionalidades-de-seguranÃ§a)
- [Como Rodar](#-como-rodar)
- [Endpoints](#-endpoints)
- [Fluxo de AutenticaÃ§Ã£o](#-fluxo-de-autenticaÃ§Ã£o)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## ðŸ“Œ Sobre o Projeto

A **FarmÃ¡cia Bem Estar API** Ã© um sistema de gerenciamento backend que permite o controle de **produtos** e **categorias** de uma farmÃ¡cia. O sistema conta com uma camada de seguranÃ§a robusta, incluindo autenticaÃ§Ã£o via **JWT**, sistema de **refresh token** para sessÃµes persistentes e **rate limiting** para bloquear tentativas de invasÃ£o.

---

## ðŸ›  Tecnologias

| Tecnologia | VersÃ£o |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.5 |
| Spring Security | 6.x |
| Spring Data JPA | 3.x |
| MySQL | 8+ |
| jjwt (JWT) | 0.12+ |
| BCrypt | â€” |
| Maven | 3.x |

---

## ðŸ— Arquitetura

O projeto segue a arquitetura em camadas padrÃ£o do Spring Boot:

```
src/main/java/com/generation/farmacia/
â”‚
â”œâ”€â”€ controller/        # Camada de entrada â€” recebe as requisiÃ§Ãµes HTTP
â”‚   â”œâ”€â”€ CategoriaController.java
â”‚   â”œâ”€â”€ ProdutoController.java
â”‚   â””â”€â”€ UsuarioController.java
â”‚
â”œâ”€â”€ model/             # Entidades JPA â€” mapeamento com o banco de dados
â”‚   â”œâ”€â”€ Categoria.java
â”‚   â”œâ”€â”€ Produto.java
â”‚   â”œâ”€â”€ Usuario.java   # Inclui enum Role (ADMIN/USER)
â”‚   â””â”€â”€ UsuarioLogin.java
â”‚
â”œâ”€â”€ repository/        # Acesso ao banco â€” Spring Data JPA
â”‚   â”œâ”€â”€ CategoriaRepository.java
â”‚   â”œâ”€â”€ ProdutoRepository.java
â”‚   â””â”€â”€ UsuarioRepository.java
â”‚
â”œâ”€â”€ service/           # Regras de negÃ³cio
â”‚   â””â”€â”€ UsuarioService.java
â”‚
â””â”€â”€ security/          # Camada de seguranÃ§a
    â”œâ”€â”€ BasicSecurityConfig.java    # ConfiguraÃ§Ã£o de rotas e permissÃµes
    â”œâ”€â”€ JwtAuthFilter.java          # Filtro JWT por requisiÃ§Ã£o
    â”œâ”€â”€ JwtService.java             # GeraÃ§Ã£o e validaÃ§Ã£o de tokens
    â”œâ”€â”€ LoginRateLimiter.java       # Bloqueio por tentativas excessivas
    â”œâ”€â”€ UserDetailsImpl.java        # Wrapper de usuÃ¡rio para o Spring Security
    â””â”€â”€ UserDetailsServiceImpl.java # Carregamento do usuÃ¡rio do banco
```

---

## ðŸ” Funcionalidades de SeguranÃ§a

### 1. AutenticaÃ§Ã£o JWT
- Token de acesso com validade de **1 hora**
- A role do usuÃ¡rio (`ADMIN` ou `USER`) Ã© embutida como **claim** dentro do token
- Algoritmo **HS256** com chave secreta configurÃ¡vel via `application.properties`

### 2. Refresh Token
- UUID gerado a cada login, vÃ¡lido por **7 dias**
- Armazenado na tabela `tb_usuarios` (campo `refresh_token`)
- **RotaÃ§Ã£o a cada uso** â€” o token antigo Ã© invalidado e um novo Ã© gerado
- Endpoint dedicado para renovaÃ§Ã£o sem necessidade de novo login

### 3. Rate Limiting (ProteÃ§Ã£o contra ForÃ§a Bruta)
- MÃ¡ximo de **5 tentativas** de login com credenciais invÃ¡lidas
- ApÃ³s o limite, a conta fica **bloqueada por 15 minutos**
- A resposta informa quantas tentativas restam antes do bloqueio
- Implementado em memÃ³ria com `ConcurrentHashMap` â€” substituÃ­vel por Redis em produÃ§Ã£o

### 4. Controle de Acesso por Role (RBAC)

| Rota | USER | ADMIN |
|---|:---:|:---:|
| `GET /produtos/**` | âœ… | âœ… |
| `GET /categorias/**` | âœ… | âœ… |
| `POST /produtos` | âŒ | âœ… |
| `PUT /produtos` | âŒ | âœ… |
| `DELETE /produtos/{id}` | âŒ | âœ… |
| `POST /categorias` | âŒ | âœ… |
| `PUT /categorias` | âŒ | âœ… |
| `DELETE /categorias/{id}` | âŒ | âœ… |
| `POST /usuarios/logar` | âœ… | âœ… |
| `POST /usuarios/cadastrar` | âœ… | âœ… |
| `POST /usuarios/refresh` | âœ… | âœ… |

> **Nota:** Novos usuÃ¡rios sÃ£o criados com role `USER` por padrÃ£o. Para promover a `ADMIN`, execute no banco:
> ```sql
> UPDATE tb_usuarios SET role = 'ADMIN' WHERE usuario = 'email@email.com';
> ```

---

## ðŸš€ Como Rodar

### PrÃ©-requisitos
- Java 21+
- Maven 3.x
- MySQL 8+

### 1. Clone o repositÃ³rio
```bash
git clone https://github.com/seu-usuario/farmacia-bem-estar.git
cd farmacia-bem-estar
```

### 2. Configure o banco de dados
Crie o banco no MySQL:
```sql
CREATE DATABASE db_farmacia_bem_estar;
```

Execute o script de migraÃ§Ã£o para adicionar as colunas de seguranÃ§a:
```sql
USE db_farmacia_bem_estar;

ALTER TABLE tb_usuarios
ADD COLUMN IF NOT EXISTS role VARCHAR(10) NOT NULL DEFAULT 'USER',
ADD COLUMN IF NOT EXISTS refresh_token VARCHAR(255) DEFAULT NULL;
```

### 3. Configure o `application.properties`
```properties
spring.application.name=projeto_final_bloco_02

spring.jpa.hibernate.ddl-auto=update

spring.datasource.url=jdbc:mysql://localhost/db_farmacia_bem_estar?createDatabaseIfNotExist=true&serverTimezone=America/Sao_Paulo
spring.datasource.username=root
spring.datasource.password=sua_senha

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.show-sql=true
spring.jpa.open-in-view=true
spring.jpa.properties.hibernate.jdbc.time_zone=America/Sao_Paulo

spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=America/Sao_Paulo

# Chave secreta JWT (substitua em produÃ§Ã£o!)
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

### 4. Rode a aplicaÃ§Ã£o
```bash
mvn spring-boot:run
```

A API estarÃ¡ disponÃ­vel em: `http://localhost:8080`

---

## ðŸ“¡ Endpoints

### ðŸ‘¤ UsuÃ¡rios

| MÃ©todo | Rota | DescriÃ§Ã£o | Auth |
|---|---|---|---|
| POST | `/usuarios/cadastrar` | Cria novo usuÃ¡rio | PÃºblico |
| POST | `/usuarios/logar` | Login â€” retorna JWT + refreshToken | PÃºblico |
| POST | `/usuarios/refresh` | Renova o access token | PÃºblico |

**Corpo do cadastro:**
```json
{
  "nome": "JoÃ£o Silva",
  "usuario": "joao@farmacia.com",
  "senha": "senha123"
}
```

**Resposta do login:**
```json
{
  "id": 1,
  "nome": "JoÃ£o Silva",
  "usuario": "joao@farmacia.com",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "role": "USER",
  "senha": ""
}
```

---

### ðŸ’Š Produtos

| MÃ©todo | Rota | DescriÃ§Ã£o | Auth |
|---|---|---|---|
| GET | `/produtos` | Lista todos os produtos | PÃºblico |
| GET | `/produtos/{id}` | Busca por ID | PÃºblico |
| GET | `/produtos/nome/{nome}` | Busca por nome | PÃºblico |
| POST | `/produtos` | Cria produto | ðŸ”’ ADMIN |
| PUT | `/produtos` | Atualiza produto | ðŸ”’ ADMIN |
| DELETE | `/produtos/{id}` | Remove produto | ðŸ”’ ADMIN |

---

### ðŸ—‚ï¸ Categorias

| MÃ©todo | Rota | DescriÃ§Ã£o | Auth |
|---|---|---|---|
| GET | `/categorias` | Lista todas as categorias | PÃºblico |
| GET | `/categorias/{id}` | Busca por ID | PÃºblico |
| GET | `/categorias/nome/{nome}` | Busca por nome | PÃºblico |
| POST | `/categorias` | Cria categoria | ðŸ”’ ADMIN |
| PUT | `/categorias` | Atualiza categoria | ðŸ”’ ADMIN |
| DELETE | `/categorias/{id}` | Remove categoria | ðŸ”’ ADMIN |

---

## ðŸ”„ Fluxo de AutenticaÃ§Ã£o

```
1. POST /usuarios/logar
   â””â”€â†’ Retorna: { token, refreshToken, role }

2. Usa o token no header de cada requisiÃ§Ã£o protegida:
   Authorization: Bearer <token>

3. Quando o token expirar (1h), usa o refreshToken:
   POST /usuarios/refresh
   Body: { "refreshToken": "uuid-aqui" }
   â””â”€â†’ Retorna novo par { token, refreshToken }

4. ApÃ³s 5 tentativas de login erradas:
   â””â”€â†’ Conta bloqueada por 15 minutos
   â””â”€â†’ HTTP 429 Too Many Requests
```

---

## ðŸ“ Estrutura do Projeto

```
farmacia/
â”œâ”€â”€ src/
â”‚   â”œâ”€â”€ main/
â”‚   â”‚   â”œâ”€â”€ java/com/generation/farmacia/
â”‚   â”‚   â”‚   â”œâ”€â”€ controller/
â”‚   â”‚   â”‚   â”œâ”€â”€ model/
â”‚   â”‚   â”‚   â”œâ”€â”€ repository/
â”‚   â”‚   â”‚   â”œâ”€â”€ security/
â”‚   â”‚   â”‚   â”œâ”€â”€ service/
â”‚   â”‚   â”‚   â””â”€â”€ ProjetoFinalBloco02Application.java
â”‚   â”‚   â””â”€â”€ resources/
â”‚   â”‚       â””â”€â”€ application.properties
â”‚   â””â”€â”€ test/
â”œâ”€â”€ pom.xml
â””â”€â”€ README.md
```

---

## ðŸ‘©â€ðŸ’» Desenvolvido por

**Bruna Mendes** â€” Projeto Final Bloco 02  
ðŸ—“ï¸ Junho / 2026

---

> *"CÃ³digo limpo nÃ£o Ã© escrito seguindo um conjunto de regras. VocÃª nÃ£o se torna um artesÃ£o de software aprendendo uma lista de heurÃ­sticas. Profissionalismo e artesanato vÃªm de valores que impulsionam disciplinas."*  
> â€” **Robert C. Martin**