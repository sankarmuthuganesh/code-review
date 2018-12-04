package RealTime.Optimus.GravityConversion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.ErrorsAndWarnings;
import RealTime.Optimus.SourceCode.CodeReviewMain;

public class OptimusMain {
    static String gitUrl;

    public Map<String, Map<MultiKey, ErrorsAndWarnings>> makeInputCompatibleToOptimus(
            Map<String, Map<MultiKey, List<String>>> finalBrowseThroughFiles, String gitConnection)
            throws FileNotFoundException, IOException {
        this.gitUrl = gitConnection;
        Map<String, Map<MultiKey, ErrorsAndWarnings>> optimusExcelOutput = new LinkedHashMap<String, Map<MultiKey,
                ErrorsAndWarnings>>();

        finalBrowseThroughFiles
                .entrySet()
                .stream()
                .forEach(
                        repository -> {
                            Map<MultiKey, ErrorsAndWarnings> branchesAndErrors = new HashMap<>();

                            repository
                                    .getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            branch -> {

                                                List<String> filesPathInTheParticularBranch = branch.getValue();
                                                List<String> filesPathUnnecessaryRemoved = new ArrayList<>();
                                                filesPathInTheParticularBranch.forEach(pathOfFile -> {
                                                    if (pathOfFile.endsWith(".xml")
                                                            || pathOfFile.endsWith(".js")
                                                            || pathOfFile.endsWith(".java")) {
                                                        filesPathUnnecessaryRemoved.add(pathOfFile);
                                                    }
                                                });
                                                try {
                                                    boolean isErrorOccured = CodeReviewMain.checkCodeReview(
                                                            new HashSet<String>(filesPathUnnecessaryRemoved),
                                                            new HashSet<String>());
                                                    // if(!isErrorOccured){
                                            ErrorsAndWarnings totalDetailsForThisBranch = new ErrorsAndWarnings();
                                            Map<String, Map<String, Integer>> countInfoMap = CodeReviewMain.countInfoMap;
                                            Map<String, List<String>> errorMap = CodeReviewMain.errorMap;
                                            Map<String, List<String>> warningMap = CodeReviewMain.warningMap;

                                            Map<MultiKey, List<String>> errorMapMulti = new HashMap<>();
                                            Map<MultiKey, List<String>> warningMapMulti = new HashMap<>();
                                            Map<MultiKey, Map<String, Integer>> countInfoMapMulti = new HashMap<>();

                                            errorMap.entrySet().stream().forEach(file -> {
                                                MultiKey localAndNull = new MultiKey(file.getKey(), null);
                                                errorMapMulti.put(localAndNull, file.getValue());
                                            });

                                            warningMap.entrySet().stream().forEach(file -> {
                                                MultiKey localAndNull = new MultiKey(file.getKey(), null);
                                                warningMapMulti.put(localAndNull, file.getValue());
                                            });

                                            countInfoMap.entrySet().stream().forEach(file -> {
                                                MultiKey localAndNull = new MultiKey(file.getKey(), null);
                                                countInfoMapMulti.put(localAndNull, file.getValue());
                                            });

                                            totalDetailsForThisBranch.setWarningMap(warningMapMulti);
                                            totalDetailsForThisBranch.setErrorMap(errorMapMulti);
                                            totalDetailsForThisBranch.setCountInfoMap(countInfoMapMulti);
                                            totalDetailsForThisBranch
                                                    .setTotalCRErrorCount(CodeReviewMain.totalCRErrorCount);
                                            totalDetailsForThisBranch
                                                    .setTotalCRWarningCount(CodeReviewMain.totalCRWarningCount);

                                            // totalDetailsForThisBranch.setCountInfoMap();
                                            // totalDetailsForThisBranch.setErrorMap();
                                            // totalDetailsForThisBranch.setWarningMap();

                                            branchesAndErrors.put(branch.getKey(), totalDetailsForThisBranch);
                                            // }
                                            // else{
                                            // System.out.println("Some Exception Occured while Reading File");
                                            // }
                                        } catch (Exception e) {
                                        }
                                    });
                            optimusExcelOutput.put(repository.getKey(), branchesAndErrors);

                        });

