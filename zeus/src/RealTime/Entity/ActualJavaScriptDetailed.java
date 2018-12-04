package RealTime.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.keyvalue.MultiKey;

@Getter
@Setter
public class ActualJavaScriptDetailed {
    final List<String> jsCategories = Arrays.asList("Docs", "RestrictedUsages", "AccessOfPrivateMembers",
            "VariableRelated", "BrowserObjects", "CommonBugs", "Functions", "Naming");
    // String-Subsystem, String-Epic String-Category String-FileLinkWithLineNumber,
    // List<String>-CompleteDetailsOfThatFile

    // Different Categories of Errors in JavaScript and Their Corresponding Files Link with LineNumbers and Details
    public Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> errorCategoriesAndItsFilesAndDetails;
    // Different Categories of Warnings in JavaScript and Their Corresponding Files Link with LineNumbers and Details
    public Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> warningCategoriesAndItsFilesAndDetails;
}
