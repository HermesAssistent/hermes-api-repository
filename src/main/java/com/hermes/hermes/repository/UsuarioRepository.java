package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUid(String uid);
    Optional<Usuario> findByEmail(String email);
}
