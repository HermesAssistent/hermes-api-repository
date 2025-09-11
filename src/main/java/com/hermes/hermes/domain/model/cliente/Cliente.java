package com.hermes.hermes.domain.model.cliente;

import com.hermes.hermes.domain.model.abstracts.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cli_cliente_seq")
    @SequenceGenerator(name = "cli_cliente_seq", sequenceName = "cli_cliente_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    private String veiculo;
}
