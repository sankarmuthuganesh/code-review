package RealTime.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.keyvalue.MultiKey;

@Getter
@Setter
public class XMLDetailed {
    final List<String> xmlCategories = Arrays.asList("NamingConvention", "Hardcodes", "Violations", "FileRelated",
            "Naming", "Forbiddens", "Comments");
    // String-Category, String-FileLinkWithLineNumber, List<String>-CompleteDetailsOfThatFile

    // Different Categories of Errors in XML and Their Corresponding Files Link with LineNumbers and Details
    public Map<String, Map<MultiKey, List<String>>> errorCategoriesAndItsFilesAndDetails;
    // Different Categories of Warnings in XML and Their Corresponding Files Link with LineNumbers and Details
    public Map<String, Map<MultiKey, List<String>>> warningCategoriesAndItsFilesAndDetails;
}
