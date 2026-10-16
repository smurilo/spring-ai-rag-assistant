package com.smsmurilo.assistant;

import com.smsmurilo.assistant.ingestion.KnowledgeIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class KnowledgeChunkingTest {

    @Test
    @SuppressWarnings("unchecked")
    void ingestionSplitsDocumentsAndKeepsSourceMetadata() {
        var store = mock(VectorStore.class);
        var service = new KnowledgeIngestionService(store);

        int chunks = service.ingest(new ClassPathResource("knowledge/politica-pix.md"), "politicas");

        assertThat(chunks).isPositive();
        var captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(store).add(captor.capture());
        Document first = (Document) captor.getValue().getFirst();
        assertThat(first.getMetadata()).containsEntry("source", "politica-pix.md").containsEntry("category", "politicas");
    }
}
