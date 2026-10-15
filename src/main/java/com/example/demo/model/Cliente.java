package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLIENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Cliente extends AuditoriaApp {

    @Column(nullable = false)
    private String cuitCuil;

    @Column(nullable = false)
    private String denominacion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Contacto contacto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Domicilio domicilio;

    public Cliente(String cuitCuil, String denominacion, Contacto contacto, Domicilio domicilio, Usuario usuarioCarga) {
        super(usuarioCarga);
        this.cuitCuil = cuitCuil;
        this.denominacion = denominacion;
        this.contacto = contacto;
        this.domicilio = domicilio;
    }
}
