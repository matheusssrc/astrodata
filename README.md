
# AstroData - Sistema de Gestão de Corpos Celestes

O **AstroData** é um sistema desktop desenvolvido em **Java** para o gerenciamento e a catalogação de corpos celestes (planetas e estrelas), aplicando conceitos de **Orientação a Objetos**, uso de **Java Streams** (programação funcional) e **persistência de dados híbrida** (Banco de Dados + Arquivos).

A ideia é ter uma aplicação robusta, resiliente e pronta para cenário real: se o banco cai, o sistema continua operando em modo seguro sem travar a operação.

---

## Como funciona

1. O **AstroData** inicia verificando a disponibilidade de conexão com o banco de dados **PostgreSQL**.  
2. Caso a conexão falhe, o sistema ativa automaticamente um mecanismo de **fallback**, tentando ler um arquivo local (TXT) ou carregando os dados em memória RAM para garantir a execução.  
3. A interface gráfica (Swing) é carregada, apresentando uma tabela com os dados e painéis de controle.  
4. O usuário pode cadastrar, remover, filtrar e visualizar detalhes de cada corpo celeste.  
5. O sistema realiza cálculos em tempo real (média de massa, rankings etc.) utilizando **Java Streams**.  
6. Os dados podem ser exportados para um arquivo **CSV** compatível com planilhas externas.

---

## Funcionalidades

- **Interface gráfica (GUI)** em Java Swing, com visual nativo do sistema operacional.  
- **Persistência robusta** com suporte a banco de dados relacional **PostgreSQL**.  
- **Exportação de dados** para relatórios em formato **CSV**.  
- **Modo de segurança (fallback)** garantindo execução mesmo sem banco de dados (TXT/RAM).  
- **Estatísticas avançadas**: soma, média, contagem e identificação de planetas potencialmente habitáveis.  
- **Busca e filtros** com pesquisa em tempo real por nome e tipo de corpo celeste.  
- **Validação de dados** com regras de negócio no cadastro (massa, distância, tipo espectral etc.).

---

## Organização do projeto

O código-fonte segue um padrão inspirado em **MVC (Model-View-Controller)**, facilitando manutenção e evolução:

- `modelo/`  
  Classes de domínio (`CorpoCeleste`, `Planeta`, `Estrela`) e o **Gerenciador**, que concentra as regras de negócio.

- `gui/`  
  Telas da interface visual (janelas, diálogos e painéis Swing).

- `persistencia/`  
  Camada de acesso a dados (DAO e conexão JDBC com PostgreSQL, além do manejo de arquivos TXT/CSV).

- `principal/`  
  Ponto de entrada da aplicação (classe `MainApp`).

- `lib/`  
  Dependências externas, incluindo o **driver JDBC do PostgreSQL**.

### Estrutura de pastas (visão geral)

```text
.
├── src/
│   └── br/com/projeto/
│       ├── gui/          # Classes da Interface Grafica (JFrames, Dialogs)
│       ├── modelo/       # Entidades e Regras de Negocio (Gerenciador)
│       ├── persistencia/ # Camada de Acesso a Dados (DAO, Conexao)
│       └── principal/    # Ponto de entrada da aplicacao
├── dados/                # Armazenamento de arquivos locais (Backup/Fallback)
├── imagens/              # Icone do AstroData
├── lib/                  # Bibliotecas externas (Driver JDBC)
├── nbproject/            # Configuracoes do projeto NetBeans
├── script_banco.sql      # Scripts DDL/DML para configuracao do ambiente
├── build.xml             # Script de construcao Ant
├── manifest.mf           # Metadados do projeto
└── README.md             # Documentacao do projeto
```

---

## Configuração do banco de dados (Dump SQL)

Para utilizar o modo completo com banco de dados PostgreSQL, o repositório inclui um script de criação e carga inicial.

**Arquivo:** `script_banco.sql`

1. Abra seu gerenciador de banco (por exemplo, **pgAdmin** ou **DBeaver**).  
2. Execute o script `script_banco.sql`.  
   - Ele cria o usuário de acesso, o banco de dados e popula a tabela com dados de teste.  
3. Caso você não queira configurar o banco de dados, o **AstroData** funciona normalmente em **modo offline**, via TXT ou memória.

---

## Requisitos

- **Java JDK 17 ou superior** (projeto originalmente configurado para JDK 25).  
- **Driver JDBC PostgreSQL** (já incluído na pasta `lib`).  
- **PostgreSQL** instalado (apenas se você optar por usar o modo conectado ao banco).

---

## Diferenciais do projeto

- Foco em **resiliência** e **experiência do usuário (UX)**, garantindo que a aplicação continue operando mesmo em cenários de falha de infraestrutura.  
- Arquitetura em **camadas** baseada em MVC, facilitando refatorações, novas features e integrações futuras.  
- Persistência **híbrida** (Banco + Arquivo + RAM), simulando um ambiente mais próximo de produção.  
- Uso de **Java Streams** para estatísticas em tempo real, trazendo um toque de programação funcional ao projeto.  

---

## Licença

MIT License

---

**Autores:** Matheus Rossi Carvalho e Rafaela Aguirre Clamer  
**Contato (Matheus):** [LinkedIn - Matheus Rossi Carvalho](https://www.linkedin.com/in/matheus-rossi-carvalho/)
