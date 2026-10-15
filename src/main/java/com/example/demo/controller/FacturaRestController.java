package com.example.demo.controller;

import com.example.demo.dto.FacturaReporteDTO;
import com.example.demo.service.FacturaService;
import com.example.demo.service.ReporteService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaRestController {

    private final FacturaService facturaService;
    private final ReporteService reporteService;

    public FacturaRestController(FacturaService facturaService, ReporteService reporteService) {
        this.facturaService = facturaService;
        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<List<FacturaReporteDTO>> obtenerFacturas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo
    ) throws Exception {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturasFiltradas(fechaDesde, fechaHasta, estado, montoMinimo);

        // 1. En la misma llamada se genera físicamente el PDF del reporte con las facturas obtenidas
        reporteService.guardarArchivosEnDisco(facturas, "reportes");

        // 2. Se construye la URL para acceder/descargar el PDF generado
        String baseUrl;
        try {
            baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        } catch (Exception e) {
            baseUrl = "http://localhost:8090";
        }

        String pdfUrl = baseUrl + "/api/facturas/pdf";
        StringBuilder queryParams = new StringBuilder();
        if (fechaDesde != null) queryParams.append("fechaDesde=").append(fechaDesde).append("&");
        if (fechaHasta != null) queryParams.append("fechaHasta=").append(fechaHasta).append("&");
        if (estado != null && !estado.trim().isEmpty()) queryParams.append("estado=").append(estado).append("&");
        if (montoMinimo != null) queryParams.append("montoMinimo=").append(montoMinimo).append("&");

        if (queryParams.length() > 0) {
            queryParams.deleteCharAt(queryParams.length() - 1);
            pdfUrl += "?" + queryParams;
        }

        // 3. Se asigna la URL del PDF a cada elemento DTO
        for (FacturaReporteDTO dto : facturas) {
            dto.setUrlPdf(pdfUrl);
        }

        return ResponseEntity.ok()
                .header("X-PDF-Url", pdfUrl)
                .body(facturas);
    }

    // 2. Sirve el reporte pdf, se genero en el DataLoader
    @GetMapping("/pdf")
    public ResponseEntity<Resource> descargarPdf() {
        File file = new File("reportes/reporte_facturas.pdf");
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"reporte_facturas.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(file));
    }


}
