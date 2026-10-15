package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "PUNTOS_VENTAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PuntoVenta extends AuditoriaApp {

    @Column(nullable = false)
    private int numero;

    private String descripcion;
    private String tipoEmision;
    private String domicilioComercial;

    public PuntoVenta(int numero, String descripcion, String tipoEmision, String domicilioComercial, Usuario usuarioCarga) {
        super(usuarioCarga);
        this.numero = numero;
        this.descripcion = descripcion;
        this.tipoEmision = tipoEmision;
        this.domicilioComercial = domicilioComercial;
    }
}
