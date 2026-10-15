package com.example.demo;

import com.example.demo.dto.FacturaReporteDTO;
import com.example.demo.service.FacturaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FacturaServiceTest {

    @Autowired
    private FacturaService facturaService;

    @Test
    @DisplayName("Debe obtener reporte con proyección JPQL al constructor del DTO")
    void testObtenerReporteFacturas() {
        List<FacturaReporteDTO> facturas = facturaService.obtenerReporteFacturas();
        assertNotNull(facturas);
        assertFalse(facturas.isEmpty());

        // Verificar que Factura 1001 tiene "Consumidor Final" por COALESCE
        FacturaReporteDTO f1001 = facturas.stream()
                .filter(f -> f.getNumeroFactura() == 1001L)
                .findFirst()
                .orElse(null);

        assertNotNull(f1001);
        assertEquals("Consumidor Final", f1001.getClienteDenominacion());
        assertEquals(2, f1001.getCantidadItems());
        assertEquals(1500.0, f1001.getImporteTotal());

        // Verificar que Factura 1002 tiene su denominación real
        FacturaReporteDTO f1002 = facturas.stream()
                .filter(f -> f.getNumeroFactura() == 1002L)
                .findFirst()
                .orElse(null);

        assertNotNull(f1002);
        assertEquals("Empresa Tech S.A.", f1002.getClienteDenominacion());
        assertEquals(5, f1002.getCantidadItems());
        assertEquals(85000.5, f1002.getImporteTotal());
    }
}
