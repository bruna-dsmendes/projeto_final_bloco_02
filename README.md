🏥 Farmácia Bem Estar API
API REST completa desenvolvida com Spring Boot 3.4.5 para o gerenciamento de uma farmácia. Este projeto oferece funcionalidades robustas para produtos, categorias, carrinho de compras e um sistema de autenticação e autorização (JWT, Refresh Token, RBAC) com proteção contra ataques de força bruta (Rate Limiting). O objetivo é fornecer uma solução segura e eficiente para operações de e-commerce e gestão de estoque. [1]

📋 Sumário
    •   Sobre o Projeto
    •   Funcionalidades Principais
    •   Tecnologias Utilizadas
    •   Arquitetura do Projeto
    •   Funcionalidades de Segurança
    •   Endpoints da API
    •   Usuários
    •   Produtos
    •   Categorias
    •   Carrinho de Compras
    •   Como Executar o Projeto Localmente
    •   Autor
    •   Referências

💡 Sobre o Projeto
A Farmácia Bem Estar API é um sistema backend abrangente projetado para otimizar a gestão de uma farmácia. Ele permite o controle detalhado de produtos e categorias, além de introduzir um carrinho de compras funcional para simular transações. A segurança é primordial, com a implementação de um sistema de autenticação via JSON Web Tokens (JWT), que inclui refresh tokens para sessões persistentes e um mecanismo de rate limiting para mitigar tentativas de login maliciosas. O controle de acesso é refinado através de Role-Based Access Control (RBAC), garantindo que apenas usuários autorizados (ADMIN ou USER) possam realizar operações específicas. [1]

✨ Funcionalidades Principais
    •   Gerenciamento de Produtos: Operações CRUD completas para produtos, incluindo busca por ID e por quantidade em estoque mínima.
    •   Gerenciamento de Categorias: Operações CRUD para categorias, com busca por ID e nome.
    •   Carrinho de Compras:
    •   Criação automática de carrinho para usuários autenticados.
    •   Adição de produtos ao carrinho, com agregação de quantidade se o item já existir.
    •   Remoção de itens específicos do carrinho.
    •   Cálculo automático do valor total do carrinho.
    •   Autenticação e Autorização:
    •   Autenticação de usuários via JWT.
    •   Geração e validação de refresh tokens para sessões prolongadas e seguras.
    •   Controle de acesso baseado em roles (ADMIN e USER).
    •   Segurança Robusta:
    •   Proteção contra ataques de força bruta com rate limiting (5 tentativas, bloqueio de 15 minutos).
    •   Criptografia de senhas utilizando BCrypt.

🛠️ Tecnologias Utilizadas
Tecnologia
Versão
Java
21
Spring Boot
3.4.5
Spring Security
6.x
Spring Data JPA
3.x
MySQL
8+
jjwt (JWT)
0.12+
BCrypt
—
Maven
3.x
🏗️ Arquitetura do Projeto
O projeto segue a arquitetura em camadas padrão do Spring Boot, promovendo a separação de responsabilidades e a manutenibilidade do código. A estrutura de pacotes é organizada da seguinte forma:

src/main/java/com/generation/farmacia/
├── controller/        # Camada de entrada — recebe as requisições HTTP (Produto, Categoria, Usuário, Carrinho)
│   ├── CategoriaController.java
│   ├── ProdutoController.java
│   ├── CarrinhoController.java
│   └── UsuarioController.java
├── model/             # Entidades JPA — mapeamento com o banco de dados (Categoria, Produto, Usuário, ItemCarrinho, Carrinho, UsuarioLogin)
│   ├── Categoria.java
│   ├── Produto.java
│   ├── Usuario.java   # Inclui enum Role (ADMIN/USER)
│   ├── UsuarioLogin.java
│   ├── Carrinho.java
│   └── ItemCarrinho.java
├── repository/        # Acesso ao banco — Spring Data JPA (Categoria, Produto, Usuário, ItemCarrinho, Carrinho)
│   ├── CategoriaRepository.java
│   ├── ProdutoRepository.java
│   ├── UsuarioRepository.java
│   ├── ItemCarrinhoRepository.java
│   └── CarrinhoRepository.java
├── service/           # Regras de negócio (Usuário, Carrinho)
│   ├── UsuarioService.java
│   └── CarrinhoService.java
└── security/          # Configuração de segurança e filtros JWT
    ├── BasicSecurityConfig.java
    ├── JwtAuthFilter.java
    ├── JwtService.java
    ├── LoginRateLimiter.java
    ├── UserDetailsImpl.java
    └── UserDetailsServiceImpl.java

