package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);

    @Query("SELECT c FROM Cliente c " +
            "JOIN Usuario u ON c.usuario.id = u.id " +
            "WHERE u.ativo = true")
    List<Cliente> findAllActives();

    boolean existsByCpf(String cpf);
}
