package com.hermes.hermes.domain.model.prestador;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.localizacao.Localizacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Classe abstrata que representa um prestador de serviços no sistema.
 * Define a estrutura comum para diferentes tipos de prestadores.
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Prestador extends Entidade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prestador_seq")
    @SequenceGenerator(name = "prestador_seq", sequenceName = "prestador_seq", allocationSize = 1)
    protected Long id;
    
    @Column(length = 200, nullable = false)
    protected String nome;
    
    @Column(length = 100)
    protected String contato;
    
    @Embedded
    protected Localizacao localizacao;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prestador_especialidades", joinColumns = @JoinColumn(name = "prestador_id"))
    @Column(name = "especialidade")
    protected List<String> especialidades;
    
    @Override
    public Long getId() {
        return this.id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Avalia a qualidade do prestador.
     * @return avaliação média (0.0 a 5.0)
     */
    public abstract double avaliar();
    
    /**
     * Verifica a disponibilidade do prestador.
     * @return true se disponível, false caso contrário
     */
    public abstract boolean verificarDisponibilidade();
}