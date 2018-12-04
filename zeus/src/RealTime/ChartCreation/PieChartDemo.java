package RealTime.ChartCreation;

import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class PieChartDemo {

    public void generatePieChart(String nameOfPDF, Map<String, Integer> pieChartData, String path) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        pieChartData.entrySet().stream().forEach(category -> {
            dataset.setValue(category.getKey(), category.getValue());
        });

        JFreeChart chart = ChartFactory.createPieChart("Bug Type Classification", dataset, true, true, false);
        ChartPDF makePdf = new ChartPDF();
        makePdf.writeChartToPDF(chart, 600, 800, path + "\\" + nameOfPDF + ".png");

    }

    public static JFreeChart generateBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(791, "Population", "1750 AD");
        dataset.setValue(978, "Population", "1800 AD");
        dataset.setValue(1262, "Population", "1850 AD");
        dataset.setValue(1650, "Population", "1900 AD");
        dataset.setValue(2519, "Population", "2000 AD");

        JFreeChart chart = ChartFactory.createBarChart("World Population Growth", "Year", "Population in Millions",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        return chart;

    }
}
