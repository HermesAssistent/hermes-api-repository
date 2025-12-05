package com.hermes.hermes.framework.usuario.repository;

import com.hermes.hermes.framework.usuario.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUid(String uid);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
