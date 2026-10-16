# Spring AI RAG Assistant

Assistente de atendimento interno para um banco digital, com **RAG (Retrieval-Augmented Generation)** sobre as políticas da empresa, **memória de conversa**, **tool calling** para dados em tempo real e **guardrails de LGPD**. Roda 100% local com **Ollama**, sem enviar dados a terceiros.

> Segmento: **Financeiro / IA Generativa aplicada**

## Stack
Java 21 • Spring Boot 3.4 • **Spring AI 1.0** (ChatClient, Advisors, Tools) • Ollama (llama3.2 + nomic-embed-text) • PostgreSQL + **pgvector** (índice HNSW) • Maven • Docker Compose • JUnit 5 • Mockito

## Arquitetura

```
             ┌──────────────── AssistantService (ChatClient) ─────────────────┐
 pergunta ─► │ PromptGuard ─► MessageChatMemoryAdvisor ─► QuestionAnswerAdvisor │ ─► Ollama (LLM)
             │  (mascara CPF/cartão,     (histórico por        (busca top-4     │       │
             │   bloqueia injection)      conversationId)       no pgvector)    │       ▼
             └──────────────────────────────────────────────────────────────────┘  TicketTools
                                                                                  (@Tool: status do protocolo)
 ingestão:  .md / upload ─► TextReader ─► TokenTextSplitter ─► embeddings ─► pgvector (HNSW, cosseno)
```

### Destaques
- **RAG com metadados** (`source`, `category`) — permite citar a política de origem e filtrar por categoria.
- **Tool calling**: o modelo consulta o status real de um protocolo em vez de "alucinar".
- **Guardrails antes do LLM**: mascaramento de CPF e número de cartão (LGPD) e bloqueio de *prompt injection*.
- **Prompt de sistema restritivo**: responde só com base no contexto e admite quando não sabe.
- **Modelo local** (Ollama): dados sensíveis não saem da infraestrutura.

## Como executar
```bash
docker compose up --build        # baixa os modelos na primeira execução

curl -X POST localhost:8080/api/assistant/ask -H 'Content-Type: application/json' \
  -d '{"conversationId":"atd-1","question":"Qual o limite do PIX à noite?"}'

curl -X POST localhost:8080/api/assistant/ask -H 'Content-Type: application/json' \
  -d '{"conversationId":"atd-1","question":"Como está o protocolo 2026-000123?"}'

curl -F file=@minha-politica.md -F category=cartoes localhost:8080/api/assistant/documents
```

## Testes
```bash
mvn test   # guardrails, chunking com metadados e ferramentas (sem depender do LLM)
```
