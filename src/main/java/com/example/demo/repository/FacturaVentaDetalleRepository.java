package com.example.demo.repository;

import com.example.demo.model.FacturaVentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaVentaDetalleRepository extends JpaRepository<FacturaVentaDetalle, Long> {
}
