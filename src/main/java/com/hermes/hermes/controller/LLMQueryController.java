package com.hermes.hermes.controller;

import com.hermes.hermes.service.LLMQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/chat-sql")
public class LLMQueryController {

    private final LLMQueryService llmService;

    public LLMQueryController(LLMQueryService llmService) {
        this.llmService = llmService;
    }

    @GetMapping("/{pergunta}")
    public ResponseEntity<String> perguntar(@PathVariable String pergunta) throws Exception {
        return ResponseEntity.ok(llmService.responderPergunta(pergunta));
    }
}
