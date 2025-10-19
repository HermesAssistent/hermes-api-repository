package com.hermes.hermes.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.hermes.hermes.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    @Override
    public UserRecord createUser(UserRecord.CreateRequest request) {
        log.info("Criando usuário no Firebase com e-mail");
        try {
            return FirebaseAuth.getInstance().createUser(request);
        } catch (FirebaseAuthException e) {
            log.error("Erro ao criar usuário no Firebase: {}", e.getMessage());
            throw new AuthenticationException("Erro ao criar usuário no Firebase: " + e.getMessage());
        }
    }

    @Override
    public FirebaseToken verifyIdToken(String idToken) {
        log.info("Verificando token Firebase");
        if (idToken == null || idToken.isEmpty()) {
            log.error("Token Firebase não fornecido");
            throw new AuthenticationException("Token Firebase não fornecido ou vazio");
        }
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            log.error("Erro ao verificar token Firebase: {}", e.getMessage());
            throw new AuthenticationException("Token Firebase inválido: " + e.getMessage());
        }
    }

    @Override
    public void revokeRefreshTokens(String uid) {
        log.info("Revogando refresh tokens para UID: {}", uid);
        if (uid == null || uid.isEmpty()) {
            log.error("UID não fornecido para revogação de tokens");
            throw new AuthenticationException("UID não fornecido para revogação de tokens");
        }
        try {
            FirebaseAuth.getInstance().revokeRefreshTokens(uid);
        } catch (FirebaseAuthException e) {
            log.error("Erro ao revogar refresh tokens: {}", e.getMessage());
            throw new AuthenticationException("Erro ao revogar refresh tokens: " + e.getMessage());
        }
    }
}