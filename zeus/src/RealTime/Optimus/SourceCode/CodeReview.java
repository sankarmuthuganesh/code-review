package RealTime.Optimus.SourceCode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import RealTime.Logger.LogUtils;

import com.github.javaparser.ParseException;

public class CodeReview {

    private final Map<String, List<String>> errorMap = new LinkedHashMap<String, List<String>>();
    private final Map<String, List<String>> warningMap = new LinkedHashMap<String, List<String>>();
    private final Map<String, Map<String, Integer>> countInfoMap = new LinkedHashMap<String, Map<String, Integer>>();
    private final AtomicInteger totalErrorCount = new AtomicInteger(0);
    private final AtomicInteger totalWarningCount = new AtomicInteger(0);
    private final AtomicBoolean isErrorOccurred = new AtomicBoolean(false);

    public CodeReview(Set<String> filesList) {
        LogUtils.print("CodeReview method starts");
        totalErrorCount.set(0);
        Set<String> fileList = filesList;
        fileList.stream().forEach(
                file -> {
                    if (file.endsWith(".java")) {
                        JavaCodeReview javaFileReview;
                        try {
                            javaFileReview = new JavaCodeReview(file);
                            errorMap.put(file, javaFileReview.getErrorList());
                            warningMap.put(file, javaFileReview.getWarningList());
                            countInfoMap.put(file, javaFileReview.getCountInfoMap());
                            totalErrorCount.set(totalErrorCount.get() + javaFileReview.getCountInfoMap().get("error"));
                            totalWarningCount.set(totalWarningCount.get()
                                    + javaFileReview.getCountInfoMap().get("warning"));
                        } catch (IOException exception) {
                            isErrorOccurred.set(true);
                            LogUtils.print((exception + "\t" + exception.getMessage() + "\t" + Arrays.asList(exception
                                    .getStackTrace())));
                        } catch (ParseException exception) {
                            isErrorOccurred.set(true);
                            LogUtils.print((exception + "\t" + exception.getMessage() + "\t" + Arrays.asList(exception
                                    .getStackTrace())));
                        }
                    } else if (file.endsWith(".js")) {
                        JavaScriptCodeReview jsCodeReview = new JavaScriptCodeReview();
                        try {
                            jsCodeReview.findBugs(new File(file));
                            errorMap.put(file, jsCodeReview.getErrorList());
                            warningMap.put(file, jsCodeReview.getWarningList());
                            countInfoMap.put(file, jsCodeReview.getCountInfoMap());
                            totalErrorCount.set(totalErrorCount.get() + jsCodeReview.getCountInfoMap().get("error"));
                            totalWarningCount.set(totalWarningCount.get()
                                    + jsCodeReview.getCountInfoMap().get("warning"));
                        } catch (IOException exception) {
                            isErrorOccurred.set(true);
                            LogUtils.print((exception + "\t" + exception.getMessage() + "\t" + Arrays.asList(exception
                                    .getStackTrace())));
                        }
                    } else if (file.endsWith(".xml")
                            && !file.endsWith(".ja.xml")
                            && !file.endsWith(".en.xml")
                            && !file.equals("pom.xml")) {
                        try {
                            XmlCodeReview xmlCodeReview = new XmlCodeReview(new File(file));
                            errorMap.put(file, xmlCodeReview.getErrorList());
                            warningMap.put(file, xmlCodeReview.getWarningList());
                            countInfoMap.put(file, xmlCodeReview.getCountInfoMap());
                            totalErrorCount.set(totalErrorCount.get() + xmlCodeReview.getCountInfoMap().get("error"));
                            totalWarningCount.set(totalWarningCount.get()
                                    + xmlCodeReview.getCountInfoMap().get("warning"));
                        } catch (IOException exception) {
                            isErrorOccurred.set(true);
                            LogUtils.print((exception + "\t" + exception.getMessage() + "\t" + Arrays.asList(exception
                                    .getStackTrace())));
                        }
                    }
                });
        LogUtils.print("CodeReview method ends");
    }

    public Map<String, List<String>> getErrorMap() {
        return errorMap;
    }

    public Map<String, List<String>> getWarningMap() {
        return warningMap;
    }

    public Map<String, Map<String, Integer>> getCountInfoMap() {
        return countInfoMap;
    }

    public int getTotalErrorCount() {
        return totalErrorCount.get();
    }

    public int getTotalWarningCount() {
        return totalWarningCount.get();
    }

    public boolean isErrorOccurred() {
        return isErrorOccurred.get();
    }
}