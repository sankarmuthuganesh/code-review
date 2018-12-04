package RealTime.Optimus.GravityConversion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.ActualErrorCollectionDetailed;
import RealTime.Entity.ActualJavaDetailed;
import RealTime.Entity.ActualJavaScriptDetailed;
import RealTime.Entity.ActualXMLDetailed;
import RealTime.Entity.ErrorsCollectionDetailed;

public class SubSystemEpicGrouping {
    static List<String> totalSelectedCategories;

    public Map<String, Map<String, ActualErrorCollectionDetailed>>
            grouper(Map<String, Map<String, ErrorsCollectionDetailed>> detailedCategorized,
                    List<String> totalSelectedCategories) {
        this.totalSelectedCategories = totalSelectedCategories;
        Map<String, Map<String, ActualErrorCollectionDetailed>> out = new HashMap<>();
        detailedCategorized
                .entrySet()
                .stream()
                .forEach(
                        repository -> {
                            Map<String, ActualErrorCollectionDetailed> branchAndCategorisedCollection = new HashMap<>();
                            repository
                                    .getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(branch -> {
                                        ErrorsCollectionDetailed detailed = branch.getValue();
                                        // For Java
                                            Map<String, Map<MultiKey, List<String>>> javaErrors = detailed
                                                    .getTotalCategorisedCollectionJava()
                                                    .getErrorCategoriesAndItsFilesAndDetails();
                                            Map<String, Map<MultiKey, List<String>>> javaWarnings = detailed
                                                    .getTotalCategorisedCollectionJava()
                                                    .getWarningCategoriesAndItsFilesAndDetails();

                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> tooCategorisedJavaError =
                                                    epicSubSystemActualPutter(javaErrors);
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> tooCategorisedJavaWarning =
                                                    epicSubSystemActualPutter(javaWarnings);

                                            // For JS
                                            Map<String, Map<MultiKey, List<String>>> javaScriptErrors = detailed
                                                    .getTotalCategorisedCollectionJS()
                                                    .getErrorCategoriesAndItsFilesAndDetails();
                                            Map<String, Map<MultiKey, List<String>>> javaScriptWarnings = detailed
                                                    .getTotalCategorisedCollectionJS()
                                                    .getWarningCategoriesAndItsFilesAndDetails();

                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> tooCategorisedJavaScriptError =
                                                    epicSubSystemActualPutter(javaScriptErrors);
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> tooCategorisedJavaScriptWarning =
                                                    epicSubSystemActualPutter(javaScriptWarnings);

                                            // For XML
                                            Map<String, Map<MultiKey, List<String>>> xmlErrors = detailed
                                                    .getTotalCategorisedCollectionXML()
                                                    .getErrorCategoriesAndItsFilesAndDetails();
                                            Map<String, Map<MultiKey, List<String>>> xmlWarnings = detailed
                                                    .getTotalCategorisedCollectionXML()
                                                    .getWarningCategoriesAndItsFilesAndDetails();

                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> tooCategorisedxmlError =
                                                    epicSubSystemActualPutter(xmlErrors);
                                            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> tooCategorisedxmlWarning =
                                                    epicSubSystemActualPutter(xmlWarnings);

                                            ActualJavaDetailed java = new ActualJavaDetailed();
                                            ActualJavaScriptDetailed js = new ActualJavaScriptDetailed();
                                            ActualXMLDetailed xml = new ActualXMLDetailed();

                                            java.setErrorCategoriesAndItsFilesAndDetails(tooCategorisedJavaError);
                                            java.setWarningCategoriesAndItsFilesAndDetails(tooCategorisedJavaWarning);

                                            js.setErrorCategoriesAndItsFilesAndDetails(tooCategorisedJavaScriptError);
                                            js.setWarningCategoriesAndItsFilesAndDetails(tooCategorisedJavaScriptWarning);

                                            xml.setErrorCategoriesAndItsFilesAndDetails(tooCategorisedxmlError);
                                            xml.setWarningCategoriesAndItsFilesAndDetails(tooCategorisedxmlWarning);

                                            ActualErrorCollectionDetailed det = new ActualErrorCollectionDetailed();
                                            det.setTotalCategorisedCollectionJava(java);
                                            det.setTotalCategorisedCollectionJS(js);
                                            det.setTotalCategorisedCollectionXML(xml);
                                            branchAndCategorisedCollection.put(branch.getKey(), det);
                                        });
                            out.put(repository.getKey(), branchAndCategorisedCollection);
                        });
        return out;
    }

