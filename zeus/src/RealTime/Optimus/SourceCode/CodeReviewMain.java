package RealTime.Optimus.SourceCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import RealTime.ExcelWriter.ExcelFileCreation;
import RealTime.GitAccess.Progress;
import RealTime.GitAccess.ProgressLife;
import RealTime.Logger.LogUtils;


public class CodeReviewMain {

    public static UUID commitID = null;
    public static int totalCRErrorCount;
    public static int totalCRWarningCount;
    public static Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
    public static Map<String, List<String>> warningMap = new HashMap<String, List<String>>();
    public static Map<String, Map<String, Integer>> countInfoMap = new HashMap<String, Map<String, Integer>>();
    static String defectsFolderPath = "C:/Users/sankraja/AppData/Local/Temp";
    static {
        errorMap = new HashMap<String, List<String>>();
        warningMap = new HashMap<String, List<String>>();
        countInfoMap = new HashMap<String, Map<String, Integer>>();
        commitID = null;
    }
    static Set<String> modifiedFileSetField;
    static Set<String> deletedFileSetField;

    public static boolean checkCodeReview(Set<String> modifiedFileSet, Set<String> deletedFileSet) throws IOException {
        LogUtils.setLogFilePath(defectsFolderPath);
        LogUtils.init();
        LogUtils.print("checkCodeReview method starts");
        clearAllStaticVariables();
        deletedFileSetField = deletedFileSet;
        modifiedFileSetField = modifiedFileSet;

        if (modifiedFileSet.size() > 0 || deletedFileSet.size() > 0) {
            CodeReview codeReview = new CodeReview(modifiedFileSet);
            errorMap.putAll(codeReview.getErrorMap());
            warningMap.putAll(codeReview.getWarningMap());
            countInfoMap.putAll(codeReview.getCountInfoMap());
            totalCRErrorCount = codeReview.getTotalErrorCount();
            totalCRWarningCount = codeReview.getTotalWarningCount();

            // ExcelFileCreation excelFileCreation = new ExcelFileCreation();
            // excelFileCreation.setDefectsFilePath(defectsFolderPath);
            // excelFileCreation.setErrorWarningMaps(errorMap, warningMap, countInfoMap,
            // modifiedFileSet,deletedFileSet);
            // excelFileCreation.setCommitId(commitID);
            // excelFileCreation.create();
            LogUtils.print("checkCodeReview method ends");
            LogUtils.print("*****Summary*****");
            LogUtils.print("Error Count:" + totalCRErrorCount);
            LogUtils.print("Warning Count: " + totalCRWarningCount);
            LogUtils.print("Status: " + ((totalCRErrorCount == 0) ? "success" : "false"));
            LogUtils.end();
            return codeReview.isErrorOccurred();
        } else {
            return true;
        }
    }

    public static void generateDefectSheet(String repository, String branch, Progress gitCall) {
        ExcelFileCreation excelFileCreation = new ExcelFileCreation();
        Path defectsRoute = Paths.get(gitCall.getGravityStoragaePath()
                + "\\" + "GravityOutput-"
                + "sankraja"
                + File.separator
                + "GravityOutput"
                + "\\OptimusDefectSheet\\"
                + repository
                + "\\"
                + branch
                + "\\");
        LogUtils.setLogFilePath(defectsFolderPath);
        LogUtils.init();
        try {
            Files.createDirectories(defectsRoute);
        } catch (Exception e1) {
            LogUtils.print("Cannot create defect folder in D:");
        }
        excelFileCreation.setDefectsFilePath(gitCall.getGravityStoragaePath()
                + "/GravityOutput-"
                + "sankraja"
                + "/GravityOutput/OptimusDefectSheet"
                + "/"
                + repository
                + "/"
                + branch
                + "/");
        excelFileCreation.setErrorWarningMaps(errorMap, warningMap, countInfoMap, modifiedFileSetField,
                deletedFileSetField);
        try {
            excelFileCreation.create();
        } catch (IOException e) {
            LogUtils.print("Failed to Create Defect Sheet (IOException)...");
        }
    }

    private static void createRequiredFolders(String defectsFolderPath) {
        if (!new File(defectsFolderPath + "/").exists()) {
            new File(defectsFolderPath).mkdir();
        }
    }

    private static void clearAllStaticVariables() {
        totalCRErrorCount = 0;
        totalCRWarningCount = 0;
        errorMap = new HashMap<String, List<String>>();
        warningMap = new HashMap<String, List<String>>();
        countInfoMap = new HashMap<String, Map<String, Integer>>();
    }
}