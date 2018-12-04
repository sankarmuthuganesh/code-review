package RealTime.MethodFind;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections4.keyvalue.MultiKey;


import RealTime.CheckTrial.FindPrivateJs;
import RealTime.CheckTrial.HoverButtonFind;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class MethodFinder {

    public Map<String, Map<MultiKey, List<String>>> findTheMethod(String methodName,
            Map<String, Map<MultiKey, List<String>>> repoBranchesAndListofFiles) {
        Map<String, Map<MultiKey, List<String>>> findingsRepoBranchesAndListofFiles = new HashMap<>();
        repoBranchesAndListofFiles.entrySet().stream().forEach(repository -> {
            Map<MultiKey, List<String>> listOfFindings = new HashMap<>();
            repository.getValue().entrySet().stream().forEach(branch -> {
                List<String> listOfFindingsBranch = new ArrayList<>();
                int countOfFiles = 0;
                while (countOfFiles < branch.getValue().size()) {
                    File theFileToSearch = new File(branch.getValue().get(countOfFiles));
//                    if(new FindPrivateJs().getPrivatePopover((branch.getValue().get(countOfFiles)))){
//						listOfFindingsBranch.add(branch.getValue().get(countOfFiles));
//					}
                    try {
                        CompilationUnit compilationUnit = JavaParser.parse(theFileToSearch);
                        for(MethodDeclaration method:compilationUnit.findAll(MethodDeclaration.class)){

                            if (method.getNameAsString().equalsIgnoreCase(methodName)) {
                            	listOfFindingsBranch.add(branch.getValue().get(countOfFiles) + "#L" + method.getBegin().get().line);
                            }
                        }
                        for(MethodCallExpr methodCall:compilationUnit.findAll(MethodCallExpr.class)){
                            if (methodCall.getNameAsString().equalsIgnoreCase(methodName)) {
                            	listOfFindingsBranch.add(branch.getValue().get(countOfFiles) + "#L" + methodCall.getBegin().get().line);
                            }
                        }
                    } catch (FileNotFoundException |ParseProblemException e) {
                        // Cannot find the file in the specified absolute path.
                    }
                countOfFiles++;
            }
            listOfFindings.put(branch.getKey(), listOfFindingsBranch);
        }   );
            findingsRepoBranchesAndListofFiles.put(repository.getKey(), listOfFindings);
        });
        return findingsRepoBranchesAndListofFiles;
    }
}
