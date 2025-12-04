package com.hermes.hermes.domain.model.oficina;

import com.hermes.hermes.domain.model.prestador.Prestador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.hermes.hermes.domain.model.seguradora.Seguradora;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "oficina")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Oficina extends Prestador {

    @Column(length = 20)
    private String telefone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "oficina_seguradora",
            joinColumns = @JoinColumn(name = "oficina_id"),
            inverseJoinColumns = @JoinColumn(name = "seguradora_id"))
    private Set<Seguradora> seguradoras = new HashSet<>();
    
    /**
     * Avalia a oficina baseado na média de avaliações.
     */
    @Override
    public double avaliar() {
        // TODO: Implementar lógica de avaliação baseada em feedbacks
        return 4.5; // Valor padrão
    }
    
    /**
     * Verifica disponibilidade da oficina.
     */
    @Override
    public boolean verificarDisponibilidade() {
        // TODO: Implementar verificação de agenda/capacidade
        return true; // Valor padrão
    }
    
    /**
     * Verifica se a oficina é credenciada por uma seguradora.
     */
    public boolean verificarCredenciamento(Seguradora seguradora) {
        return this.seguradoras != null && this.seguradoras.contains(seguradora);
    }
}
