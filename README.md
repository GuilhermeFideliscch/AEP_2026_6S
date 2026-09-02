#Rede Saber
Plataforma de compartilhamento de materiais acadêmicos

---

## Objetivo

O **Rede Saber** é uma aplicação desenvolvida como uma Prova de Conceito (PoC) para demonstrar a aplicação prática de conceitos de Engenharia de Software, Programação Orientada a Objetos, banco de dados NoSQL, versionamento e testes automatizados.

A solução permite centralizar materiais acadêmicos e facilitar seu compartilhamento e consulta entre estudantes.

O projeto está alinhado ao:

> **ODS 4 – Educação de Qualidade**

---

## Tecnologias utilizadas

| Tecnologia              | Utilização                                          |
| ----------------------- | --------------------------------------------------- |
| **Java 25**             | Linguagem principal                                 |
| **Spring Boot 4.1.1**   | Desenvolvimento da aplicação                        |
| **Spring Web MVC**      | Construção da API                                   |
| **Spring Data MongoDB** | Persistência dos dados                              |
| **MongoDB**             | Banco de dados NoSQL                                |
| **Spring Security**     | Segurança da aplicação                              |
| **JUnit**               | Testes automatizados                                |
| **Mockito**             | Testes unitários e simulação de dependências        |
| **JaCoCo 0.8.14**       | Medição da cobertura de testes                      |
| **Maven**               | Gerenciamento de dependências e execução do projeto |
| **Lombok**              | Redução de código boilerplate                       |
| **Springdoc OpenAPI**   | Documentação da API                                 |

As tecnologias acima estão configuradas no `pom.xml` do projeto.

---

# Pré-requisitos

Antes de executar o projeto, é necessário possuir as seguintes ferramentas instaladas:

### Java 25

O projeto está configurado para utilizar **Java 25**.

Verifique a instalação com:

```bash
java -version
```

O resultado deve indicar uma versão compatível com Java 25.

### Maven

O projeto possui **Maven Wrapper**, portanto não é obrigatório instalar o Maven separadamente.

O repositório contém:

```text
mvnw
mvnw.cmd
```

Isso permite executar os comandos Maven diretamente pelo projeto.

### MongoDB

O sistema utiliza o **MongoDB** como banco de dados NoSQL.

A aplicação está configurada para acessar:

```text
mongodb://localhost:27017/User
```

Portanto, o MongoDB deve estar em execução localmente antes de iniciar a aplicação.

---

# Clonando o projeto

Clone o repositório:

```bash
git clone https://github.com/GuilhermeFideliscch/AEP_2026_6S.git
```

Entre na pasta:

```bash
cd AEP_2026_6S
```

Para utilizar a versão atualmente em desenvolvimento:

```bash
git checkout desenvolvimento
```

---

# Executando a aplicação

## Windows

No Windows, utilize o Maven Wrapper:

```bash
.\mvnw.cmd spring-boot:run
```

## Linux / macOS

```bash
./mvnw spring-boot:run
```

Após a inicialização, a aplicação ficará disponível na porta padrão do Spring Boot:

```text
http://localhost:8080
```

---

# Testes automatizados

Os testes automatizados fazem parte do projeto e estão localizados em:

```text
src/
└── test/
    └── java/
        └── com/
            └── aep/
                └── redeSaber/
                    ├── controllers/
                    │   └── UserControllerTest.java
                    │
                    └── services/
                        └── UserServiceTest.java
```

Os testes abrangem principalmente as camadas de **Controller** e **Service**.

Os testes de serviço utilizam **JUnit 5, Mockito e AssertJ**, com mocks para simular o comportamento do repositório.

---

## Como executar os testes

Para executar todos os testes:

```bash
./mvnw clean verify
```

O Maven irá compilar o projeto, executar os testes automatizados e apresentar no terminal o resultado da execução.

Um resultado de sucesso deve apresentar:

```text
BUILD SUCCESS
```

---

# Cobertura dos testes

A cobertura dos testes é calculada utilizando o **JaCoCo**.

O projeto está configurado para gerar automaticamente o relatório durante a execução dos testes.

Para executar os testes e gerar o relatório de cobertura:

```bash
./mvnw clean verify
```

O relatório será gerado em:

```text
target/site/jacoco/index.html
```

---

## Como visualizar o relatório do JaCoCo

Depois de executar os testes, abra o arquivo:

```text
target/site/jacoco/index.html
```

no navegador.

Também é possível navegar pelo relatório para visualizar a cobertura por:

* Projeto;
* Pacote;
* Classe;
* Método;
* Linhas de código.

O JaCoCo permite identificar quais partes do código foram executadas pelos testes automatizados.

---

# Evidência da cobertura de testes

Exemplo de relatório do JaCoCo:

![](https://github.com/GuilhermeFideliscch/AEP_2026_6S/blob/desenvolvimento/imgs/ExJacoco.png)

---

# Estrutura do projeto

A estrutura principal do projeto está organizada da seguinte maneira:

```text
AEP_2026_6S/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── aep/
│   │   │           └── redeSaber/
│   │   │               ├── controllers/
│   │   │               ├── models/
│   │   │               ├── repositories/
│   │   │               ├── services/
│   │   │               └── RedeSaberApplication.java
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── aep/
│                   └── redeSaber/
│                       ├── controllers/
│                       ├── services/
│                       └── ...
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

A organização separa responsabilidades entre **controllers, models, repositories e services**, mantendo uma estrutura compatível com a arquitetura utilizada pela aplicação.

---

## Equipe

| RA | Nome |
| 24170521-2 | Guilherme Fidelis Candido Chiquito |
| 24000499-2  | João Ricardo Barbiero de Souza    |
| 24151606-2  | Gabriel Delefrati Ferreira    |
