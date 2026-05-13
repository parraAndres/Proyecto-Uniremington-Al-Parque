package com.vetsync.app.uniremington.service;

// Importaciones de PDF (OpenPDF) - Explícitas para evitar conflictos
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// Importaciones de Excel (Apache POI) - Explícitas
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vetsync.app.uniremington.entity.Beneficiario;
import com.vetsync.app.uniremington.entity.ServicioSocial;
import com.vetsync.app.uniremington.repository.ServicioSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ServicioSocialRepository repository;

    public List<ServicioSocial> getFilteredData(LocalDateTime inicio, LocalDateTime fin, String municipio, String vereda, String barrio) {
        return repository.findAll().stream()
                .filter(s -> (inicio == null || s.getFechaServicio().isAfter(inicio)))
                .filter(s -> (fin == null || s.getFechaServicio().isBefore(fin)))
                .filter(s -> {
                    if (s.getBeneficiario() == null) return (municipio == null || municipio.isEmpty());
                    boolean matchMun = (municipio == null || municipio.isEmpty() || s.getBeneficiario().getMunicipio().equalsIgnoreCase(municipio));
                    boolean matchVer = (vereda == null || vereda.isEmpty() || (s.getBeneficiario().getVereda() != null && s.getBeneficiario().getVereda().equalsIgnoreCase(vereda)));
                    boolean matchBar = (barrio == null || barrio.isEmpty() || (s.getBeneficiario().getBarrio() != null && s.getBeneficiario().getBarrio().equalsIgnoreCase(barrio)));
                    return matchMun && matchVer && matchBar;
                })
                .collect(Collectors.toList());
    }

    public ByteArrayInputStream generateExcel(List<ServicioSocial> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte Atenciones");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Fecha", "Beneficiario", "Documento", "Facultad", "Servicio", "Municipio", "Vereda", "Barrio"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (ServicioSocial s : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getFechaServicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                
                Beneficiario b = s.getBeneficiario();
                row.createCell(2).setCellValue(b != null ? b.getNombre() : "N/A");
                row.createCell(3).setCellValue(b != null ? b.getDocumento() : "N/A");
                row.createCell(4).setCellValue(s.getFacultad());
                row.createCell(5).setCellValue(s.getTipoServicio());
                row.createCell(6).setCellValue(b != null ? b.getMunicipio() : "N/A");
                row.createCell(7).setCellValue(b != null ? b.getVereda() : "N/A");
                row.createCell(8).setCellValue(b != null ? b.getBarrio() : "N/A");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generatePdf(List<ServicioSocial> data) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph para = new Paragraph("Reporte Uniremington al Parque", fontHeader);
        para.setAlignment(Element.ALIGN_CENTER);
        document.add(para);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        String[] headers = {"Fecha", "Beneficiario", "Documento", "Facultad", "Servicio", "Municipio", "Vereda", "Barrio"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(230, 230, 230));
            table.addCell(cell);
        }

        for (ServicioSocial s : data) {
            Beneficiario b = s.getBeneficiario();
            table.addCell(new Phrase(s.getFechaServicio().format(DateTimeFormatter.ofPattern("dd/MM/yy")), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(b != null ? b.getNombre() : "N/A", FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(b != null ? b.getDocumento() : "N/A", FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getFacultad(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(s.getTipoServicio(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(b != null ? b.getMunicipio() : "N/A", FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(b != null ? b.getVereda() : "N/A", FontFactory.getFont(FontFactory.HELVETICA, 9)));
            table.addCell(new Phrase(b != null ? b.getBarrio() : "N/A", FontFactory.getFont(FontFactory.HELVETICA, 9)));
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
