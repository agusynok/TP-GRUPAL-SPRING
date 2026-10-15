package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "CONDICIONES_IVA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CondicionIva extends AuditoriaApp {

    @Column(nullable = false)
    private int codigoAfip;

    @Column(nullable = false)
    private String denominacion;

    public CondicionIva(int codigoAfip, String denominacion, Usuario usuarioCarga) {
        super(usuarioCarga);
        this.codigoAfip = codigoAfip;
        this.denominacion = denominacion;
    }
}
