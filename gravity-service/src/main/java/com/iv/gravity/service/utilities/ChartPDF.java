package com.iv.gravity.service.utilities;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import org.jfree.chart.JFreeChart;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class ChartPDF {

   public static byte[] writeChartToPDF(JFreeChart chart, int width, int height) {
      ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
      PdfWriter writer = null;
      Document document = new Document();
      try {
         writer = PdfWriter.getInstance(document, pdfOut);
         document.open();
         document.addTitle("\u00A9 Gravity");
         PdfContentByte contentByte = writer.getDirectContent();
         PdfTemplate template = contentByte.createTemplate(width, height);
         Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
         Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
         chart.draw(graphics2d, rectangle2d);
         graphics2d.dispose();
         contentByte.addTemplate(template, 0, 0);

      }
      catch (Exception e) {
      }
      document.close();
      return pdfOut.toByteArray();
   }

}
