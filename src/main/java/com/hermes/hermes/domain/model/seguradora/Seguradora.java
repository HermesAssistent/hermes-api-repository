package com.hermes.hermes.domain.model.seguradora;
import com.hermes.hermes.domain.model.abstracts.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seguradora extends Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seg_seguradora_seq")
    @SequenceGenerator(name = "seg_seguradora_seq", sequenceName = "seg_seguradora_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true, length = 14)
    private String cnpj;
    private String contato;

    @Override
    public String getRole() {
        return "ROLE_SEGURADORA";
    }
}
