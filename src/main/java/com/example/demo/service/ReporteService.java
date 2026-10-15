package com.example.demo.service;

import com.example.demo.dto.FacturaReporteDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] generarReportePdf(List<FacturaReporteDTO> facturas) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Título del reporte
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte Ejecutivo de Ventas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        // Tabla con 7 columnas
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.0f, 2.2f, 4.0f, 3.5f, 3.5f, 2.5f, 1.8f});

        // Encabezados
        String[] headers = {
                "N° Factura", "Fecha", "Cliente", "Cond. IVA", "Punto Venta", "Total ($)", "Items"
        };
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        BaseColor headerBackground = new BaseColor(41, 128, 185);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerBackground);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);
            table.addCell(cell);
        }

        // Datos
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        double totalVentas = 0;
        long totalItems = 0;

        for (FacturaReporteDTO dto : facturas) {
            totalVentas += dto.getImporteTotal();
            totalItems += dto.getCantidadItems();

            PdfPCell c1 = new PdfPCell(new Phrase(String.valueOf(dto.getNumeroFactura() != null ? dto.getNumeroFactura() : "-"), dataFont));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setPadding(5);
            table.addCell(c1);

            String fechaStr = dto.getFechaEmision() != null ? dto.getFechaEmision().format(DATE_FORMATTER) : "-";
            PdfPCell c2 = new PdfPCell(new Phrase(fechaStr, dataFont));
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            c2.setPadding(5);
            table.addCell(c2);

            PdfPCell c3 = new PdfPCell(new Phrase(dto.getClienteDenominacion() != null ? dto.getClienteDenominacion() : "Consumidor Final", dataFont));
            c3.setPadding(5);
            table.addCell(c3);

            PdfPCell c4 = new PdfPCell(new Phrase(dto.getCondicionIva() != null ? dto.getCondicionIva() : "-", dataFont));
            c4.setPadding(5);
            table.addCell(c4);

            PdfPCell c5 = new PdfPCell(new Phrase(dto.getPuntoVentaDescripcion() != null ? dto.getPuntoVentaDescripcion() : "-", dataFont));
            c5.setPadding(5);
            table.addCell(c5);

            PdfPCell c6 = new PdfPCell(new Phrase(String.format("$ %.2f", dto.getImporteTotal()), dataFont));
            c6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c6.setPadding(5);
            table.addCell(c6);

            PdfPCell c7 = new PdfPCell(new Phrase(String.valueOf(dto.getCantidadItems()), dataFont));
            c7.setHorizontalAlignment(Element.ALIGN_CENTER);
            c7.setPadding(5);
            table.addCell(c7);
        }

        // Fila de Totales
        com.itextpdf.text.Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTALES", totalFont));
        totalLabelCell.setColspan(5);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setBackgroundColor(new BaseColor(230, 230, 230));
        totalLabelCell.setPadding(6);
        table.addCell(totalLabelCell);

        PdfPCell totalImporteCell = new PdfPCell(new Phrase(String.format("$ %.2f", totalVentas), totalFont));
        totalImporteCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalImporteCell.setBackgroundColor(new BaseColor(230, 230, 230));
        totalImporteCell.setPadding(6);
        table.addCell(totalImporteCell);

        PdfPCell totalItemsCell = new PdfPCell(new Phrase(String.valueOf(totalItems), totalFont));
        totalItemsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalItemsCell.setBackgroundColor(new BaseColor(230, 230, 230));
        totalItemsCell.setPadding(6);
        table.addCell(totalItemsCell);

        document.add(table);
        document.close();

        return out.toByteArray();
    }





    public byte[] generarReporteExcel(List<FacturaReporteDTO> facturas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte de Ventas");

            // Estilo encabezado
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo moneda
            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat(dataFormat.getFormat("$#,##0.00"));

            // Encabezados
            String[] headers = {
                    "N° Factura", "Fecha de Emisión", "Cliente", "Condición IVA", "Punto de Venta", "Importe Total", "Cantidad de Items"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (FacturaReporteDTO dto : facturas) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.getNumeroFactura() != null ? dto.getNumeroFactura() : 0);
                row.createCell(1).setCellValue(dto.getFechaEmision() != null ? dto.getFechaEmision().format(DATE_FORMATTER) : "");
                row.createCell(2).setCellValue(dto.getClienteDenominacion() != null ? dto.getClienteDenominacion() : "Consumidor Final");
                row.createCell(3).setCellValue(dto.getCondicionIva() != null ? dto.getCondicionIva() : "");
                row.createCell(4).setCellValue(dto.getPuntoVentaDescripcion() != null ? dto.getPuntoVentaDescripcion() : "");

                Cell cellTotal = row.createCell(5);
                cellTotal.setCellValue(dto.getImporteTotal());
                cellTotal.setCellStyle(currencyCellStyle);

                row.createCell(6).setCellValue(dto.getCantidadItems());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }





    public byte[] generarReporteTsv(List<FacturaReporteDTO> facturas) {
        StringBuilder sb = new StringBuilder();
        sb.append("numeroFactura\tfechaEmision\tclienteDenominacion\tcondicionIva\tpuntoVentaDescripcion\timporteTotal\tcantidadItems\n");

        for (FacturaReporteDTO dto : facturas) {
            sb.append(dto.getNumeroFactura() != null ? dto.getNumeroFactura() : "").append("\t");
            sb.append(dto.getFechaEmision() != null ? dto.getFechaEmision().format(DATE_FORMATTER) : "").append("\t");
            sb.append(dto.getClienteDenominacion() != null ? dto.getClienteDenominacion() : "Consumidor Final").append("\t");
            sb.append(dto.getCondicionIva() != null ? dto.getCondicionIva() : "").append("\t");
            sb.append(dto.getPuntoVentaDescripcion() != null ? dto.getPuntoVentaDescripcion() : "").append("\t");
            sb.append(String.format(java.util.Locale.US, "%.2f", dto.getImporteTotal())).append("\t");
            sb.append(dto.getCantidadItems()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }




    //Guarda físicamente los 2 archivos generados (PDF y Excel/TSV) en un directorio local.
    public void guardarArchivosEnDisco(List<FacturaReporteDTO> facturas, String directorioSalida) throws Exception {
        File dir = new File(directorioSalida);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Guardar PDF
        byte[] pdfBytes = generarReportePdf(facturas);
        File pdfFile = new File(dir, "reporte_facturas.pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(pdfBytes);
        }

        // Guardar Excel (.xlsx)
        byte[] excelBytes = generarReporteExcel(facturas);
        File excelFile = new File(dir, "reporte_facturas.xlsx");
        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            fos.write(excelBytes);
        }

        // Guardar TSV (.txt separado por tabulaciones según alternativa de la consigna)
        byte[] tsvBytes = generarReporteTsv(facturas);
        File tsvFile = new File(dir, "reporte_facturas.txt");
        try (FileOutputStream fos = new FileOutputStream(tsvFile)) {
            fos.write(tsvBytes);
        }
    }
}
