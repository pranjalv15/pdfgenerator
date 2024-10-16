package com.example.pdfgenerator.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.html.simpleparser.HTMLWorker;

import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @SuppressWarnings("deprecation")
    public String generatePdf(Map<String, Object> data, String templateName, String pdfFileName) throws Exception {
        // Create 'invoices' directory if it doesn't exist
        Files.createDirectories(Paths.get("invoices"));

        // Check if file already exists
        if (Files.exists(Paths.get(pdfFileName))) {
            return pdfFileName;
        }

        // Create Thymeleaf context
        Context context = new Context();
        context.setVariables(data);

        // Render HTML from Thymeleaf template
        String htmlContent = templateEngine.process(templateName, context);

        // Generate PDF using iText
        Document document = new Document();
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFileName)) {
            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(htmlContent));
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return pdfFileName;
    }
}
