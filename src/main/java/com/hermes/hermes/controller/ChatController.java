package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "v1/chat")
@RequiredArgsConstructor
public class ChatController {


    @PostMapping("/estabelecer-conexao")
    public ResponseEntity<HttpStatus> estabelecerConexao(Long userId){
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/receber-mensagem/{userId}")
    public ResponseEntity<String> sendMessage(@PathVariable("userId") Long userId, @RequestBody MessageDto message) throws InterruptedException {
        List<String> mensagens = new ArrayList<>();

        //todo: implementar comunicacao com api python aqui
        mensagens.add("Mensagem recebida com sucesso!");
        Thread.sleep(3000L);

        return ResponseEntity.ok(mensagens.getFirst());
    }

}
