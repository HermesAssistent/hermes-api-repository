package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.chat.ChatSession;
import com.hermes.hermes.domain.model.chat.Foto;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.repository.FotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FotoService {

    private final FotoRepository fotoRepository;

    private static final String UPLOAD_DIR = "uploads/fotos";

    public Foto salvarFoto(MultipartFile arquivo, ChatSession session, Sinistro sinistro) throws IOException {
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String nomeArquivo = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
        Path caminho = Paths.get(UPLOAD_DIR, nomeArquivo);

        arquivo.transferTo(caminho);

        Foto foto = Foto.builder()
                .nomeArquivo(nomeArquivo)
                .caminhoArquivo(caminho.toString())
                .chatSession(session)
                .sinistro(sinistro)
                .build();

        return fotoRepository.save(foto);
    }
}
