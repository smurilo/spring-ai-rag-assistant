package com.smsmurilo.assistant;

import com.smsmurilo.assistant.tools.TicketTools;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketToolsTest {

    @Test
    void returnsKnownTicketAndHandlesUnknownOnes() {
        var tools = new TicketTools();
        assertThat(tools.ticketStatus(" 2026-000123 ").status()).isEqualTo("EM_ANALISE");
        assertThat(tools.ticketStatus("0000").status()).isEqualTo("NAO_ENCONTRADO");
    }
}
