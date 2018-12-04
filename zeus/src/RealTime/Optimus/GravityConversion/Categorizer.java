package RealTime.Optimus.GravityConversion;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.ErrorsAndWarnings;
import RealTime.Entity.ErrorsCollection;
import RealTime.Entity.Java;
import RealTime.Entity.JavaScript;
import RealTime.Entity.XML;

@Getter
public class Categorizer {
    int totalCRErrorCount;
    int totalCRWarningCount;

    public Map<String, Map<String, ErrorsCollection>> makeIntoCategories(
            Map<String, Map<MultiKey, ErrorsAndWarnings>> outputForOptimusExcel) {
        Map<String, Map<String, ErrorsCollection>> categorisedOutput = new LinkedHashMap<>();

        outputForOptimusExcel
                .entrySet()
                .stream()
                .forEach(repository -> {
                    Map<String, ErrorsCollection> branchAndCategorisedCollection = new HashMap<>();
                    // ForEachBranch
                        repository
                                .getValue()
                                .entrySet()
                                .stream()
                                .forEach(branch -> {
                                    // ForEachBranchErrorsAndWarnings
                                        Map<MultiKey, List<String>> errorMap = branch.getValue().getErrorMap();
                                        Map<MultiKey, List<String>> warningMap = branch.getValue().getWarningMap();
                                        Map<MultiKey, Map<String, Integer>> countInfoMap = branch.getValue()
                                                .getCountInfoMap();
                                        this.totalCRErrorCount = branch.getValue().getTotalCRErrorCount();
                                        this.totalCRWarningCount = branch.getValue().getTotalCRWarningCount();

                                        Java errorWarningJava = new Java();
                                        JavaScript errorWarningJs = new JavaScript();
                                        XML errorWarningXML = new XML();

                                        Map<String, Map<MultiKey, List<String>>> errorJavaMap = new LinkedHashMap<>();
                                        Map<String, Map<MultiKey, List<String>>> warningJavaMap = new LinkedHashMap<>();
                                        Map<String, Map<MultiKey, List<String>>> errorJSMap = new LinkedHashMap<>();
                                        Map<String, Map<MultiKey, List<String>>> warningJSMap = new LinkedHashMap<>();
                                        Map<String, Map<MultiKey, List<String>>> errorXMLMap = new LinkedHashMap<>();
                                        Map<String, Map<MultiKey, List<String>>> warningXMLMap = new LinkedHashMap<>();

                                        // errorMap construction
                                        for (Map.Entry<MultiKey, List<String>> eachMap : errorMap.entrySet()) {
                                            if (eachMap.getKey().getKey(0).toString().endsWith(".java")) {
                                                List<String> categoriesOfAFile = eachMap.getValue().stream()
                                                        .sequential()
                                                        .map(error -> error.substring(error.lastIndexOf("|") + 1))
                                                        .collect(Collectors.toList());
                                                List<String> lineNumberForEachCategory = eachMap
                                                        .getValue()
                                                        .stream()
                                                        .sequential()
                                                        .map(error -> {
                                                            if (error.contains("Line No")) {
                                                                int indexWhereLineNumberStarts = error
                                                                        .indexOf("Line No");
                                                                StringBuilder lineNumber = new StringBuilder();
                                                                char[] arr = error.substring(
                                                                        indexWhereLineNumberStarts + 9,
                                                                        indexWhereLineNumberStarts + 14).toCharArray();
                                                                for (int i = 0; i < arr.length; i++) {
                                                                    if (isValid(String.valueOf(arr[i]))) {
                                                                        lineNumber.append(String.valueOf(arr[i]));
                                                                    }
                                                                }
                                                                return lineNumber.toString();

                                                            } else {
                                                                return "";
                                                            }
                                                        }).collect(Collectors.toList());

                                                for (int n = 0; n < categoriesOfAFile.size(); n++) {
                                                    if (errorJavaMap.containsKey(categoriesOfAFile.get(n))) {
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        errorJavaMap.get(categoriesOfAFile.get(n)).put(
                                                                fileLinkAndLocal, eachMap.getValue());
                                                    }
                                                    else {
                                                        Map<MultiKey, List<String>> fileLinkAndFullDetails = new HashMap<>();
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        fileLinkAndFullDetails.put(fileLinkAndLocal, eachMap.getValue());
                                                        errorJavaMap.put(categoriesOfAFile.get(n),
                                                                fileLinkAndFullDetails);
                                                    }
                                                }
                                            }
                                            else if (eachMap.getKey().getKey(0).toString().endsWith(".js")) {
                                                List<String> categoriesOfAFile = eachMap.getValue().stream()
                                                        .sequential()
                                                        .map(error -> error.substring(error.lastIndexOf("|") + 1))
                                                        .collect(Collectors.toList());
                                                List<String> lineNumberForEachCategory = eachMap
                                                        .getValue()
                                                        .stream()
                                                        .sequential()
                                                        .map(error -> {
                                                            if (error.contains("Line No")) {
                                                                int indexWhereLineNumberStarts = error
                                                                        .indexOf("Line No");
                                                                StringBuilder lineNumber = new StringBuilder();
                                                                char[] arr = error.substring(
                                                                        indexWhereLineNumberStarts + 9,
                                                                        indexWhereLineNumberStarts + 14).toCharArray();
                                                                for (int i = 0; i < arr.length; i++) {
                                                                    if (isValid(String.valueOf(arr[i]))) {
                                                                        lineNumber.append(String.valueOf(arr[i]));
                                                                    }
                                                                }
                                                                return lineNumber.toString();
                                                            } else {
                                                                return "";
                                                            }

                                                        }).collect(Collectors.toList());
                                                for (int n = 0; n < categoriesOfAFile.size(); n++) {
                                                    if (errorJSMap.containsKey(categoriesOfAFile.get(n))) {
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        errorJSMap.get(categoriesOfAFile.get(n)).put(fileLinkAndLocal,
                                                                eachMap.getValue());
                                                    }
                                                    else {
                                                        Map<MultiKey, List<String>> fileLinkAndFullDetails = new HashMap<>();
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        fileLinkAndFullDetails.put(fileLinkAndLocal, eachMap.getValue());
                                                        errorJSMap.put(categoriesOfAFile.get(n), fileLinkAndFullDetails);
                                                    }
                                                }
                                            }
                                            else if (eachMap.getKey().getKey(0).toString().endsWith(".xml")) {
                                                List<String> categoriesOfAFile = eachMap.getValue().stream()
                                                        .sequential()
                                                        .map(error -> error.substring(error.lastIndexOf("|") + 1))
                                                        .collect(Collectors.toList());
                                                List<String> lineNumberForEachCategory = eachMap
                                                        .getValue()
                                                        .stream()
                                                        .sequential()
                                                        .map(error -> {
                                                            if (error.contains("Line No")) {
                                                                int indexWhereLineNumberStarts = error
                                                                        .indexOf("Line No");
                                                                StringBuilder lineNumber = new StringBuilder();
                                                                char[] arr = error.substring(
                                                                        indexWhereLineNumberStarts + 9,
                                                                        indexWhereLineNumberStarts + 14).toCharArray();
                                                                for (int i = 0; i < arr.length; i++) {
                                                                    if (isValid(String.valueOf(arr[i]))) {
                                                                        lineNumber.append(String.valueOf(arr[i]));
                                                                    }
                                                                }
                                                                return lineNumber.toString();
                                                            } else {
                                                                return "";
                                                            }

                                                        }).collect(Collectors.toList());
                                                for (int n = 0; n < categoriesOfAFile.size(); n++) {
                                                    if (errorXMLMap.containsKey(categoriesOfAFile.get(n))) {
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        errorXMLMap.get(categoriesOfAFile.get(n)).put(fileLinkAndLocal,
                                                                eachMap.getValue());
                                                    }
                                                    else {
                                                        Map<MultiKey, List<String>> fileLinkAndFullDetails = new HashMap<>();
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        fileLinkAndFullDetails.put(fileLinkAndLocal, eachMap.getValue());
                                                        errorXMLMap.put(categoriesOfAFile.get(n),
                                                                fileLinkAndFullDetails);
                                                    }
                                                }
                                            }

                                        }
                                        // warningJavaMap construction
                                        for (Map.Entry<MultiKey, List<String>> eachMap : warningMap.entrySet()) {
                                            if (eachMap.getKey().getKey(0).toString().endsWith(".java")) {
                                                List<String> categoriesOfAFile = eachMap.getValue().stream()
                                                        .sequential()
                                                        .map(error -> error.substring(error.lastIndexOf("|") + 1)
                                                        ).collect(Collectors.toList());

                                                List<String> lineNumberForEachCategory = eachMap
                                                        .getValue()
                                                        .stream()
                                                        .sequential()
                                                        .map(error -> {
                                                            if (error.contains("Line No")) {
                                                                int indexWhereLineNumberStarts = error
                                                                        .indexOf("Line No");
                                                                StringBuilder lineNumber = new StringBuilder();
                                                                char[] arr = error.substring(
                                                                        indexWhereLineNumberStarts + 9,
                                                                        indexWhereLineNumberStarts + 14).toCharArray();
                                                                for (int i = 0; i < arr.length; i++) {
                                                                    if (isValid(String.valueOf(arr[i]))) {
                                                                        lineNumber.append(String.valueOf(arr[i]));
                                                                    }
                                                                }
                                                                return lineNumber.toString();
                                                            } else {
                                                                return "";
                                                            }

                                                        }).collect(Collectors.toList());

                                                for (int n = 0; n < categoriesOfAFile.size(); n++) {
                                                    if (warningJavaMap.containsKey(categoriesOfAFile.get(n))) {
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        warningJavaMap.get(categoriesOfAFile.get(n)).put(
                                                                fileLinkAndLocal, eachMap.getValue());
                                                    }
                                                    else {
                                                        Map<MultiKey, List<String>> fileLinkAndFullDetails = new HashMap<>();
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        fileLinkAndFullDetails.put(fileLinkAndLocal, eachMap.getValue());
                                                        warningJavaMap.put(categoriesOfAFile.get(n),
                                                                fileLinkAndFullDetails);
                                                    }
                                                }
                                            }

                                            else if (eachMap.getKey().getKey(0).toString().endsWith(".js")) {
                                                List<String> categoriesOfAFile = eachMap.getValue().stream()
                                                        .sequential()
                                                        .map(error -> error.substring(error.lastIndexOf("|") + 1))
                                                        .collect(Collectors.toList());
                                                List<String> lineNumberForEachCategory = eachMap
                                                        .getValue()
                                                        .stream()
                                                        .sequential()
                                                        .map(error -> {
                                                            if (error.contains("Line No")) {
                                                                int indexWhereLineNumberStarts = error
                                                                        .indexOf("Line No");
                                                                StringBuilder lineNumber = new StringBuilder();
                                                                char[] arr = error.substring(
                                                                        indexWhereLineNumberStarts + 9,
                                                                        indexWhereLineNumberStarts + 14).toCharArray();
                                                                for (int i = 0; i < arr.length; i++) {
                                                                    if (isValid(String.valueOf(arr[i]))) {
                                                                        lineNumber.append(String.valueOf(arr[i]));
                                                                    }
                                                                }
                                                                return lineNumber.toString();
                                                            } else {
                                                                return "";
                                                            }

                                                        }).collect(Collectors.toList());
                                                for (int n = 0; n < categoriesOfAFile.size(); n++) {
                                                    if (warningJSMap.containsKey(categoriesOfAFile.get(n))) {
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        warningJSMap.get(categoriesOfAFile.get(n)).put(
                                                                fileLinkAndLocal, eachMap.getValue());
                                                    }
                                                    else {
                                                        Map<MultiKey, List<String>> fileLinkAndFullDetails = new HashMap<>();
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        fileLinkAndFullDetails.put(fileLinkAndLocal, eachMap.getValue());
                                                        warningJSMap.put(categoriesOfAFile.get(n),
                                                                fileLinkAndFullDetails);
                                                    }
                                                }
                                            }
                                            else if (eachMap.getKey().getKey(0).toString().endsWith(".xml")) {
                                                List<String> categoriesOfAFile = eachMap.getValue().stream()
                                                        .sequential()
                                                        .map(error -> error.substring(error.lastIndexOf("|") + 1))
                                                        .collect(Collectors.toList());
                                                List<String> lineNumberForEachCategory = eachMap
                                                        .getValue()
                                                        .stream()
                                                        .sequential()
                                                        .map(error -> {
                                                            if (error.contains("Line No")) {
                                                                int indexWhereLineNumberStarts = error
                                                                        .indexOf("Line No");
                                                                StringBuilder lineNumber = new StringBuilder();
                                                                char[] arr = error.substring(
                                                                        indexWhereLineNumberStarts + 9,
                                                                        indexWhereLineNumberStarts + 14).toCharArray();
                                                                for (int i = 0; i < arr.length; i++) {
                                                                    if (isValid(String.valueOf(arr[i]))) {
                                                                        lineNumber.append(String.valueOf(arr[i]));
                                                                    }
                                                                }
                                                                return lineNumber.toString();
                                                            } else {
                                                                return "";
                                                            }

                                                        }).collect(Collectors.toList());

                                                for (int n = 0; n < categoriesOfAFile.size(); n++) {
                                                    if (warningXMLMap.containsKey(categoriesOfAFile.get(n))) {
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        warningXMLMap.get(categoriesOfAFile.get(n)).put(
                                                                fileLinkAndLocal, eachMap.getValue());
                                                    }
                                                    else {
                                                        Map<MultiKey, List<String>> fileLinkAndFullDetails = new HashMap<>();
                                                        String lineLink = eachMap.getKey().getKey(0).toString()
                                                                + "#L"
                                                                + lineNumberForEachCategory.get(n);
                                                        MultiKey fileLinkAndLocal = new MultiKey(lineLink, eachMap
                                                                .getKey().getKey(1).toString());
                                                        fileLinkAndFullDetails.put(fileLinkAndLocal, eachMap.getValue());
                                                        warningXMLMap.put(categoriesOfAFile.get(n),
                                                                fileLinkAndFullDetails);
                                                    }
                                                }
                                            }
                                        }

                                        errorWarningJava.setErrorCategoriesAndItsFilesAndDetails(errorJavaMap);
                                        errorWarningJava.setWarningCategoriesAndItsFilesAndDetails(warningJavaMap);
                                        errorWarningJs.setErrorCategoriesAndItsFilesAndDetails(errorJSMap);
                                        errorWarningJs.setWarningCategoriesAndItsFilesAndDetails(warningJSMap);
                                        errorWarningXML.setErrorCategoriesAndItsFilesAndDetails(errorXMLMap);
                                        errorWarningXML.setWarningCategoriesAndItsFilesAndDetails(warningXMLMap);
                                        // System.out.println(errorJavaMap);
                                        // for(Map.Entry<String,Map<String,List<String>>> a:errorJavaMap.entrySet()){
                                        // System.out.println(a.getKey());
                                        // System.out.println(a.getValue());
                                        // System.out.println("------------------------------------------------");
                                        // }
                                        // System.out.println(warningJavaMap);
                                        // System.out.println(errorJSMap);
                                        // System.out.println(warningJSMap);
                                        // System.out.println(errorXMLMap);
                                        // System.out.println(warningXMLMap);

                                        ErrorsCollection categorizedAllCollection = new ErrorsCollection();
                                        categorizedAllCollection.setTotalCategorisedCollectionJava(errorWarningJava);
                                        categorizedAllCollection.setTotalCategorisedCollectionJS(errorWarningJs);
                                        categorizedAllCollection.setTotalCategorisedCollectionXML(errorWarningXML);

                                        branchAndCategorisedCollection.put(branch.getKey().getKey(0).toString(),
                                                categorizedAllCollection);

                                    });
                        categorisedOutput.put(repository.getKey(), branchAndCategorisedCollection);
                    });
        return categorisedOutput;
    }

    public boolean isValid(String lineRange) {
        return lineRange.matches("^\\d+$");

    }
}
