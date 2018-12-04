package RealTime.SubSystemEpicSplit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassifyUsingController {
    public static Map<String, Set<String>> classifyUsingController(List<String> listOfBranchFiles) {
        Set<String> licenseGroupList = new HashSet<>();
        Set<String> licenseList = new HashSet<>();
        Set<String> subsystemList = new HashSet<>();
        Set<String> epicList = new HashSet<>();
        Map<String, Set<String>> classification = new HashMap<>();
        listOfBranchFiles.stream().filter(file -> file.contains("-front")).forEach(filePath -> {
            Path path = Paths.get(filePath);
            Iterator<Path> iteratorOfPath = path.iterator();
            List<String> foldersOfAFile = new ArrayList<>();
            while (iteratorOfPath.hasNext()) {
                foldersOfAFile.add(iteratorOfPath.next().toString());
            }

            if (foldersOfAFile.contains("controller")) {
                int controllerIndex = foldersOfAFile.indexOf("controller");
                try {
                    licenseGroupList.add(foldersOfAFile.get(controllerIndex + 1));
                    licenseList.add(foldersOfAFile.get(controllerIndex + 2));
                    subsystemList.add(foldersOfAFile.get(controllerIndex + 3));
                    epicList.add(foldersOfAFile.get(controllerIndex + 4));
                } catch (IndexOutOfBoundsException e) {
                    // UnCategorised Files in Controller Folder Throws This Exception.
            }
        }
    }   );

        Iterator<String> iteratorSubsystem = subsystemList.iterator();
        while (iteratorSubsystem.hasNext()) {
            String str = iteratorSubsystem.next();
            if (str.contains(".")) {
                iteratorSubsystem.remove();
            }
        }

        Iterator<String> iteratorEpic = epicList.iterator();
        while (iteratorEpic.hasNext()) {
            String str = iteratorEpic.next();
            if (str.contains(".")) {
                iteratorEpic.remove();
            }
        }

        classification.put("licenseGroups", licenseGroupList);
        classification.put("licenses", licenseList);
        classification.put("subsystems", subsystemList);
        classification.put("epics", epicList);
        return classification;
    }
}
