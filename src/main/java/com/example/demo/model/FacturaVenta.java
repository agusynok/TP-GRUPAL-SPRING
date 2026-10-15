package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FACTURAS_VENTAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"detalles"})
@ToString(exclude = {"detalles"})
public class FacturaVenta extends AuditoriaApp {

    private Long numero;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "punto_venta_id", nullable = false)
    private PuntoVenta puntoVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicion_iva_id", nullable = false)
    private CondicionIva condicionIva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_moneda_id", nullable = false)
    private TipoMoneda tipoMoneda;

    private double importeCobrado;
    private double importeSaldo;

    @Column(nullable = false)
    private double importeTotal;

    private String cae;
    private LocalDate caeFechaVencimiento;
    private String resultadoAfip;
    private String motivoRechazo;

    @Column(nullable = false)
    private String estado;

    private LocalDate fechaAnulacion;
    private String observaciones;

    @Builder.Default
    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FacturaVentaDetalle> detalles = new ArrayList<>();

    public FacturaVenta(List<FacturaVentaDetalle> detalles, Cliente cliente, CondicionIva condicionIva,
                        TipoMoneda tipoMoneda, PuntoVenta puntoVenta, Long numero, double importeTotal,
                        double importeSaldo, String cae, LocalDate caeFechaVencimiento, String resultadoAfip,
                        String estado, String observaciones, Usuario usuarioCarga) {
        super(usuarioCarga);
        if (detalles != null) {
            for (FacturaVentaDetalle fvd : detalles) {
                addDetalle(fvd);
            }
        }
        this.cliente = cliente;
        this.condicionIva = condicionIva;
        this.tipoMoneda = tipoMoneda;
        this.puntoVenta = puntoVenta;
        this.numero = numero;
        this.fechaEmision = LocalDate.now();
        this.importeSaldo = importeSaldo;
        this.importeTotal = importeTotal;
        this.cae = cae;
        this.caeFechaVencimiento = caeFechaVencimiento;
        this.resultadoAfip = resultadoAfip;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public void addDetalle(FacturaVentaDetalle detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
        detalle.setFacturaVenta(this);
    }
}
