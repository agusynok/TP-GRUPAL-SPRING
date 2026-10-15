package com.example.demo.service;

import com.example.demo.dto.FacturaReporteDTO;
import com.example.demo.repository.FacturaVentaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

//  @Transactional(readOnly = true) le indica a Spring y a Hibernate que las operaciones dentro de ese método (o clase) son
//  exclusivamente de lectura (consultas SELECT), garantizando que no se van a insertar, modificar ni borrar datos.

@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaVentaRepository facturaVentaRepository;

    @PersistenceContext
    private final EntityManager em;

    public FacturaService(FacturaVentaRepository facturaVentaRepository, EntityManager em) {
        this.facturaVentaRepository = facturaVentaRepository;
        this.em = em;
    }

    // Obtiene el reporte de facturas llamando a la consulta JPQL definida en el repository
    public List<FacturaReporteDTO> obtenerReporteFacturas() {
        return facturaVentaRepository.obtenerReporteFacturas();
    }

    // Búsqueda dinámica con filtros opcionales (Punto 3 de la consigna)
    public List<FacturaReporteDTO> buscarFacturasFiltradas(LocalDate fechaDesde, LocalDate fechaHasta, String estado, Double montoMinimo) {
        StringBuilder jpql = new StringBuilder(
            "SELECT new com.example.demo.dto.FacturaReporteDTO(" +
            " f.numero, f.fechaEmision, COALESCE(c.denominacion, 'Consumidor Final'), " +
            " ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d)" +
            ") " +
            "FROM FacturaVenta f " +
            "LEFT JOIN f.cliente c " +
            "JOIN f.condicionIva ci " +
            "JOIN f.puntoVenta pv " +
            "JOIN f.detalles d " +
            "WHERE 1=1 "
        );

        Map<String, Object> params = new HashMap<>();

        if (fechaDesde != null) {
            jpql.append("AND f.fechaEmision >= :fechaDesde ");
            params.put("fechaDesde", fechaDesde);
        }

        if (fechaHasta != null) {
            jpql.append("AND f.fechaEmision <= :fechaHasta ");
            params.put("fechaHasta", fechaHasta);
        }

        if (estado != null && !estado.trim().isEmpty()) {
            jpql.append("AND f.estado = :estado ");
            params.put("estado", estado);
        }

        if (montoMinimo != null) {
            jpql.append("AND f.importeTotal >= :montoMinimo ");
            params.put("montoMinimo", montoMinimo);
        }

        jpql.append(
            "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, " +
            "ci.denominacion, pv.descripcion, f.importeTotal"
        );

        TypedQuery<FacturaReporteDTO> query = em.createQuery(
            jpql.toString(),
            FacturaReporteDTO.class
        );

        params.forEach(query::setParameter);

        return query.getResultList();
    }

    // Sobrecarga compatible con java.util.Date según ejemplo de la consigna
    public List<FacturaReporteDTO> buscarFacturasFiltradas(Date fechaDesde, Date fechaHasta, String estado, Double montoMinimo) {
        LocalDate desde = (fechaDesde != null) ? new java.sql.Date(fechaDesde.getTime()).toLocalDate() : null;
        LocalDate hasta = (fechaHasta != null) ? new java.sql.Date(fechaHasta.getTime()).toLocalDate() : null;
        return buscarFacturasFiltradas(desde, hasta, estado, montoMinimo);
    }

    public FacturaReporteDTO obtenerFacturaPorNumero(Long numero) {
        if (numero == null) {
            return null;
        }
        String jpql = "SELECT new com.example.demo.dto.FacturaReporteDTO(" +
                " f.numero, f.fechaEmision, COALESCE(c.denominacion, 'Consumidor Final'), " +
                " ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d)" +
                ") " +
                "FROM FacturaVenta f " +
                "LEFT JOIN f.cliente c " +
                "JOIN f.condicionIva ci " +
                "JOIN f.puntoVenta pv " +
                "JOIN f.detalles d " +
                "WHERE f.numero = :numero " +
                "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ci.denominacion, pv.descripcion, f.importeTotal";

        List<FacturaReporteDTO> list = em.createQuery(jpql, FacturaReporteDTO.class)
                .setParameter("numero", numero)
                .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }
}
