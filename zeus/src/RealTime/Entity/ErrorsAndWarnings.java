package RealTime.Entity;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.keyvalue.MultiKey;

@Getter
@Setter
public class ErrorsAndWarnings {
    Map<MultiKey, List<String>> errorMap;
    Map<MultiKey, List<String>> warningMap;
    Map<MultiKey, Map<String, Integer>> countInfoMap;
    int totalCRErrorCount;
    int totalCRWarningCount;
}
