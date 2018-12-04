package RealTime.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.keyvalue.MultiKey;

@Getter
@Setter
public class JavaDetailed {

    final List<String> categories = Arrays.asList("Variable", "Constants", "VariableName", "PackageClassName",
            "HueSerializable", "DocsComments",
            "Annotations", "ShouldNotUse", "AvoidsAndAlternatives", "DaoNotAllowedChecks", "Global"
            , "DtoEntityRelated", "MethodViolations", "WarningList", "ImportIssue");

    // String-Category, String-FileLinkWithLineNumber, List<String>-CompleteDetailsOfThatFile

    // Different Categories of Errors in Java and Their Corresponding Files Link with LineNumbers and Details
    public Map<String, Map<MultiKey, List<String>>> errorCategoriesAndItsFilesAndDetails;
    // Different Categories of Warnings in Java and Their Corresponding Files Link with LineNumbers and Details
    public Map<String, Map<MultiKey, List<String>>> warningCategoriesAndItsFilesAndDetails;
}
