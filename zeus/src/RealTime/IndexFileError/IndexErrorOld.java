package RealTime.IndexFileError;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

// Only Method calls that are terminated...
public class IndexErrorOld {

    public Map<String, Map<MultiKey, List<String>>> getJoinErrorFiles(
            Map<String, Map<MultiKey, List<String>>> groupedFilesTotal) {
        Map<String, Map<MultiKey, List<String>>> groupedFilesTotalFindings = new LinkedHashMap<>();

        groupedFilesTotal
                .entrySet()
                .stream()
                .forEach(
                        repo -> {
                            Map<MultiKey, List<String>> listOfFindings = new HashMap<>();
                            repo.getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            branch -> {
                                                List<String> listOfFindingsBranch = new ArrayList<>();
                                                int countOfFiles = 0;
                                                while (countOfFiles < branch.getValue().size()) {
                                                    File theFileToSearch = new File(branch.getValue().get(countOfFiles));
                                                    if (theFileToSearch.getName().endsWith("Index.java")) {
                                                        CompilationUnit parsedFile;
                                                        List<String> lineNumber = new ArrayList<>();
                                                        try {
                                                            parsedFile = JavaParser.parse(theFileToSearch);
                                                            new AnnotaionVisitor().visit(parsedFile, lineNumber);
                                                        } catch (Exception e) {
                                                        }

                                                        if (!lineNumber.isEmpty()) {
                                                            int countLines = 0;
                                                            while (countLines < lineNumber.size()) {
                                                                listOfFindingsBranch.add(branch.getValue().get(
                                                                        countOfFiles)
                                                                        + "#L"
                                                                        + lineNumber.get(countLines));
                                                                countLines++;
                                                            }
                                                            lineNumber = new ArrayList<>();
                                                        }
                                                    }
                                                    countOfFiles++;
                                                }
                                                listOfFindings.put(branch.getKey(), listOfFindingsBranch);
                                            });

                            groupedFilesTotalFindings.put(repo.getKey(), listOfFindings);
                        });
        return groupedFilesTotalFindings;
    }

    private class AnnotaionVisitor extends VoidVisitorAdapter {
    	
        @Override
        public void visit(ClassOrInterfaceDeclaration clas, Object lineNumber) {
            List<String> lineNumberList = (List<String>)lineNumber;
            for (AnnotationExpr annos : clas.getAnnotations()) {
                if (annos instanceof MarkerAnnotationExpr) {

                }
                if (annos instanceof SingleMemberAnnotationExpr) {
                }
              
                if (annos instanceof NormalAnnotationExpr) {
                	  Map<String,String> keyValueParamMap=new HashMap<>();
                    NormalAnnotationExpr paramAnno = (NormalAnnotationExpr)annos;
                    for (MemberValuePair param : paramAnno.getPairs()) {
                      	String value=param.getValue().toString();
                      	String key=param.getNameAsString();
                      	keyValueParamMap.put(key, value);
                	  }
                   
                    
                   
                    for (MemberValuePair param : paramAnno.getPairs()) {
                    	
                    	
                    	String value=param.getValue().toString();
                    	if(value.equals("")){
                    		
                    	}
                    	
                        // if(param.getName().toString().equals("with")){
                        if (param.getValue().toString().contains("Index.class")) {
                            lineNumberList.add(paramAnno.getBegin().get().line + "-" + paramAnno.getEnd().get().line);
                            // }
                        }
                    }
                }
            }
        }
    }
}
