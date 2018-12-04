package RealTime.ChartCreation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.ActualErrorCollectionDetailed;
import RealTime.Entity.ActualJavaDetailed;
import RealTime.Entity.ActualJavaScriptDetailed;
import RealTime.Entity.ActualXMLDetailed;
import RealTime.GitAccess.Progress;

public class MakeInputToChart {
    public void
            giveInputToChart(Map<String, Map<String, ActualErrorCollectionDetailed>> actualOut, Progress gitCall) {

        actualOut
                .entrySet()
                .stream()
                .forEach(
                        repo -> {

                            repo.getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(branch -> {
                                        // Java
                                            Map<String, Integer> branchChartJavaErrorMap = new HashMap<>();
                                            Map<String, Integer> branchChartJavaWarningMap = new HashMap<>();
                                            // JavaScript
                                            Map<String, Integer> branchChartJavaScriptErrorMap = new HashMap<>();
                                            Map<String, Integer> branchChartJavaScriptWarningMap = new HashMap<>();
                                            // XML
                                            Map<String, Integer> branchChartXMLErrorMap = new HashMap<>();
                                            Map<String, Integer> branchChartXMLWarningMap = new HashMap<>();

                                            String repoDirBranch;
                                            repoDirBranch = gitCall.getGravityStoragaePath()
                                                    + File.separator
                                                    + "GravityOutput-"
                                                    + "sankraja" + File.separator
                                                    + "GravityOutput"
                                                    + File.separator
                                                    + "OptimusCategorizedOutput"
                                                    + File.separator
                                                    + repo.getKey()
                                                    + "\\"
                                                    + branch.getKey();

                                            Path repoAndBranchDirChart = Paths.get(gitCall.getGravityStoragaePath()
                                                    + File.separator
                                                    + "GravityOutput-"
                                                    + "sankraja" + File.separator
                                                    + "GravityOutput"
                                                    + File.separator
                                                    + "OptimusCategorizedOutput"
                                                    + File.separator
                                                    + repo.getKey()
                                                    + "\\"
                                                    + branch.getKey()
                                                    + "\\Charts\\");
                                            try {
                                                Files.createDirectories(repoAndBranchDirChart);
                                            } catch (Exception e1) {
                                            }

                                            File chartDirJava = new File(repoAndBranchDirChart.toString() + "\\Java\\");
                                            if (chartDirJava.exists()) {
                                                chartDirJava.delete();
                                            }
                                            chartDirJava.mkdir();

                                            File chartDirJavaScript = new File(repoAndBranchDirChart.toString()
                                                    + "\\JavaScript\\");
                                            if (chartDirJavaScript.exists()) {
                                                chartDirJavaScript.delete();
                                            }
                                            chartDirJavaScript.mkdir();

                                            File chartDirXML = new File(repoAndBranchDirChart.toString() + "\\XML\\");
                                            if (chartDirXML.exists()) {
                                                chartDirXML.delete();
                                            }
                                            chartDirXML.mkdir();

                                            ActualErrorCollectionDetailed branchErrorsAndWarnings = branch.getValue();

                                            ActualJavaDetailed javaBugs = branchErrorsAndWarnings
                                                    .getTotalCategorisedCollectionJava();
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaCategories = javaBugs
                                                    .getErrorCategoriesAndItsFilesAndDetails();
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaCategoriesWarnings = javaBugs
                                                    .getWarningCategoriesAndItsFilesAndDetails();
                                            try {
                                                makeCategoriesAndFilesList(javaCategories, "Errors",
                                                        chartDirJava.getAbsolutePath(), branchChartJavaErrorMap);
                                                makeCategoriesAndFilesList(javaCategoriesWarnings, "Warnings",
                                                        chartDirJava.getAbsolutePath(), branchChartJavaWarningMap);
                                                PieChartDemo makePieChart = new PieChartDemo();
                                                makePieChart.generatePieChart(branch.getKey() + "-JavaErrors",
                                                        branchChartJavaErrorMap, chartDirJava.getAbsolutePath());
                                                makePieChart.generatePieChart(branch.getKey() + "-JavaWarnings",
                                                        branchChartJavaWarningMap, chartDirJava.getAbsolutePath());

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            ActualJavaScriptDetailed javaScriptBugs = branchErrorsAndWarnings
                                                    .getTotalCategorisedCollectionJS();
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaScriptCategories = javaScriptBugs
                                                    .getErrorCategoriesAndItsFilesAndDetails();
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaScriptCategoriesWarnings = javaScriptBugs
                                                    .getWarningCategoriesAndItsFilesAndDetails();
                                            try {
                                                makeCategoriesAndFilesList(javaScriptCategories, "Errors",
                                                        chartDirJavaScript.getAbsolutePath(),
                                                        branchChartJavaScriptErrorMap);
                                                makeCategoriesAndFilesList(javaScriptCategoriesWarnings, "Warnings",
                                                        chartDirJavaScript.getAbsolutePath(),
                                                        branchChartJavaScriptWarningMap);
                                                PieChartDemo makePieChart = new PieChartDemo();
                                                makePieChart.generatePieChart(branch.getKey() + "-JavaScriptErrors",
                                                        branchChartJavaScriptErrorMap,
                                                        chartDirJavaScript.getAbsolutePath());
                                                makePieChart.generatePieChart(branch.getKey() + "-JavaScriptWarnings",
                                                        branchChartJavaScriptWarningMap,
                                                        chartDirJavaScript.getAbsolutePath());

                                            } catch (Exception e) {
                                            }

                                            ActualXMLDetailed xmlBugs = branchErrorsAndWarnings
                                                    .getTotalCategorisedCollectionXML();
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> xmlCategories = xmlBugs
                                                    .getErrorCategoriesAndItsFilesAndDetails();
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> xmlCategoriesWarnings = xmlBugs
                                                    .getWarningCategoriesAndItsFilesAndDetails();
                                            try {
                                                makeCategoriesAndFilesList(xmlCategories, "Errors",
                                                        chartDirXML.getAbsolutePath(), branchChartXMLErrorMap);
                                                makeCategoriesAndFilesList(xmlCategoriesWarnings, "Warnings",
                                                        chartDirXML.getAbsolutePath(), branchChartXMLWarningMap);
                                                PieChartDemo makePieChart = new PieChartDemo();
                                                makePieChart.generatePieChart(branch.getKey() + "-XMLErrors",
                                                        branchChartXMLErrorMap, chartDirXML.getAbsolutePath());
                                                makePieChart.generatePieChart(branch.getKey() + "-XMLWarnings",
                                                        branchChartXMLWarningMap, chartDirXML.getAbsolutePath());

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                        });

    }

    private static void makeCategoriesAndFilesList(
            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> categories, String bugType,
            String repoAndBranchDir, Map<String, Integer> branchChartMap) throws IOException {

        Map<String, Integer> subSysCountMap = new HashMap<>();
        for (Entry<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> fromSubsys : categories.entrySet()) {

            Map<String, Map<String, Map<MultiKey, List<String>>>> subSystemDetails = fromSubsys.getValue();

            for (Entry<String, Map<String, Map<MultiKey, List<String>>>> fromEpic : subSystemDetails.entrySet()) {
                Map<String, Integer> categoryCountMap = new HashMap<>();
                for (Entry<String, Map<MultiKey, List<String>>> fromCategory : fromEpic.getValue().entrySet()) {
                    categoryCountMap.put(fromCategory.getKey(), fromCategory.getValue().size());

                    // Branch Map
                    if (branchChartMap.containsKey(fromCategory.getKey())) {
                        Integer oldCountOFBugsInThatCategory = branchChartMap.get(fromCategory.getKey());
                        branchChartMap.put(fromCategory.getKey(), oldCountOFBugsInThatCategory
                                + fromCategory.getValue().size());
                    }
                    else {
                        branchChartMap.put(fromCategory.getKey(), fromCategory.getValue().size());
                    }

                    // Subsystem Map
                    if (subSysCountMap.containsKey(fromCategory.getKey())) {
                        Integer oldCountOFBugsInThatCategory = subSysCountMap.get(fromCategory.getKey());
                        subSysCountMap.put(fromCategory.getKey(), oldCountOFBugsInThatCategory
                                + fromCategory.getValue().size());
                    }
                    else {
                        subSysCountMap.put(fromCategory.getKey(), fromCategory.getValue().size());
                    }
                }
                Path makeEpicPath = Paths.get(repoAndBranchDir + "\\Epics\\" + bugType);
                Files.createDirectories(makeEpicPath);
                PieChartDemo makePieChart = new PieChartDemo();
                makePieChart.generatePieChart(fromEpic.getKey(), categoryCountMap, makeEpicPath.toString());

            }
            Path makeSubsysPath = Paths.get(repoAndBranchDir + "\\Subsystem\\" + bugType);
            Files.createDirectories(makeSubsysPath);
            PieChartDemo makePieChart = new PieChartDemo();
            makePieChart.generatePieChart(fromSubsys.getKey(), subSysCountMap, makeSubsysPath.toString());
        }
    }
}
