package com.smsmurilo.assistant.ingestion;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "assistant.ingest-on-startup", havingValue = "true")
class StartupIngestion implements ApplicationRunner {

    private final KnowledgeIngestionService ingestion;

    StartupIngestion(KnowledgeIngestionService ingestion) {
        this.ingestion = ingestion;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (Resource r : new PathMatchingResourcePatternResolver().getResources("classpath:knowledge/*.md")) {
            ingestion.ingest(r, "politicas");
        }
    }
}
