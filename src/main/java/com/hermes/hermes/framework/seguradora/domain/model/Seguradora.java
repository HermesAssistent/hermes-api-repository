package com.hermes.hermes.framework.seguradora.domain.model;
import com.hermes.hermes.framework.abstracts.Entidade;
import com.hermes.hermes.framework.usuario.domain.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seguradora extends Entidade {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seg_seguradora_seq")
    @SequenceGenerator(name = "seg_seguradora_seq", sequenceName = "seg_seguradora_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true, length = 20)
    private String cnpj;
    private String contato;
    @Column(nullable = true)
    private String razaoSocial;
    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private Usuario usuario;
}
