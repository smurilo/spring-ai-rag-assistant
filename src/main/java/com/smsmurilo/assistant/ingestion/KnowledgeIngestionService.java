package com.smsmurilo.assistant.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/** ETL for the knowledge base: read → chunk → embed (done by the VectorStore) → store. */
@Service
public class KnowledgeIngestionService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeIngestionService.class);

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter = new TokenTextSplitter(400, 100, 20, 1000, true);

    public KnowledgeIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public int ingest(Resource resource, String category) {
        var reader = new TextReader(resource);
        reader.getCustomMetadata().put("category", category);
        reader.getCustomMetadata().put("source", resource.getFilename());

        List<Document> chunks = chunk(reader.get());
        vectorStore.add(chunks);
        log.info("Ingeridos {} trechos de {}", chunks.size(), resource.getFilename());
        return chunks.size();
    }

    List<Document> chunk(List<Document> documents) {
        return splitter.apply(documents);
    }
}
