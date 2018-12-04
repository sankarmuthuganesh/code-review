package com.iv.gravity.service.utilities;

import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class PieChart {

   public byte[] generatePieChart(Map<String, Integer> pieChartData) {
      DefaultPieDataset dataset = new DefaultPieDataset();
      pieChartData.entrySet().stream().forEach(category -> {
         dataset.setValue(category.getKey(), category.getValue());
      });
      JFreeChart chart = ChartFactory.createPieChart("Bug Detailed Classification", dataset, true, true, false);
      ChartPDF makePdf = new ChartPDF();
      return makePdf.writeChartToPDF(chart, 600, 800);
   }

}
