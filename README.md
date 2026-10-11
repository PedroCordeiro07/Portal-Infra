# Portal Infra

> Uma aplicação web para registro e compartilhamento de problemas relacionados à infraestrutura urbana.

## Sobre

O Portal Infra é um projeto acadêmico desenvolvido com o objetivo de facilitar o registro e a visualização de problemas de infraestrutura urbana.

A plataforma permite que usuários registrem ocorrências com informações como descrição, imagens, gravidade e localização, disponibilizando esses dados para consulta e interação.

O projeto está alinhado ao **ODS 11 — Cidades e Comunidades Sustentáveis**, buscando contribuir para uma melhor visualização das condições dos espaços públicos.

## Versão Atual

**Sprint 3 — Reclamações**

O sistema agora possui o módulo de Reclamações concluído. Os usuários podem relatar problemas de infraestrutura com fotos, enquanto o sistema garante segurança e escalabilidade através de paginação, integração com armazenamento em nuvem e restrições de acesso (RBAC) para administradores.

### Funcionalidades Atuais

- [x] Cadastro de usuários
- [x] Login
- [x] Logout
- [x] Criptografia de senhas
- [x] Verificação de e-mail
- [x] Recuperação de senha
- [x] Redefinição de senha através de token
- [x] Persistência de usuários no PostgreSQL
- [x] Configuração de segurança com Spring Security
- [x] Publicação de reclamações
- [x] Upload e exclusão segura de imagens (Cloudinary)
- [x] Feed de reclamações otimizado com Paginação
- [x] Exclusão de reclamações (restrita ao autor ou Administrador)
- [x] Atualização de status das ocorrências (restrita a Administradores)
- [ ] Comentários e respostas
- [ ] Denúncias
- [ ] Mapa interativo
- [ ] Filtro por bairro

## Histórico de Sprints

### Sprint 1 — Estrutura Inicial
A estrutura inicial do projeto foi criada, estabelecendo o frontend, backend e a comunicação entre as partes da aplicação.

### Sprint 2 — Autenticação
O sistema de usuários foi implementado, incluindo cadastro, login, verificação de e-mail e recuperação de senha.

### Sprint 3 — Reclamações
Implementação completa do sistema de criação, listagem paginada, exclusão e alteração de status de reclamações de infraestrutura com upload de evidências.

### Sprint 4 — Mapa e Localização
Implementação do mapa interativo, marcadores e filtragem das ocorrências por localização.

### Sprint 5 — Interações
Implementação de comentários, respostas e sistema de denúncias comunitárias.

## Roadmap

### Reclamações
- [x] Criar reclamações
- [x] Adicionar imagens
- [x] Definir nível de gravidade
- [x] Registrar localização
- [x] Visualizar reclamações no feed

### Mapa
- [ ] Exibir reclamações no mapa
- [ ] Adicionar marcadores
- [ ] Filtrar por bairro
- [ ] Integrar Google Maps JavaScript API

### Interações
- [ ] Adicionar comentários
- [ ] Responder comentários
- [ ] Denunciar reclamações
- [ ] Denunciar comentários

## Tecnologias

- Java 21
- Spring Boot
- Spring Security
- Maven
- PostgreSQL
- Cloudinary (Armazenamento de Imagens)
- HTML5
- CSS3
- JavaScript
- Google Maps JavaScript API
- Docker
- Render
- Git/GitHub

## Estrutura do Projeto

```text
arthx06-portal-infra/
├── index.html
├── login.html
├── reclamacoes.html
├── reclamacoes.css
├── redefinir-senha.html
├── style.css
│
└── backend/
    ├── DockerFile
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            │   └── org/example/portalinfra/
            │       ├── PortalInfraApplication.java
            │       ├── config/
            │       │   ├── SecurityConfig.java
            │       │   └── CloudinaryConfig.java
            │       ├── controller/
            │       │   ├── ReclamacaoController.java
            │       │   └── UsuarioController.java
            │       ├── model/
            │       │   ├── dto/
            │       │   │   └── ReclamacaoResponse.java
            │       │   ├── EmailVerification.java
            │       │   ├── PasswordResetToken.java
            │       │   ├── Reclamacao.java
            │       │   └── Usuario.java
            │       ├── repository/
            │       │   ├── EmailVerificationRepository.java
            │       │   ├── PasswordResetTokenRepository.java
            │       │   ├── ReclamacaoRepository.java
            │       │   └── UsuarioRepository.java
            │       └── service/
            │           ├── EmailService.java
            │           ├── ReclamacaoService.java
            │           └── UsuarioService.java
            └── resources/
               └── application.properties