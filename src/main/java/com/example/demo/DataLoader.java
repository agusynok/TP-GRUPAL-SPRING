package com.example.demo;

import com.example.demo.dto.FacturaReporteDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.FacturaService;
import com.example.demo.service.ReporteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final CondicionIvaRepository condicionIvaRepository;
    private final TipoMonedaRepository tipoMonedaRepository;
    private final ClienteRepository clienteRepository;
    private final ArticuloRepository articuloRepository;
    private final FacturaVentaRepository facturaVentaRepository;
    private final FacturaService facturaService;
    private final ReporteService reporteService;

    public DataLoader(UsuarioRepository usuarioRepository, PuntoVentaRepository puntoVentaRepository, CondicionIvaRepository condicionIvaRepository, TipoMonedaRepository tipoMonedaRepository, ClienteRepository clienteRepository, ArticuloRepository articuloRepository, FacturaVentaRepository facturaVentaRepository, FacturaService facturaService, ReporteService reporteService) {
        this.usuarioRepository = usuarioRepository;
        this.puntoVentaRepository = puntoVentaRepository;
        this.condicionIvaRepository = condicionIvaRepository;
        this.tipoMonedaRepository = tipoMonedaRepository;
        this.clienteRepository = clienteRepository;
        this.articuloRepository = articuloRepository;
        this.facturaVentaRepository = facturaVentaRepository;
        this.facturaService = facturaService;
        this.reporteService = reporteService;
    }

    @Override
    public void run(String... args) throws Exception {

        //count() retorna la cantidad de instancias de FacturaVenta
        if (facturaVentaRepository.count() == 0) {
            cargarDatosIniciales();
        }

        // Generar físicamente los 2 archivos de reporte (PDF y Excel/TXT tabulado) usando la consulta JPQL
        List<FacturaReporteDTO> facturas = facturaService.obtenerReporteFacturas();
        reporteService.guardarArchivosEnDisco(facturas, "reportes");
    }

    private void cargarDatosIniciales() {
        // 1. Usuario admin
        Usuario usuario = new Usuario("admin", "admin123", "Agustin", "Perez");
        usuarioRepository.save(usuario);

        // 2. Punto de venta
        PuntoVenta pvCentral = new PuntoVenta(1, "Sucursal Central", "ELECTRONICA", "Av. San Martín 100", usuario);
        PuntoVenta pvNorte = new PuntoVenta(2, "Sucursal Norte", "ELECTRONICA", "Av. Belgrano 500", usuario);
        puntoVentaRepository.saveAll(List.of(pvCentral, pvNorte));

        // 3. Condición IVA
        CondicionIva ivaRI = new CondicionIva(1, "IVA Responsable Inscripto", usuario);
        CondicionIva ivaMono = new CondicionIva(6, "Monotributo", usuario);
        condicionIvaRepository.saveAll(List.of(ivaRI, ivaMono));

        // 4. Tipo de moneda
        TipoMoneda pesos = new TipoMoneda("PES", "Pesos Argentinos", "$", usuario);
        tipoMonedaRepository.save(pesos);

        // 5. Clientes
        Contacto contacto1 = new Contacto("info@techsa.com", "4220000", "2615555555");
        Domicilio dom1 = new Domicilio("Av. España", "1234");
        Cliente clienteTech = new Cliente("30-71234567-9", "Empresa Tech S.A.", contacto1, dom1, usuario);

        Contacto contacto2 = new Contacto("ventas@distrisur.com", "4221111", "2616666666");
        Domicilio dom2 = new Domicilio("San Juan", "456");
        Cliente clienteDistri = new Cliente("30-89123456-0", "Distribuidora del Sur", contacto2, dom2, usuario);

        clienteRepository.saveAll(List.of(clienteTech, clienteDistri));

        // 6. Artículos
        Articulo artTeclado = new Articulo(null, null, "ART01", "Teclado Mecánico RGB", usuario);
        Articulo artMouse = new Articulo(null, null, "ART02", "Mouse Óptico Inalámbrico", usuario);
        Articulo artMonitor = new Articulo(null, null, "ART03", "Monitor Gamer 27\"", usuario);
        Articulo artLaptop = new Articulo(null, null, "ART04", "Notebook Core i7 16GB", usuario);
        articuloRepository.saveAll(List.of(artTeclado, artMouse, artMonitor, artLaptop));

        // 7. Factura 1001 (Consumidor Final, fecha 2026-03-15, total 1500.0, 2 items, EMITIDA)
        FacturaVenta f1001 = new FacturaVenta();
        f1001.setUsuarioCarga(usuario);
        f1001.setUsuarioModificacion(usuario);
        f1001.setFechaAlta(LocalDate.now());
        f1001.setFechaModificacion(LocalDate.now());
        f1001.setNumero(1001L);
        f1001.setFechaEmision(LocalDate.of(2026, 3, 15));
        f1001.setCliente(null); // Consumidor Final
        f1001.setCondicionIva(ivaRI);
        f1001.setPuntoVenta(pvCentral);
        f1001.setTipoMoneda(pesos);
        f1001.setImporteTotal(1500.0);
        f1001.setImporteSaldo(0.0);
        f1001.setImporteCobrado(1500.0);
        f1001.setEstado("EMITIDA");

        FacturaVentaDetalle d1 = new FacturaVentaDetalle(artMouse, "Mouse Óptico", 1, 750.0, 0, 750.0, 0);
        FacturaVentaDetalle d2 = new FacturaVentaDetalle(artTeclado, "Teclado Básico", 1, 750.0, 0, 750.0, 0);
        f1001.addDetalle(d1);
        f1001.addDetalle(d2);

        // 8. Factura 1002 (Empresa Tech S.A., fecha 2026-03-20, total 85000.5, 5 items, EMITIDA)
        FacturaVenta f1002 = new FacturaVenta();
        f1002.setUsuarioCarga(usuario);
        f1002.setUsuarioModificacion(usuario);
        f1002.setFechaAlta(LocalDate.now());
        f1002.setFechaModificacion(LocalDate.now());
        f1002.setNumero(1002L);
        f1002.setFechaEmision(LocalDate.of(2026, 3, 20));
        f1002.setCliente(clienteTech);
        f1002.setCondicionIva(ivaRI);
        f1002.setPuntoVenta(pvCentral);
        f1002.setTipoMoneda(pesos);
        f1002.setImporteTotal(85000.5);
        f1002.setImporteSaldo(0.0);
        f1002.setImporteCobrado(85000.5);
        f1002.setEstado("EMITIDA");

        for (int i = 1; i <= 5; i++) {
            f1002.addDetalle(new FacturaVentaDetalle(artMonitor, "Item " + i + " Monitor", 1, 17000.1, 0, 17000.1, 0));
        }

        // 9. Factura 1003 (Distribuidora del Sur, fecha 2026-04-05, total 12000.0, 1 item, ANULADA)
        FacturaVenta f1003 = new FacturaVenta();
        f1003.setUsuarioCarga(usuario);
        f1003.setUsuarioModificacion(usuario);
        f1003.setFechaAlta(LocalDate.now());
        f1003.setFechaModificacion(LocalDate.now());
        f1003.setNumero(1003L);
        f1003.setFechaEmision(LocalDate.of(2026, 4, 5));
        f1003.setCliente(clienteDistri);
        f1003.setCondicionIva(ivaMono);
        f1003.setPuntoVenta(pvNorte);
        f1003.setTipoMoneda(pesos);
        f1003.setImporteTotal(12000.0);
        f1003.setImporteSaldo(12000.0);
        f1003.setImporteCobrado(0.0);
        f1003.setEstado("ANULADA");
        f1003.setFechaAnulacion(LocalDate.of(2026, 4, 6));

        f1003.addDetalle(new FacturaVentaDetalle(artLaptop, "Laptop ref.", 1, 12000.0, 0, 12000.0, 0));

        // 10. Factura 1004 (Consumidor Final, fecha 2026-04-10, total 35000.0, 3 items, EMITIDA)
        FacturaVenta f1004 = new FacturaVenta();
        f1004.setUsuarioCarga(usuario);
        f1004.setUsuarioModificacion(usuario);
        f1004.setFechaAlta(LocalDate.now());
        f1004.setFechaModificacion(LocalDate.now());
        f1004.setNumero(1004L);
        f1004.setFechaEmision(LocalDate.of(2026, 4, 10));
        f1004.setCliente(null);
        f1004.setCondicionIva(ivaRI);
        f1004.setPuntoVenta(pvNorte);
        f1004.setTipoMoneda(pesos);
        f1004.setImporteTotal(35000.0);
        f1004.setImporteSaldo(0.0);
        f1004.setImporteCobrado(35000.0);
        f1004.setEstado("EMITIDA");

        f1004.addDetalle(new FacturaVentaDetalle(artTeclado, "Teclados", 2, 10000.0, 0, 20000.0, 0));
        f1004.addDetalle(new FacturaVentaDetalle(artMouse, "Mouse", 1, 5000.0, 0, 5000.0, 0));
        f1004.addDetalle(new FacturaVentaDetalle(artMonitor, "Monitor", 1, 10000.0, 0, 10000.0, 0));

        facturaVentaRepository.saveAll(List.of(f1001, f1002, f1003, f1004));
    }
}
