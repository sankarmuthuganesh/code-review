package RealTime.CheckTrial;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class GetMostAffectedAreas {
    public static List<String> getMajorityBugCategories(Map<String, Integer> initialBugCountMap) {
        Comparator<Entry<String, Integer>> compareByValue = (entry1, entry2) ->
                entry1.getValue().compareTo(entry2.getValue());
        Map<String, Integer> bugCountListMap = new LinkedHashMap<>();
        initialBugCountMap
                .entrySet()
                .stream()
                .sorted(compareByValue.reversed())
                .forEachOrdered(mapEntry -> {
                    bugCountListMap.put(mapEntry.getKey(), mapEntry.getValue());
                });
        IntSummaryStatistics totalIssueStats = bugCountListMap.values().
                stream().collect(Collectors.summarizingInt(totalIssues -> totalIssues));
        double issueTotalSum = totalIssueStats.getSum();
        Map<String, Double> issuePercentMap = new LinkedHashMap<>();
        DecimalFormat percentformat = new DecimalFormat("##.##");
        bugCountListMap.entrySet().stream().forEach(mapper -> {
            double percentConversion = (((mapper.getValue().doubleValue()) / issueTotalSum) * 100);
            Double percentDoubleValue = Double.valueOf(percentformat.format(percentConversion));
            issuePercentMap.put(mapper.getKey(), percentDoubleValue);
        });
        DoubleSummaryStatistics totalIssuePercentStats = issuePercentMap.values().
                stream().collect(Collectors.summarizingDouble(totalPercentIssues -> totalPercentIssues));
        double issuePercentAverage = totalIssuePercentStats.getAverage();
        Comparator<Entry<String, Double>> compareByValuePercent = (percentEntry1, percentEntry2) -> percentEntry1
                .getValue().compareTo(percentEntry2.getValue());
        List<String> toFocusOnCategoryList = issuePercentMap
                .entrySet()
                .stream()
                .filter(valueEntry -> {
                    double moreValue = issuePercentAverage - valueEntry.getValue();
                    return (moreValue > 0 && moreValue < 2 || (valueEntry.getValue()) >= issuePercentAverage);
                }).sorted(compareByValuePercent.reversed()).map(percentMapKey -> {
                    return percentMapKey.getKey();
                }).collect(Collectors.toList());
        return toFocusOnCategoryList;
      

    }
}
