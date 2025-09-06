package com.hermes.hermes.domain.model.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Entidade implements Serializable {

    public abstract Long getId();

    public abstract void setId(Long id);

    @CreatedDate
    @Column(name = "datahora_cadastro")
    private LocalDateTime dataHoraCadastro;

    @LastModifiedDate
    @Column(name = "datahora_alteracao")
    private LocalDateTime dataHoraAlteracao;

    @NotNull(message = "{campo.not.null}")
    @Column(columnDefinition = "boolean DEFAULT true")
    protected boolean ativo = true;

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(getId()).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Entidade rhs = (Entidade) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(getId(), rhs.getId())
                .isEquals();
    }
}
