package RealTime.SessionCache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldVisitor {
	static File passedFile;
	static Map<Integer, List<String>> flagMap=new HashMap<>();
	public static Map<String, Map<MultiKey, List<String>>> sessionFind(Map<String, Map<MultiKey, List<String>>> groupedFilesTotal){
		
		Map<String, Map<MultiKey, List<String>>> groupedFilesTotalFindings=new LinkedHashMap<>();
		
		groupedFilesTotal.entrySet().stream().forEach(repo -> {
			
			Map<MultiKey,List<String>> listOfFindings=new HashMap<>();
			repo.getValue().entrySet().stream().forEach(branch -> {
				List<String> listOfFindingsBranch=new ArrayList<>();
				int countOfFiles=0;
				while(countOfFiles<branch.getValue().size()){
					File theFileToSearch=new File(branch.getValue().get(countOfFiles));
					if(theFileToSearch.getName().endsWith(".java")){
						try {
							passedFile=theFileToSearch;
							getTheFile(theFileToSearch);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if(flagMap.containsKey(1)){
							int count=0;
							while(count<flagMap.get(1).size()){
								listOfFindingsBranch.add(branch.getValue().get(countOfFiles)+"#L"+flagMap.get(1).get(count));
								count++;
							}
							flagMap=new HashMap<>();
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
	public static void getTheFile(File passedFile) throws ParseException, IOException{
		CompilationUnit compilationUnit = JavaParser.parse(passedFile);
		new FieldNameVisitor().visit(compilationUnit,null);
	}

	private static class FieldNameVisitor extends VoidVisitorAdapter<Object> {
		@Override
		public void visit(FieldDeclaration field, Object path) {
			String fieldType=field.getElementType().toString().toLowerCase();
			if(fieldType.contains("session")||fieldType.contains("cache")){
				String fieldName=field.getVariables().get(0).toString();
				LineNumberThatComposeAStatement stat=new LineNumberThatComposeAStatement();
				flagMap=stat.statementsByLine(passedFile,fieldName);
			}
			
		}
	}
}
