package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AuditoriaApp extends EntityId {

    @Column(nullable = false)
    protected LocalDate fechaAlta;

    protected LocalDate fechaBaja;

    @Column(nullable = false)
    protected LocalDate fechaModificacion;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioCarga;

    @ManyToOne
    protected Usuario usuarioBaja;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioModificacion;

    public AuditoriaApp(Usuario usuarioCarga) {
        this.fechaAlta = LocalDate.now();
        this.fechaModificacion = LocalDate.now();
        this.usuarioCarga = usuarioCarga;
        this.usuarioModificacion = usuarioCarga;
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaAlta == null) {
            this.fechaAlta = LocalDate.now();
        }
        if (this.fechaModificacion == null) {
            this.fechaModificacion = LocalDate.now();
        }
        if (this.usuarioModificacion == null && this.usuarioCarga != null) {
            this.usuarioModificacion = this.usuarioCarga;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDate.now();
    }
}
