package RealTime.UnnecessaryConstantsFields.java;

import java.io.File;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.google.common.io.Files;
/**
 * Finds Unnecessary Fields and Constants in Java File.
 *
 * @author Sankar
 * 
 */

public class GlobalVarList {
    Map<String, Map<MultiKey, List<String>>> findingsRepoBranchesAndListofFiles = new HashMap<>();
    public Map<String, Map<MultiKey, List<String>>> findUnnecessaryFields(Map<String, Map<MultiKey, List<String>>> repoBranchesAndListofFiles){
    repoBranchesAndListofFiles.entrySet().stream().forEach(repository -> {
        Map<MultiKey, List<String>> listOfFindings = new HashMap<>();
        repository.getValue().entrySet().stream().forEach(branch -> {
            List<String> listOfFindingsBranch = new ArrayList<>();
            int countOfFiles = 0;
            while (countOfFiles < branch.getValue().size()) {
            	  // Storage Maps
                Map<MultiKey, Integer> constantAndLineNumberMap = new HashMap<>();
                Map<String, Integer> referenceFieldsAndLineNumberMap = new HashMap<>();

                // Result Maps
                List<Integer> invalidFieldLineNumbers = new ArrayList<>();
                
                File fileToCheck = new File(branch.getValue().get(countOfFiles));
                if (fileToCheck.getName().endsWith(".java")) {
                	if(fileToCheck.getName().toLowerCase().contains("controller")||fileToCheck.getName().toLowerCase().contains("impl")){
                        String exactFileName = fileToCheck.getName().replace(".java", StringUtils.EMPTY);
                        // Has to Exlude Unnecessary Files.
                        try {
                            CompilationUnit compilationUnit = JavaParser.parse(fileToCheck);

                            compilationUnit
                                    .findAll(FieldDeclaration.class)
                                    .stream()
                                    .forEach(field -> {
                                        if (field.isFinal() && field.isStatic()) {
                                        	if(field.getElementType().asString().equals("String[]") ||
                                                    field.getElementType().asString().contains("List<") ||
                                                    field.getElementType().asString().contains("Map<") ||
                                                    field.getElementType().asString().contains("Set<")){
                                        		invalidFieldLineNumbers.add(field
                                                          .getBegin()
                                                          .get().line);
                                        	}
                                        }
                                
                               
                                }   );


                            for(Integer invalidFieldLineNumber:invalidFieldLineNumbers){
                            	listOfFindingsBranch.add(branch.getValue().get(countOfFiles) + "#L" + invalidFieldLineNumber);
                            }
                            
                        } catch (FileNotFoundException e) {
                            // Cannot find the file in the specified absolute path.
                        }
                	}
                	
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
