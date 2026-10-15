package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaReporteDTO {

    private Long numeroFactura;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;

    private String clienteDenominacion;
    private String condicionIva;
    private String puntoVentaDescripcion;
    private double importeTotal;
    private long cantidadItems;
    private String urlPdf;

    // Constructor de 7 parámetros utilizado en las proyecciones JPQL SELECT new
    public FacturaReporteDTO(Long numeroFactura, LocalDate fechaEmision, String clienteDenominacion, String condicionIva, String puntoVentaDescripcion, double importeTotal, long cantidadItems) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.clienteDenominacion = clienteDenominacion;
        this.condicionIva = condicionIva;
        this.puntoVentaDescripcion = puntoVentaDescripcion;
        this.importeTotal = importeTotal;
        this.cantidadItems = cantidadItems;
    }

    // Constructor compatible si la consulta o entidad devuelve java.util.Date
    public FacturaReporteDTO(Long numeroFactura, Date fechaEmision, String clienteDenominacion, String condicionIva, String puntoVentaDescripcion, double importeTotal, long cantidadItems) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = (fechaEmision != null) ? new java.sql.Date(fechaEmision.getTime()).toLocalDate() : null;
        this.clienteDenominacion = clienteDenominacion;
        this.condicionIva = condicionIva;
        this.puntoVentaDescripcion = puntoVentaDescripcion;
        this.importeTotal = importeTotal;
        this.cantidadItems = cantidadItems;
    }
}