🔒 Funcionalidades de Segurança
    •   Autenticação JWT
    •   Tokens de acesso com validade de 1 hora.
    •   A role do usuário (ADMIN ou USER) é embutida como claim dentro do token JWT.
    •   Utiliza algoritmo HS256 com chave secreta configurável via application.properties.
    •   Refresh Token
    •   UUID gerado a cada login, válido por 7 dias.
    •   Armazenado na tabela tb_usuarios (campo refresh_token).
    •   Endpoint dedicado (/usuarios/refresh) para renovar o access token sem a necessidade de um novo login completo. O refresh token é rotacionado a cada uso para maior segurança. [1]
    •   Rate Limiting (Proteção contra Força Bruta)
    •   Máximo de 5 tentativas de login com credenciais inválidas.
    •   Após o limite, a conta é bloqueada por 15 minutos.
    •   A resposta da API informa o número de tentativas restantes antes do bloqueio.
    •   Implementado em memória com ConcurrentHashMap (pode ser substituído por Redis em produção para ambientes distribuídos). [1]
    •   Controle de Acesso por Role (RBAC)
    •   Acesso diferenciado para usuários com roles ADMIN e USER.
    •   Rotas Públicas:
    •   POST /usuarios/cadastrar
    •   POST /usuarios/logar
    •   POST /usuarios/refresh
    •   GET /produtos/** (todas as operações GET em produtos)
    •   GET /categorias/** (todas as operações GET em categorias)
    •   Rotas Exclusivas para ADMIN:
    •   POST /produtos
    •   PUT /produtos
    •   DELETE /produtos/{id}
    •   POST /categorias
    •   PUT /categorias
    •   DELETE /categorias/{id}
    •   Outras Rotas: Qualquer outra rota (incluindo as de carrinho) exige autenticação (qualquer role).
    •   Novos usuários são criados com a role USER por padrão. Para promover um usuário a ADMIN, execute o seguinte comando SQL diretamente no banco de dados:

UPDATE tb_usuarios SET role = 'ADMIN' WHERE usuario = 'email@email.com';

🌐 Endpoints da API
A base URL para todos os endpoints é http://localhost:8080.

Usuários
Método
Rota
Descrição
Auth
POST
/usuarios/cadastrar
Cria um novo usuário no sistema
Público
POST
/usuarios/logar
Realiza o login e retorna JWT + Refresh Token
Público
POST
/usuarios/refresh
Renova o access token utilizando o refresh token
Público
Exemplo de Corpo de Requisição para Cadastro:

{
  "nome": "João Silva",
  "usuario": "joao@farmacia.com",
  "senha": "senha123"
}

Exemplo de Resposta de Login:

{
  "id": 1,
  "nome": "João Silva",
  "usuario": "joao@farmacia.com",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "role": "USER",
  "senha": ""
}

Produtos
Método
Rota
Descrição
Auth
GET
/produtos
Lista todos os produtos
Público
GET
/produtos/{id}
Busca um produto específico por ID
Público
GET
/produtos/estoque-minimo/{quantidade}
Lista produtos com estoque menor ou igual à quantidade especificada
Público
POST
/produtos
Cria um novo produto (requer categoria.id)
ADMIN
PUT
/produtos
Atualiza um produto existente (requer categoria.id)
ADMIN
DELETE
/produtos/{id}
Remove um produto por ID
ADMIN
Categorias
Método
Rota
Descrição
Auth
GET
/categorias
Lista todas as categorias
Público
GET
/categorias/{id}
Busca uma categoria específica por ID
Público
GET
/categorias/nome/{nome}
Busca categorias por nome
Público
POST
/categorias
Cria uma nova categoria
ADMIN
PUT
/categorias
Atualiza uma categoria existente
ADMIN
DELETE
/categorias/{id}
Remove uma categoria por ID
ADMIN
Carrinho de Compras
Método
Rota
Descrição
Auth
GET
/carrinho
Visualiza o carrinho do usuário autenticado (cria um novo se não existir)
Autenticado
POST
/carrinho/adicionar?produtoId={id}&quantidade={qtd}
Adiciona um produto ao carrinho (agrega quantidade se já existir)
Autenticado
DELETE
/carrinho/remover/{idItemCarrinho}
Remove um item específico do carrinho por ID do item
Autenticado
🚀 Como Executar o Projeto Localmente
Para configurar e executar a API em seu ambiente local, siga os passos abaixo:

Pré-requisitos
    •   Java 21+
    •   Maven 3.x
    •   MySQL 8+

1. Clonar o repositório
git clone https://github.com/bruna-dsmendes/projeto_final_bloco_02.git
cd projeto_final_bloco_02

2. Configurar o banco de dados
Crie o banco de dados no MySQL:

CREATE DATABASE db_farmacia_bem_estar;

Execute o script de migração para adicionar as colunas de segurança à tabela de usuários:

USE db_farmacia_bem_estar;
 
ALTER TABLE tb_usuarios
ADD COLUMN IF NOT EXISTS role VARCHAR(10) NOT NULL DEFAULT 'USER',
ADD COLUMN IF NOT EXISTS refresh_token VARCHAR(255) DEFAULT NULL;

3. Configurar o application.properties
Atualize o arquivo src/main/resources/application.properties com as configurações do seu banco de dados e a chave secreta JWT. Certifique-se de substituir sua_senha pela senha do seu usuário MySQL.

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

4. Rode a aplicação
Navegue até a raiz do projeto e execute o comando Maven:

mvn spring-boot:run

A API estará disponível em: http://localhost:8080

🧑‍💻 Autor
Bruna Mendes - Projeto Final Bloco 02 - Junho / 2026

📚 Referências
[1] Repositório GitHub: bruna-dsmendes/projeto_final_bloco_02
