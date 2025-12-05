package com.hermes.hermes.framework.chat.service;

import com.hermes.hermes.framework.chat.domain.model.ChatSession;
import com.hermes.hermes.framework.chat.domain.model.Foto;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.framework.exception.FileStorageException;
import com.hermes.hermes.framework.chat.repository.FotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FotoService {

    private final FotoRepository fotoRepository;

    private static final String UPLOAD_DIR = "uploads/fotos";

    public Foto salvarFoto(MultipartFile arquivo, ChatSession session, SinistroBase sinistro) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo vazio");
        }

        try {
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
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo: {}", e.getMessage());
            throw new FileStorageException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    public void relacionarSinistro(ChatSession session, SinistroBase sinistro) {
        List<Foto> fotos = fotoRepository.findAllByChatSession(session);
        for (Foto foto : fotos) {
            foto.setSinistro(sinistro);
            fotoRepository.save(foto);
        }
    }
}
