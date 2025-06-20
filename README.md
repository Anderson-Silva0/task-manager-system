# Task Manager System 🗂️

Bem-vindo ao Task Manager System! 🚀

**Sistema de gerenciamento de tarefas com microsserviços em Spring Boot, Swagger, PostgreSQL, testes automatizados e Docker. Back end do sistema Task Manager.**

Este repositório contém o backend com dois microservices (**user-service** e **task-service**, ambos localizados na mesma pasta para facilitar a execução do projeto). O frontend está em outro repositório e deve ser clonado dentro deste para execução via Docker Compose.

🚀 **Visão Geral**

- **Repositório Backend**: https://github.com/Anderson-Silva0/task-manager-system
- **Repositório Frontend**: https://github.com/Anderson-Silva0/task-manager-ui

Este README mostra como clonar e executar o sistema completo (backend + frontend) rapidamente.

------

## 📥 Como clonar

1. **Clone o repositório backend**:

   ```bash
   git clone https://github.com/Anderson-Silva0/task-manager-system.git
   cd task-manager-system
   ```

2. **Clone o repositório frontend dentro da pasta do backend**:

   ```bash
   git clone https://github.com/Anderson-Silva0/task-manager-ui.git
   ```

> Estrutura esperada após clone:
>
> ```plain
> task-manager-system/
> ├── user-service/        # Microservice de usuários
> ├── task-service/        # Microservice de tarefas
> └── task-manager-ui/     # Aplicação frontend
> ```

------

## 🐳 Pré-requisitos

- Docker
- Docker Compose

> Tudo está containerizado; nenhum outro pré-requisito local é necessário.

------

## 🐳 Executando com Docker Compose

1. No diretório raiz (`task-manager-system`), execute para construir imagens e ver logs em primeiro plano:

   ```bash
   docker compose up --build
   ```

   - Isso mostra logs em tempo real, incluindo execução de testes (unitários, integração e e2e) configurados nos containers.

2. Para parar e remover containers, redes e volumes associados:

   ```bash
   docker compose down -v
   ```

> Quem clonar vê Dockerfiles e configurações em cada pasta para detalhes de como os serviços e testes são executados.

------

## 🌐 URLs de Acesso ao Sistema

Após iniciar os containers via Docker Compose, abra no navegador:

- **Frontend Angular**: http://localhost:4200/

- **User Service Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **Task Service Swagger UI**: http://localhost:8082/swagger-ui/index.html

------

## 📬 Contato

- Anderson Silva ([LinkedIn](https://www.linkedin.com/in/anderson-da-silva-004a0320b/))

------

✨ Pronto para usar! Se precisar de detalhes extras, explore o código nos diretórios. 😊