        return makeListOfUrls(optimusExcelOutput);

    }

    public Map<String, Map<MultiKey, ErrorsAndWarnings>> makeListOfUrls(
            Map<String, Map<MultiKey, ErrorsAndWarnings>> optimusExcelInputHavingfileAbsolutePathAndErrorLists)
            throws FileNotFoundException, IOException {
        Map<String, Map<MultiKey, ErrorsAndWarnings>> filesAndErrorsOrWarningListURLOutput = new LinkedHashMap<>();
        optimusExcelInputHavingfileAbsolutePathAndErrorLists
                .entrySet()
                .stream()
                .forEach(
                        repository -> {
                            Map<MultiKey, ErrorsAndWarnings> branchAndWarnings = new HashMap<>();
                            repository
                                    .getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            branchesAndErrorsMap -> {
                                                Map<MultiKey, List<String>> errorMap = new HashMap<>();
                                                Map<MultiKey, List<String>> warningMap = new HashMap<>();
                                                Map<MultiKey, Map<String, Integer>> countInfoMap = new HashMap<>();
                                                branchesAndErrorsMap
                                                        .getValue()
                                                        .getErrorMap()
                                                        .entrySet()
                                                        .stream()
                                                        .forEach(
                                                                fileAbsolutePathAndErrorList -> {

                                                                    String URLPath = ((fileAbsolutePathAndErrorList
                                                                            .getKey()
                                                                            .getKey(0)
                                                                            .toString()
                                                                            .replace(
                                                                                    branchesAndErrorsMap
                                                                                            .getKey()
                                                                                            .getKey(1)
                                                                                            .toString()
                                                                                    ,
                                                                                    gitUrl
                                                                                            + "root/"
                                                                                            + repository.getKey()
                                                                                            + "/blob/"
                                                                                            + branchesAndErrorsMap
                                                                                                    .getKey().getKey(0)
                                                                                                    .toString())
                                                                            .replace("\\", "/")));
                                                                    MultiKey localAndHttp = new MultiKey(URLPath,
                                                                            fileAbsolutePathAndErrorList.getKey()
                                                                                    .getKey(0).toString());
                                                                    errorMap.put(localAndHttp,
                                                                            fileAbsolutePathAndErrorList.getValue());
                                                                });
                                                branchesAndErrorsMap
                                                        .getValue()
                                                        .getWarningMap()
                                                        .entrySet()
                                                        .stream()
                                                        .forEach(
                                                                fileAbsolutePathAndErrorList -> {
                                                                    String URLPath = ((fileAbsolutePathAndErrorList
                                                                            .getKey()
                                                                            .getKey(0)
                                                                            .toString()
                                                                            .replace(
                                                                                    branchesAndErrorsMap
                                                                                            .getKey()
                                                                                            .getKey(1)
                                                                                            .toString()
                                                                                    ,
                                                                                    gitUrl
                                                                                            + "root/"
                                                                                            + repository.getKey()
                                                                                            + "/blob/"
                                                                                            + branchesAndErrorsMap
                                                                                                    .getKey().getKey(0)
                                                                                                    .toString())
                                                                            .replace("\\", "/")));
                                                                    MultiKey localAndHttp = new MultiKey(URLPath,
                                                                            fileAbsolutePathAndErrorList.getKey()
                                                                                    .getKey(0).toString());
                                                                    warningMap.put(localAndHttp,
                                                                            fileAbsolutePathAndErrorList.getValue());
                                                                });
                                                branchesAndErrorsMap
                                                        .getValue()
                                                        .getCountInfoMap()
                                                        .entrySet()
                                                        .stream()
                                                        .forEach(
                                                                fileAbsolutePathAndErrorList -> {
                                                                    String URLPath = ((fileAbsolutePathAndErrorList
                                                                            .getKey()
                                                                            .getKey(0)
                                                                            .toString()
                                                                            .replace(
                                                                                    branchesAndErrorsMap
                                                                                            .getKey()
                                                                                            .getKey(1)
                                                                                            .toString()
                                                                                    ,
                                                                                    gitUrl
                                                                                            + "root/"
                                                                                            + repository.getKey()
                                                                                            + "/blob/"
                                                                                            + branchesAndErrorsMap
                                                                                                    .getKey().getKey(0)
                                                                                                    .toString())
                                                                            .replace("\\", "/")));
                                                                    MultiKey localAndHttp = new MultiKey(URLPath,
                                                                            fileAbsolutePathAndErrorList.getKey()
                                                                                    .getKey(0).toString());
                                                                    countInfoMap.put(localAndHttp,
                                                                            fileAbsolutePathAndErrorList.getValue());
                                                                });
                                                ErrorsAndWarnings urlUpdated = branchesAndErrorsMap.getValue();
                                                urlUpdated.setErrorMap(errorMap);
                                                urlUpdated.setWarningMap(warningMap);
                                                urlUpdated.setCountInfoMap(countInfoMap);
                                                branchAndWarnings.put(branchesAndErrorsMap.getKey(), urlUpdated);
                                            });
                            filesAndErrorsOrWarningListURLOutput.put(repository.getKey(), branchAndWarnings);
                        });

        return filesAndErrorsOrWarningListURLOutput;
    }
}
