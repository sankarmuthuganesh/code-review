package RealTime.ChartCreation;

import java.io.File;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

public class ChartPDF {

    public static void writeChartToPDF(JFreeChart chart, int width, int height, String fileName) {
        // PdfWriter writer = null;
        // Document document = new Document();
        try {
            // Image Creation
            // BufferedImage obj = chart.createBufferedImage(600, 800);
            // ByteArrayOutputStream bas = new ByteArrayOutputStream();
            // ImageIO.write(obj, "png", bas);
            // byte[] byteArray = bas.toByteArray();
            //
            // InputStream in = new ByteArrayInputStream(byteArray);
            // BufferedImage image = ImageIO.read(in);
            // File outputFile = new File("");
            // ImageIO.write(image, "png", outputFile);

            ChartUtils.saveChartAsPNG(new File(fileName), chart, width, height);

            // writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            // document.open();
            // PdfContentByte contentByte = writer.getDirectContent();
            // PdfTemplate template = contentByte.createTemplate(width, height);
            // Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
            // Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
            // chart.draw(graphics2d, rectangle2d);
            // graphics2d.dispose();
            // contentByte.addTemplate(template, 0, 0);

        } catch (Exception e) {
        }
        // document.close();
    }
}
