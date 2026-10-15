package com.example.demo;

import com.example.demo.dto.FacturaReporteDTO;
import com.example.demo.service.ReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReporteServiceTest {

    private final ReporteService reporteService = new ReporteService();

    private List<FacturaReporteDTO> mockFacturas() {
        return List.of(
                new FacturaReporteDTO(1001L, LocalDate.of(2026, 3, 15), "Consumidor Final", "IVA Responsable Inscripto", "Sucursal Central", 1500.0, 2L),
                new FacturaReporteDTO(1002L, LocalDate.of(2026, 3, 20), "Empresa Tech S.A.", "IVA Responsable Inscripto", "Sucursal Central", 85000.5, 5L)
        );
    }

    @Test
    @DisplayName("Debe generar archivo PDF válido con cabecera %PDF")
    void testGenerarReportePdf() throws Exception {
        byte[] pdfBytes = reporteService.generarReportePdf(mockFacturas());
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        String header = new String(pdfBytes, 0, 5, StandardCharsets.US_ASCII);
        assertEquals("%PDF-", header);
    }

    @Test
    @DisplayName("Debe generar archivo Excel válido")
    void testGenerarReporteExcel() throws Exception {
        byte[] excelBytes = reporteService.generarReporteExcel(mockFacturas());
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        // Los archivos .xlsx son ZIPs y comienzan con los bytes 'PK' (0x50, 0x4B)
        assertEquals('P', (char) excelBytes[0]);
        assertEquals('K', (char) excelBytes[1]);
    }

    @Test
    @DisplayName("Debe generar archivo TSV delimitado por tabulaciones")
    void testGenerarReporteTsv() {
        byte[] tsvBytes = reporteService.generarReporteTsv(mockFacturas());
        assertNotNull(tsvBytes);
        String contenido = new String(tsvBytes, StandardCharsets.UTF_8);

        assertTrue(contenido.contains("numeroFactura\tfechaEmision\tclienteDenominacion"));
        assertTrue(contenido.contains("1001\t2026-03-15\tConsumidor Final"));
        assertTrue(contenido.contains("1002\t2026-03-20\tEmpresa Tech S.A."));
    }

    @Test
    @DisplayName("Debe guardar archivos físicos en disco")
    void testGuardarArchivosEnDisco() throws Exception {
        String testDir = "build/tmp/test_reportes";
        reporteService.guardarArchivosEnDisco(mockFacturas(), testDir);

        File pdf = new File(testDir, "reporte_facturas.pdf");
        File excel = new File(testDir, "reporte_facturas.xlsx");
        File txt = new File(testDir, "reporte_facturas.txt");

        assertTrue(pdf.exists() && pdf.length() > 0);
        assertTrue(excel.exists() && excel.length() > 0);
        assertTrue(txt.exists() && txt.length() > 0);
    }
}
