package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FACTURAS_VENTAS_DETALLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"facturaVenta"})
@ToString(exclude = {"facturaVenta"})
public class FacturaVentaDetalle extends EntityId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facturaVenta_id")
    private FacturaVenta facturaVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Articulo articulo;

    private String descripcion;

    @Column(nullable = false)
    private double cantidad;

    @Column(nullable = false)
    private double precioUnitario;

    private double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;

    @Column(nullable = false)
    private double importeSubtotal;

    public FacturaVentaDetalle(Articulo articulo, String descripcion, double cantidad, double precioUnitario,
                               double porcentajeBonificacion, double importeNeto, double importeIva) {
        this.articulo = articulo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.porcentajeBonificacion = porcentajeBonificacion;
        this.importeNeto = importeNeto;
        this.importeIva = importeIva;
        this.importeSubtotal = cantidad * precioUnitario;
    }
}
