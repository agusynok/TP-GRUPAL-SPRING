package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "ARTICULOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Articulo extends AuditoriaApp {

    @ManyToOne
    private Rubro rubro;

    @ManyToOne
    private Marca marca;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;

    public Articulo(Rubro rubro, Marca marca, String codigo, String denominacion, Usuario usuarioCarga) {
        super(usuarioCarga);
        this.rubro = rubro;
        this.marca = marca;
        this.codigo = codigo;
        this.denominacion = denominacion;
    }
}
