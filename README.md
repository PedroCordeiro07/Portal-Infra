# Portal Infra

> Uma aplicação web para registro e compartilhamento de problemas relacionados à infraestrutura urbana.

## Sobre

O Portal Infra é um projeto acadêmico desenvolvido com o objetivo de facilitar o registro e a visualização de problemas de infraestrutura urbana.

A plataforma permite que usuários registrem ocorrências com informações como descrição, imagens, gravidade e localização, disponibilizando esses dados para consulta e interação.

O projeto está alinhado ao **ODS 11 — Cidades e Comunidades Sustentáveis**, buscando contribuir para uma melhor visualização das condições dos espaços públicos.

## Versão Atual

**Sprint 5 — Interações e Moderação**

O sistema agora possui os módulos de Comentários e Denúncias (Moderação) concluídos. Os usuários podem interagir em threads com proteção contra aninhamento infinito, postar provas de resolução com fotos obrigatórias e editar seus conteúdos de forma segura usando requisições PATCH. O sistema também conta com um painel administrativo robusto que permite gerenciar denúncias através de filas separadas e apagar conteúdos maliciosos com exclusão em cascata controlada, sem vazar armazenamento na nuvem.

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
- [x] Publicação de reclamações com upload e exclusão segura de imagens (Cloudinary)
- [x] Edição parcial de reclamações (`PATCH`) com substituição segura de fotos
- [x] Feed de reclamações otimizado com Paginação
- [x] Exclusão de reclamações e imagens vinculadas (restrita ao autor ou Administrador)
- [x] Sistema de comentários em thread achatada
- [x] Marcação de comentário como "Resolução" com exigência obrigatória de foto
- [x] Edição parcial de comentários (`PATCH`) com flag para remoção opcional de foto
- [x] Sistema de denúncias (Design XOR: Reclamações ou Comentários) com regras anti-spam e bloqueio de autorrelato
- [x] Filas de moderação administrativa para aprovação/rejeição de denúncias
- [x] Inspeção de segurança a nível de bytes (`ImageIO.read()`) para uploads
- [x] Mapa interativo
- [ ] Filtro por bairro

## Histórico de Sprints

### Sprint 1 — Estrutura Inicial
A estrutura inicial do projeto foi criada, estabelecendo o frontend, backend e a comunicação entre as partes da aplicação.

### Sprint 2 — Autenticação
O sistema de usuários foi implementado, incluindo cadastro, login, verificação de e-mail e recuperação de senha.

### Sprint 3 — Reclamações
Implementação completa do sistema de criação, listagem paginada, exclusão e alteração de status de reclamações de infraestrutura com upload de evidências.

### Sprint 4 — Mapa e Localização
Implementação do mapa interativo, marcadores e filtragem das ocorrências por localização. *(Nota: Planejado para ser executado com o Front-end)*

### Sprint 5 — Interações
Implementação de comentários, respostas, validações de arquivo e sistema avançado de moderação/denúncias comunitárias.

## Roadmap

### Reclamações
- [x] Criar reclamações
- [x] Adicionar imagens
- [x] Definir nível de gravidade
- [x] Registrar localização
- [x] Visualizar reclamações no feed
- [x] Editar reclamações (PATCH)

### Mapa
- [ ] Exibir reclamações no mapa
- [ ] Adicionar marcadores
- [ ] Filtrar por bairro
- [ ] Integrar Google Maps JavaScript API

### Interações
- [x] Adicionar comentários
- [x] Responder comentários (Flattened Threads)
- [x] Denunciar reclamações
- [x] Denunciar comentários
- [x] Painel de Resolução de Denúncias (Admin)

## Tecnologias

- Java 21
- Spring Boot
- Spring Security
- Maven
- PostgreSQL (Neon)
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
            │       │   ├── ComentarioController.java
            │       │   ├── DenunciaController.java
            │       │   ├── ReclamacaoController.java
            │       │   └── UsuarioController.java
            │       ├── dto/
            │       │   ├── ComentarioRequest.java
            │       │   ├── ComentarioResponse.java
            │       │   ├── DenunciaRequest.java
            │       │   ├── DenunciaResponse.java
            │       │   ├── ReclamacaoRequest.java
            │       │   └── ReclamacaoResponse.java
            │       ├── model/
            │       │   ├── Comentario.java
            │       │   ├── Denuncia.java
            │       │   ├── EmailVerification.java
            │       │   ├── PasswordResetToken.java
            │       │   ├── Reclamacao.java
            │       │   └── Usuario.java
            │       ├── repository/
            │       │   ├── ComentarioRepository.java
            │       │   ├── DenunciaRepository.java
            │       │   ├── EmailVerificationRepository.java
            │       │   ├── PasswordResetTokenRepository.java
            │       │   ├── ReclamacaoRepository.java
            │       │   └── UsuarioRepository.java
            │       └── service/
            │           ├── ComentarioService.java
            │           ├── DenunciaService.java
            │           ├── EmailService.java
            │           ├── ReclamacaoService.java
            │           └── UsuarioService.java
            └── resources/
               └── application.properties