package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FacturaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/facturas sin filtros debe generar PDF y retornar facturas con urlPdf")
    void testObtenerFacturasSinFiltros() throws Exception {
        mockMvc.perform(get("/api/facturas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string("X-PDF-Url", containsString("/api/facturas/pdf")))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].numeroFactura").exists())
                .andExpect(jsonPath("$[0].clienteDenominacion").exists())
                .andExpect(jsonPath("$[0].urlPdf", containsString("/api/facturas/pdf")));
    }

    @Test
    @DisplayName("GET /api/facturas con filtro estado=EMITIDA debe generar PDF filtrado y retornar url con query param")
    void testFiltrarPorEstado() throws Exception {
        mockMvc.perform(get("/api/facturas")
                        .param("estado", "EMITIDA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-PDF-Url", containsString("/api/facturas/pdf?estado=EMITIDA")))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].urlPdf", containsString("/api/facturas/pdf?estado=EMITIDA")))
                .andExpect(jsonPath("$[*].numeroFactura", not(hasItem(1003))));
    }

    @Test
    @DisplayName("GET /api/facturas con filtro montoMinimo=50000")
    void testFiltrarPorMontoMinimo() throws Exception {
        mockMvc.perform(get("/api/facturas")
                        .param("montoMinimo", "50000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroFactura", is(1002)));
    }

    @Test
    @DisplayName("GET /api/facturas con filtro de fechas")
    void testFiltrarPorFechas() throws Exception {
        mockMvc.perform(get("/api/facturas")
                        .param("fechaDesde", "2026-04-01")
                        .param("fechaHasta", "2026-04-30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].numeroFactura", containsInAnyOrder(1003, 1004)));
    }

    @Test
    @DisplayName("GET /api/facturas/1001/pdf debe retornar el archivo PDF de la factura")
    void testDescargarPdfFacturaIndividual() throws Exception {
        mockMvc.perform(get("/api/facturas/1001/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("factura_1001.pdf")));
    }

    @Test
    @DisplayName("GET /api/facturas/9999/pdf para factura inexistente debe retornar 404")
    void testDescargarPdfFacturaInexistente() throws Exception {
        mockMvc.perform(get("/api/facturas/9999/pdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/facturas/pdf debe retornar el archivo PDF del reporte completo")
    void testDescargarReportePdf() throws Exception {
        mockMvc.perform(get("/api/facturas/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("reporte_facturas.pdf")));
    }
}
