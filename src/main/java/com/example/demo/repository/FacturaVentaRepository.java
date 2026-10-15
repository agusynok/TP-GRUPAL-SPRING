package com.example.demo.repository;

import com.example.demo.dto.FacturaReporteDTO;
import com.example.demo.model.FacturaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaVentaRepository extends JpaRepository<FacturaVenta, Long> {

    @Query("SELECT new com.example.demo.dto.FacturaReporteDTO(" +
            "   f.numero, " +
            "   f.fechaEmision, " +
            "   COALESCE(c.denominacion, 'Consumidor Final'), " +
            "   ci.denominacion, " +
            "   pv.descripcion, " +
            "   f.importeTotal, " +
            "   COUNT(d) " +
            ") " +
            "FROM FacturaVenta f " +
            "LEFT JOIN f.cliente c " +
            "JOIN f.condicionIva ci " +
            "JOIN f.puntoVenta pv " +
            "JOIN f.detalles d " +
            "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ci.denominacion, pv.descripcion, f.importeTotal")
    List<FacturaReporteDTO> obtenerReporteFacturas();
}
