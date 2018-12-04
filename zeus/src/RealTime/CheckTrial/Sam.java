package RealTime.CheckTrial;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class Sam {
public static void main(String[] args)
{
    List<String> epics=Arrays.asList("orderentry","ordersreceivedmanagement");
    Comparator<String> epicAlmostMatch = (exactepic1, exactepic2) -> Integer.compare(
        StringUtils.getLevenshteinDistance(
                "Orders Recieved Management".toLowerCase().replace(StringUtils.SPACE, StringUtils.EMPTY),
                exactepic1),
        StringUtils.getLevenshteinDistance(
                "Orders Recieved Management".toLowerCase().replace(StringUtils.SPACE, StringUtils.EMPTY),
                exactepic2));
Optional<String> epicMatched = epics.stream().sorted(epicAlmostMatch).findFirst();
System.out.println(epicMatched.get());
}
}
