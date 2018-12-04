package RealTime.MethodFind;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import RealTime.Entity.FileUnit;
import RealTime.Entity.SearchResult;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class MethodFinderFU {
	public List<FileUnit> findTheKeyword(List<FileUnit> files,String methodName) {
		List<FileUnit> searchResults = files.stream().filter(file ->{
			boolean foundFlag=false;
			try {
                CompilationUnit compilationUnit = JavaParser.parse(new File(file.getAbsolutePath()));
                for(MethodDeclaration method:compilationUnit.findAll(MethodDeclaration.class)){
                    if (method.getNameAsString().equalsIgnoreCase(methodName)) {
                    	SearchResult searches=new SearchResult();
						searches.setLineNumber(String.valueOf(method.getBegin().get().line));
						file.setSearches(Arrays.asList(searches));
						foundFlag=true;
                    }
                }
                for(MethodCallExpr methodCall:compilationUnit.findAll(MethodCallExpr.class)){
                    if (methodCall.getNameAsString().equalsIgnoreCase(methodName)) {
                    	  if (methodCall.getNameAsString().equalsIgnoreCase(methodName)) {
                          	SearchResult searches=new SearchResult();
      						searches.setLineNumber(String.valueOf(methodCall.getBegin().get().line));
      						file.setSearches(Arrays.asList(searches));
      						foundFlag=true;
                          }
                    }
                }
			} catch (Exception e) {
			}
			return foundFlag;
		}).collect(Collectors.toList());
		return searchResults;
	}
}
