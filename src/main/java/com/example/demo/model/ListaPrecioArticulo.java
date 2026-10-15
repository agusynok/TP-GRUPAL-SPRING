package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LISTAS_PRECIOS_ARTICULOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ListaPrecioArticulo extends AuditoriaApp {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ListaPrecio listaPrecio;

    @Column(nullable = false)
    private double precioVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Articulo articulo;

    public ListaPrecioArticulo(ListaPrecio listaPrecio, double precioVenta, Articulo articulo, Usuario usuarioCarga) {
        super(usuarioCarga);
        this.listaPrecio = listaPrecio;
        this.precioVenta = precioVenta;
        this.articulo = articulo;
    }
}
