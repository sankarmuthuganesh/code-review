package RealTime.Author;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;

import com.github.javaparser.ParseException;

public class MakeFilesOfAnAuthor {
    public Map<String, Map<MultiKey, List<String>>> getFilesOfSelectedAuthor(List<String> authorsList,
            Map<String, Map<MultiKey, List<String>>> groupedFiles) {

        Map<String, Map<MultiKey, List<String>>> finalBrowseThroughFiles = new HashMap<>();
        groupedFiles.entrySet().stream().forEach(repo -> {
            Map<MultiKey, List<String>> branchAndListofFiles = new LinkedHashMap<>();
            repo.getValue().entrySet().stream().forEach(branch -> {
                List<String> filesList = new ArrayList<>();
                int countOfFiles = 0;
                while (countOfFiles < branch.getValue().size()) {
                    String filePath = branch.getValue().get(countOfFiles);
                    if (!authorsList.isEmpty()) {
                        try {
                            if (doesItContainsAuthorSpecified(filePath, authorsList)) {
                                filesList.add(filePath);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                filesList.add(filePath);
            }
            countOfFiles++;
        }
        branchAndListofFiles.put(branch.getKey(), filesList);
    }       );
            finalBrowseThroughFiles.put(repo.getKey(), branchAndListofFiles);
        });
        return finalBrowseThroughFiles;

    }

    private boolean doesItContainsAuthorSpecified(String filePath, List<String> authorsList) throws IOException,
            ParseException {
        if (filePath.endsWith(".java")) {
            JavaAuthor java = new JavaAuthor();
            java.getJavaAuthor(filePath);
            for (String author : authorsList) {
                if (java.authorOfFile.equals(author)) {
                    return true;
                }
            }
        }
        else if (filePath.endsWith(".js")) {
            JsAuthor js = new JsAuthor();
            for (String author : authorsList) {
                if (js.getJsAuthor(filePath).equals(author)) {
                    return true;
                }
            }
        }
        else {
            // No Author Manipulation can be done!.
            // Because they dont contain author.
            return false;
        }
        return false;
    }

}
