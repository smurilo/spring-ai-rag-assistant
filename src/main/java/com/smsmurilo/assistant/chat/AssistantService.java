package com.smsmurilo.assistant.chat;

import com.smsmurilo.assistant.tools.TicketTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    static final String SYSTEM_PROMPT = """
            Você é o assistente interno de atendimento de um banco digital.
            Responda em português, de forma objetiva, usando APENAS o contexto fornecido e as ferramentas disponíveis.
            Se a resposta não estiver no contexto, diga que não encontrou a informação e sugira abrir um protocolo.
            Nunca invente valores, prazos ou taxas. Cite a política de origem quando possível.
            """;

    private final ChatClient chatClient;
    private final PromptGuard guard;

    public AssistantService(ChatClient.Builder builder, VectorStore vectorStore, ChatMemory chatMemory,
                            TicketTools ticketTools, PromptGuard guard) {
        this.guard = guard;
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().topK(4).similarityThreshold(0.55).build())
                                .build())
                .defaultTools(ticketTools)
                .build();
    }

    public String ask(String conversationId, String question) {
        return chatClient.prompt()
                .user(guard.sanitize(question))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
