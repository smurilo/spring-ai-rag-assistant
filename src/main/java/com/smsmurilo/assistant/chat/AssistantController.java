package com.smsmurilo.assistant.chat;

import com.smsmurilo.assistant.ingestion.KnowledgeIngestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
class AssistantController {

    private final AssistantService assistant;
    private final KnowledgeIngestionService ingestion;

    AssistantController(AssistantService assistant, KnowledgeIngestionService ingestion) {
        this.assistant = assistant;
        this.ingestion = ingestion;
    }

    record Question(@NotBlank String conversationId, @NotBlank @Size(max = 2000) String question) {}

    record Answer(String conversationId, String answer) {}

    @PostMapping("/ask")
    Answer ask(@Valid @RequestBody Question q) {
        return new Answer(q.conversationId(), assistant.ask(q.conversationId(), q.question()));
    }

    @PostMapping(value = "/documents", consumes = "multipart/form-data")
    Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "category", defaultValue = "geral") String category) throws IOException {
        var resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        return Map.of("file", String.valueOf(file.getOriginalFilename()), "chunks", ingestion.ingest(resource, category));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail blocked(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
