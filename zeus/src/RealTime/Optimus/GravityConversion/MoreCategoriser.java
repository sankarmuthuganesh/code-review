package RealTime.Optimus.GravityConversion;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;

import RealTime.Entity.ErrorsCollection;
import RealTime.Entity.ErrorsCollectionDetailed;
import RealTime.Entity.Java;
import RealTime.Entity.JavaDetailed;
import RealTime.Entity.JavaScript;
import RealTime.Entity.JavaScriptDetailed;
import RealTime.Entity.XML;
import RealTime.Entity.XMLDetailed;
import RealTime.SubSystemEpicSplit.GetMatchedEpicOrSubsystem;

public class MoreCategoriser {

    static Set<String> subSystemField = new HashSet<String>();
    static Set<String> epicField = new HashSet<String>();

    public Map<String, Map<String, ErrorsCollectionDetailed>> makeASubsystemEpicCategorisedOutput(
            Map<String, Map<String, ErrorsCollection>> normallyCategorized,
            Map<String, Map<String, Map<String, Set<String>>>> classification) {
        Map<String, Map<String, ErrorsCollectionDetailed>> detailedCategorized = new HashMap();
        normallyCategorized
                .entrySet()
                .stream()
                .forEach(repository -> {
                    Map<String, ErrorsCollectionDetailed> branchAndCategorisedCollectionDetailed = new HashMap<>();
                    // ForEachBranch

                        // GetRepoClassification
                        Map<String, Map<String, Set<String>>> repoClassfication = classification.get(repository
                                .getKey());

                        repository
                                .getValue()
                                .entrySet()
                                .stream()
                                .forEach(
                                        branch -> {

                                            // GetBranchClassification
                                        Map<String, Set<String>> branchClassfication = repoClassfication
                                                .get(branch
                                                        .getKey());

                                        ErrorsCollection branchErrorsAndWarnings = branch.getValue();
                                        ErrorsCollectionDetailed branchErrorsAndWarningsDetailed = new ErrorsCollectionDetailed();

                                        // For Java
                                        Java javaBugs = branchErrorsAndWarnings.getTotalCategorisedCollectionJava();
                                        Map<String, Map<MultiKey, List<String>>> javaCategories = javaBugs
                                                .getErrorCategoriesAndItsFilesAndDetails();
                                        Map<String, Map<MultiKey, List<String>>> javaCategoriesWarnings = javaBugs
                                                .getWarningCategoriesAndItsFilesAndDetails();
                                        // Detailed Output
                                        Map<String, Map<MultiKey, List<String>>> javaDetailedErrorCategories = makeDetails(
                                                javaCategories, branchClassfication);
                                        Map<String, Map<MultiKey, List<String>>> javaDetailedWarningCategories = makeDetails(
                                                javaCategoriesWarnings, branchClassfication);

                                        JavaDetailed detailedOutJava = new JavaDetailed();
                                        detailedOutJava
                                                .setErrorCategoriesAndItsFilesAndDetails(javaDetailedErrorCategories);
                                        detailedOutJava
                                                .setWarningCategoriesAndItsFilesAndDetails(javaDetailedWarningCategories);

                                        // For JS
                                        JavaScript javaScriptBugs = branchErrorsAndWarnings
                                                .getTotalCategorisedCollectionJS();
                                        Map<String, Map<MultiKey, List<String>>> javaScriptCategories = javaScriptBugs
                                                .getErrorCategoriesAndItsFilesAndDetails();
                                        Map<String, Map<MultiKey, List<String>>> javaScriptCategoriesWarnings = javaScriptBugs
                                                .getWarningCategoriesAndItsFilesAndDetails();
                                        // Detailed Output
                                        Map<String, Map<MultiKey, List<String>>> jsDetailedErrorCategories = makeDetails(
                                                javaScriptCategories, branchClassfication);
                                        Map<String, Map<MultiKey, List<String>>> jsDetailedWarningCategories = makeDetails(
                                                javaScriptCategoriesWarnings, branchClassfication);

                                        JavaScriptDetailed detailedOutJavaScript = new JavaScriptDetailed();
                                        detailedOutJavaScript
                                                .setErrorCategoriesAndItsFilesAndDetails(jsDetailedErrorCategories);
                                        detailedOutJavaScript
                                                .setWarningCategoriesAndItsFilesAndDetails(jsDetailedWarningCategories);

                                        // For XML
                                        XML xmlBugs = branchErrorsAndWarnings.getTotalCategorisedCollectionXML();
                                        Map<String, Map<MultiKey, List<String>>> xmlCategories = xmlBugs
                                                .getErrorCategoriesAndItsFilesAndDetails();
                                        Map<String, Map<MultiKey, List<String>>> xmlCategoriesWarnings = xmlBugs
                                                .getWarningCategoriesAndItsFilesAndDetails();
                                        // Detailed Output
                                        Map<String, Map<MultiKey, List<String>>> xmlDetailedErrorCategories = makeDetails(
                                                xmlCategories, branchClassfication);
                                        Map<String, Map<MultiKey, List<String>>> xmlDetailedWarningCategories = makeDetails(
                                                xmlCategoriesWarnings, branchClassfication);

                                        XMLDetailed detailedOutXML = new XMLDetailed();
                                        detailedOutXML
                                                .setErrorCategoriesAndItsFilesAndDetails(xmlDetailedErrorCategories);
                                        detailedOutXML
                                                .setWarningCategoriesAndItsFilesAndDetails(xmlDetailedWarningCategories);

                                        branchErrorsAndWarningsDetailed
                                                .setTotalCategorisedCollectionJava(detailedOutJava);
                                        branchErrorsAndWarningsDetailed
                                                .setTotalCategorisedCollectionJS(detailedOutJavaScript);
                                        branchErrorsAndWarningsDetailed
                                                .setTotalCategorisedCollectionXML(detailedOutXML);
                                        branchAndCategorisedCollectionDetailed.put(branch.getKey(),
                                                branchErrorsAndWarningsDetailed);
                                    });
                        detailedCategorized.put(repository.getKey(), branchAndCategorisedCollectionDetailed);
                    });
        return detailedCategorized;
    }

