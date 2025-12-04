package com.hermes.hermes.controller;

import com.hermes.hermes.domain.model.chat.ChatSession;
import com.hermes.hermes.domain.model.chat.Foto;
import com.hermes.hermes.domain.model.sinistro.SinistroBase;
import com.hermes.hermes.service.chat.ChatSessionService;
import com.hermes.hermes.service.FotoService;
import com.hermes.hermes.service.sinistro.SinistroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/fotos")
@RequiredArgsConstructor
public class FotoController {

    private final FotoService fotoService;
    private final ChatSessionService chatSessionService;
    private final SinistroService sinistroService;

    @PostMapping("/upload")
    public ResponseEntity<Foto> uploadFoto(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "sessionId", required = false) Long sessionId,
            @RequestParam(value = "sinistroId", required = false) Long sinistroId,
            @RequestParam(value = "tipoSinistro", required = false) String tipoSinistro
    ) {
        ChatSession session = null;
        SinistroBase sinistro = null;

        if (sessionId != null) {
            session = chatSessionService.buscarPorId(sessionId);
        }

        if (sinistroId != null) {
            sinistro = sinistroService.buscarPorId(sinistroId, tipoSinistro);
        }

        var foto = fotoService.salvarFoto(arquivo, session, sinistro);

        return ResponseEntity.ok(foto);
    }
}
