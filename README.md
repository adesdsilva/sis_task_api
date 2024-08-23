# sis_task_manager

Aqui está um modelo de README para o seu projeto "Sistema de Gerenciamento de Tarefas":

---

# Sistema de Gerenciamento de Tarefas

Este é um sistema de gerenciamento de tarefas desenvolvido com Java Spring Boot e Thymeleaf. O projeto inclui funcionalidades para criar, atualizar, deletar e visualizar tarefas e subtarefas, além de suporte para filtragem e ordenação de tarefas.

## Tecnologias

O projeto utiliza as seguintes tecnologias:

- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA**: Biblioteca para acesso e manipulação de dados em bancos de dados relacionais.
- **Spring Web**: Módulo para criar APIs RESTful.
- **Spring Thymeleaf**: Motor de templates para renderização de páginas HTML.
- **Spring Validation**: Biblioteca para validação de dados.
- **Springdoc OpenAPI**: Geração de documentação Swagger para APIs.
- **PostgreSQL**: Banco de dados relacional utilizado para armazenamento.
- **H2**: Banco de dados em memória utilizado para testes.
- **Maven**: Ferramenta de automação de builds e gerenciamento de dependências.

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Configurações para PostgreSQL

Adicione as seguintes configurações no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sis_task
spring.datasource.username=postgres
spring.datasource.password=admin # configurer sua senha corretamente
spring.datasource.driver-class-name=org.postgresql.Driver
```

## Configurações para H2 (Testes)

Adicione as seguintes configurações no arquivo `application_test.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

## Iniciando a Aplicação

Para iniciar a aplicação, você pode usar Maven ou Java:

- **Usando Maven**:
  ```bash
  mvn spring-boot:run
  ```

- **Usando Java**:
  ```bash
  java -jar target/sis_task_manager.war
  ```

## Documentação da API

Você pode acessar a documentação da API usando Swagger:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Rotas dos Controladores

Aqui estão as rotas disponíveis para cada controlador:

### TaskController

- **Criar Tarefa**
  - **Método**: POST
  - **Rota**: `/tasks/{listId}`
  - **Exemplo de JSON**:
    ```json
    {
      "title": "Reunião de trabalho",
      "description": "Discussão sobre o novo projeto",
      "dueDate": "29-08-2024",
      "status": "PENDING"
    }
    ```

- **Obter Tarefas por Lista**
  - **Método**: GET
  - **Rota**: `/tasks/list/{listId}`
  - **Parâmetros**: `page` (opcional)

- **Atualizar Tarefa**
  - **Método**: PUT
  - **Rota**: `/tasks/{taskId}`
  - **Exemplo de JSON**:
    ```json
    {
      "title": "Nova reunião",
      "description": "Discussão sobre o projeto atual",
      "dueDate": "30-08-2024",
      "status": "COMPLETED"
    }
    ```

- **Deletar Tarefa**
  - **Método**: DELETE
  - **Rota**: `/tasks/{taskId}`

- **Completar Tarefa**
  - **Método**: PATCH
  - **Rota**: `/tasks/{taskId}/complete`

- **Filtrar Tarefas**
  - **Método**: GET
  - **Rota**: `/tasks/filter`
  - **Parâmetros**: `completed` (opcional), `favorite` (opcional)

- **Filtrar Tarefas Concluídas**
  - **Método**: GET
  - **Rota**: `/tasks/completed`
  - **Parâmetros**: `completed` (opcional)

- **Filtrar Tarefas Pendentes**
  - **Método**: GET
  - **Rota**: `/tasks/pending`
  - **Parâmetros**: `pending` (opcional)

- **Filtrar Tarefas Favoritas**
  - **Método**: GET
  - **Rota**: `/tasks/favorite`
  - **Parâmetros**: `favorite` (opcional)

- **Excluir Tarefas Concluídas de uma Lista**
  - **Método**: DELETE
  - **Rota**: `/tasks/lists/{taskListId}/completed`

- **Atualizar Status da Tarefa**
  - **Método**: PATCH
  - **Rota**: `/tasks/{taskId}/status`
  - **Exemplo de JSON**:
    ```json
    {
      "status": "COMPLETED"
    }
    ```

- **Favoritar/Desfavoritar Tarefa**
  - **Método**: PATCH
  - **Rota**: `/tasks/{taskId}/favorite`
  - **Exemplo de JSON**:
    ```json
    {
      "isFavorite": true
    }
    ```

### TaskListController

- **Criar Lista de Tarefas**
  - **Método**: POST
  - **Rota**: `/task-lists`
  - **Exemplo de JSON**:
    ```json
    {
      "title": "Lista de Projetos",
      "isFavorite": false
    }
    ```

- **Obter Listas de Tarefas Ordenadas**
  - **Método**: GET
  - **Rota**: `/task-lists/ordered`
  - **Parâmetros**: `page` (opcional), `size` (opcional)

- **Obter Todas as Listas de Tarefas**
  - **Método**: GET
  - **Rota**: `/task-lists`
  - **Parâmetros**: `page` (opcional), `size` (opcional)

### SubTaskController

- **Criar Subtarefa**
  - **Método**: POST
  - **Rota**: `/subtasks`
  - **Exemplo de JSON**:
    ```json
    {
      "title": "Preparar apresentação",
      "description": "Preparar slides para a reunião",
      "dueDate": "28-08-2024",
      "status": "PENDING"
    }
    ```

- **Obter Subtarefa por ID**
  - **Método**: GET
  - **Rota**: `/subtasks/{id}`

## Execução de Testes

Os testes estão localizados nos seguintes diretórios:

- **Testes de Controlador**: `src/test/java/setecolinas/com/sis_task_manager/controllerTest`
- **Testes de Repositório**: `src/test/java/setecolinas/com/sis_task_manager/repositoryTest`
- **Testes de Serviço**: `src/test/java/setecolinas/com/sis_task_manager/serviceTest`

Para executar os testes, use o comando Maven:

```bash
mvn test
```

## Informações de Contato

**Adelino Santos**

- GitHub: [https://github.com/adesdsilva](https://github.com/adesdsilva)
- Email: adesdsilva@gmail.com
- WhatsApp: (87)99614-3286 https://wa.me/qr/A37WJB66QUXZH1.
- LinkedIn: [[https://www.linkedin.com/in/adelino-santos-9b70b3a7/](https://www.linkedin.com/in/adelino-santos-9b70b3a7/)

