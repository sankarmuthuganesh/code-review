package RealTime.SubSystemEpicSplit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class GetMatchedEpicOrSubsystem {
    public Map<String, String> getMatches(Set<String> epics, Set<String> subsystems, String httpPath) {
        String convertedPath =
                (httpPath.substring(StringUtils.ordinalIndexOf(httpPath, "/", 7) + 1, httpPath.length())).replace(
                        "/", "\\");
        List<String> detailsOfAFile = new ArrayList<>();
        Path path = Paths.get(convertedPath);
        Iterator<Path> iteratorOfPath = path.iterator();
        while (iteratorOfPath.hasNext()) {
            detailsOfAFile.add(iteratorOfPath.next().toString());
        }
        String subsystemMatched = "UnknownSubsystem";

        for (String subsystem : subsystems) {
            if (detailsOfAFile.contains(subsystem)) {
                subsystemMatched = subsystem;
            }
        }
        String epicMatched = "UnknownEpic";
        for (String epic : epics) {
            if (detailsOfAFile.contains(epic)) {
                epicMatched = epic;
            }
        }
        Map<String, String> epicSubsystemDetailsMap = new HashMap<>();
        epicSubsystemDetailsMap.put("epic", epicMatched);
        epicSubsystemDetailsMap.put("subsystem", subsystemMatched);

        return epicSubsystemDetailsMap;

    }
}
