package RealTime.Optimus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.ErrorsAndWarnings;
import RealTime.GitAccess.Progress;
import RealTime.GitAccess.ProgressLife;
import RealTime.Optimus.SourceCode.CodeReviewMain;



public class DefectSheet {

    public void
            printDefectSheet(Map<String, Map<MultiKey, List<String>>> finalBrowseThroughFiles, Progress gitCall) {
        finalBrowseThroughFiles
                .entrySet()
                .stream()
                .forEach(
                        repository -> {
                            Map<MultiKey, ErrorsAndWarnings> branchesAndErrors = new HashMap<>();
                            repository
                                    .getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            branch -> {
                                                List<String> filesPathInTheParticularBranch = branch.getValue();
                                                List<String> filesPathUnnecessaryRemoved = new ArrayList<>();
                                                filesPathInTheParticularBranch.forEach(pathOfFile -> {
                                                    if (pathOfFile.endsWith(".xml")
                                                            || pathOfFile.endsWith(".js")
                                                            || pathOfFile.endsWith(".java")) {
                                                        filesPathUnnecessaryRemoved.add(pathOfFile);
                                                    }
                                                });
                                                try {

                                                    boolean isErrorOccured = CodeReviewMain.checkCodeReview(
                                                            new HashSet<String>(filesPathUnnecessaryRemoved),
                                                            new HashSet<String>());
                                                    CodeReviewMain.generateDefectSheet(repository.getKey(), branch
                                                            .getKey().getKey(0).toString(), gitCall);

                                                } catch (Exception e) {

                                                }
                                            });
                        });

    }

}
