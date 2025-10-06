package com.hermes.hermes.domain.model.cliente;

import com.hermes.hermes.domain.model.usuario.Usuario;
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
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cli_cliente_seq")
    @SequenceGenerator(name = "cli_cliente_seq", sequenceName = "cli_cliente_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true, length = 11)
    private String cpf;
    private String veiculo;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private Usuario usuario;
}