    public static Map<String, Map<MultiKey, List<String>>> makeDetails(
            Map<String, Map<MultiKey, List<String>>> passedDetails, Map<String, Set<String>> branchClassfication) {
        Map<String, Map<MultiKey, List<String>>> updatedDetails = new HashMap<>();
        passedDetails
                .entrySet()
                .stream()
                .forEach(
                        category -> {
                            Map<MultiKey, List<String>> javaDetailed = new HashMap<>();
                            category.getValue()
                                    .entrySet()
                                    .forEach(
                                            file -> {

                                                Set<String> epics = branchClassfication.get("epics");
                                                Set<String> subsystems = branchClassfication.get("subsystems");
                                                GetMatchedEpicOrSubsystem match = new GetMatchedEpicOrSubsystem();
                                                Map<String, String> detailsMap = match.getMatches(epics, subsystems,
                                                        file.getKey().getKey(0).toString());

                                                String subsystem = detailsMap.get("subsystem");
                                                String epic = detailsMap.get("epic");

                                                // Map<String, String> epicSubDetails = Splitter
                                                // .getSubsystemAndEpicOfAFile(file.getKey().getKey(0).toString());
                                                // String subsystem = epicSubDetails.get("subsystem");
                                                // String epic = epicSubDetails.get("epic");
                                                // additionalFunctionlityForSplitter(epicSubDetails);
                                                // if ((subsystem.equals("UncategorisedSubsystem") && epic
                                                // .equals("UncategorisedEpic"))) {
                                                // Map<String, String> calcultedMap = checkIfItHasAnySuchStructure(file
                                                // .getKey().getKey(0).toString());
                                                // if (!(calcultedMap.get("subsystem")
                                                // .equals("UncategorisedSubsystem"))) {
                                                // subsystem = calcultedMap.get("subsystem");
                                                // }
                                                // if (!calcultedMap.get("epic").equals("UncategorisedEpic")) {
                                                // epic = calcultedMap.get("epic");
                                                // }
                                                // }
                                            MultiKey fileDetails = new MultiKey(file.getKey().getKey(0).toString(),
                                                    subsystem, epic, file.getKey().getKey(1).toString());
                                            javaDetailed.put(fileDetails, file.getValue());
                                        });
                            updatedDetails.put(category.getKey(), javaDetailed);
                        });
        return updatedDetails;
    }

    private static Map<String, String> checkIfItHasAnySuchStructure(String httpPath) {
        // Works correctly if the subsystem and epic structure of a file has already been put in to that set<string>
        // field of a class
        String subsystem = "UncategorisedSubsystem";
        String epicCalculate = "UncategorisedEpic";
        String convertedPath =
                (httpPath.substring(StringUtils.ordinalIndexOf(httpPath, "/", 7) + 1, httpPath.length())).replace(
                        "/", "\\");
        List<String> detailsOfAFile = new ArrayList<>();
        Path path = Paths.get(convertedPath);
        Iterator<Path> iteratorOfPath = path.iterator();
        while (iteratorOfPath.hasNext()) {
            detailsOfAFile.add(iteratorOfPath.next().toString());
        }
        for (String collectedSubsystem : subSystemField) {
            if (detailsOfAFile.contains(collectedSubsystem)) {
                subsystem = collectedSubsystem;
            }
        }
        for (String collectedEpic : epicField) {
            if (detailsOfAFile.contains(collectedEpic)) {
                epicCalculate = collectedEpic;
            }
        }
        Map<String, String> det = new HashMap<>();
        det.put("epic", epicCalculate);
        det.put("subsystem", subsystem);
        return det;
    }

    public static void additionalFunctionlityForSplitter(Map<String, String> epicSubDetails) {
        String subSystemOfFile = epicSubDetails.get("subsystem");
        String epicOfFile = epicSubDetails.get("epic");
        if (!(subSystemOfFile.equals("UncategorisedSubsystem") && epicOfFile.equals("UncategorisedEpic"))) {
            subSystemField.add(subSystemOfFile);
            epicField.add(epicOfFile);
        }
    }
}
