package com.smsmurilo.assistant.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Tools the model may call (function calling). They give the LLM live, authoritative data
 * instead of letting it guess. Backed by an in-memory map here; in production, a service client.
 */
@Component
public class TicketTools {

    public record TicketStatus(String protocol, String status, String sla, String team) {}

    private final Map<String, TicketStatus> tickets = Map.of(
            "2026-000123", new TicketStatus("2026-000123", "EM_ANALISE", "2 dias úteis", "Contestação de compras"),
            "2026-000456", new TicketStatus("2026-000456", "CONCLUIDO", "-", "Portabilidade de salário"));

    @Tool(description = "Consulta o status de um protocolo de atendimento pelo número do protocolo")
    public TicketStatus ticketStatus(@ToolParam(description = "Número do protocolo, ex: 2026-000123") String protocol) {
        var ticket = tickets.get(protocol.trim());
        return ticket != null ? ticket : new TicketStatus(protocol, "NAO_ENCONTRADO", "-", "-");
    }
}
