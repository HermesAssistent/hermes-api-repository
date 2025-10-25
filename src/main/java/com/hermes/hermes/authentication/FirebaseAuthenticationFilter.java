package com.hermes.hermes.authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hermes.hermes.domain.model.usuario.Usuario;
import com.hermes.hermes.exception.AuthenticationException;
import com.hermes.hermes.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private final UsuarioService usuarioService;

    public FirebaseAuthenticationFilter(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            log.info("Token Firebase validado para UID: {}", uid);
            Usuario usuario = usuarioService.findByUid(uid);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            List.of(new SimpleGrantedAuthority(usuario.getRole()))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Autenticação configurada para usuário: {}", usuario.getEmail());

            filterChain.doFilter(request, response);
        } catch (FirebaseAuthException e) {
            log.error("Erro ao validar token Firebase: {}", e.getMessage());
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED,"Sessão expirada!");
        }
    }
}