    public static Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> epicSubSystemActualPutter(
            Map<String, Map<MultiKey, List<String>>> bugs) {
        Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> categoryAndTooCategorized = new HashMap<>();
        for (Entry<String, Map<MultiKey, List<String>>> category : bugs.entrySet()) {
            Map<MultiKey, List<String>> fileDetailsAndFullBugs = category.getValue();
            for (Entry<MultiKey, List<String>> fileDetails : fileDetailsAndFullBugs.entrySet()) {
                String subSystem = fileDetails.getKey().getKey(1).toString();
                String epic = fileDetails.getKey().getKey(2).toString();
                // System.out.println("---"+epic);System.out.println("+++"+subSystem);
                if (categoryAndTooCategorized.containsKey(subSystem)) {
                    Map<String, Map<String, Map<MultiKey, List<String>>>> fromEpicMap = categoryAndTooCategorized
                            .get(subSystem);
                    if (fromEpicMap.containsKey(epic)) {
                        Map<String, Map<MultiKey, List<String>>> categoryMap = fromEpicMap.get(epic);
                        if (categoryMap.containsKey(category.getKey())) {
                            MultiKey localAndHttp = new MultiKey(fileDetails.getKey().getKey(0).toString(), fileDetails
                                    .getKey().getKey(3).toString());
                            categoryMap.get(category.getKey()).put(localAndHttp, fileDetails.getValue());
                        }
                        else {
                            Map<MultiKey, List<String>> fileDet = new HashMap<>();
                            MultiKey localAndHttp = new MultiKey(fileDetails.getKey().getKey(0).toString(), fileDetails
                                    .getKey().getKey(3).toString());
                            fileDet.put(localAndHttp, fileDetails.getValue());
                            categoryMap.put(category.getKey(), fileDet);
                        }
                    }
                    else {
                        Map<String, Map<MultiKey, List<String>>> categoryMap = new HashMap<>();
                        Map<MultiKey, List<String>> fileDet = new HashMap<>();
                        MultiKey localAndHttp = new MultiKey(fileDetails.getKey().getKey(0).toString(), fileDetails
                                .getKey().getKey(3).toString());
                        fileDet.put(localAndHttp, fileDetails.getValue());
                        categoryMap.put(category.getKey(), fileDet);
                        fromEpicMap.put(epic, categoryMap);
                    }
                }
                else {
                    Map<String, Map<String, Map<MultiKey, List<String>>>> fromEpi = new HashMap<>();
                    Map<String, Map<MultiKey, List<String>>> categoryMap = new HashMap<>();
                    Map<MultiKey, List<String>> fileDet = new HashMap<>();
                    MultiKey localAndHttp = new MultiKey(fileDetails.getKey().getKey(0).toString(), fileDetails
                            .getKey().getKey(3).toString());
                    fileDet.put(localAndHttp, fileDetails.getValue());
                    categoryMap.put(category.getKey(), fileDet);
                    fromEpi.put(epic, categoryMap);
                    categoryAndTooCategorized.put(subSystem, fromEpi);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(totalSelectedCategories)) {
            categoryAndTooCategorized.entrySet().forEach(subsystem -> {
                subsystem.getValue().entrySet().forEach(epic -> {
                    epic.getValue().entrySet().removeIf(category -> {
                        for (String categorySelected : totalSelectedCategories) {
                            if (categorySelected.equals(category.getKey())) {
                                return false;
                            }
                        }
                        return true;
                    });
                });
            });
        }
        return categoryAndTooCategorized;
    }
}
