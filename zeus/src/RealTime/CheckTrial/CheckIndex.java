package RealTime.CheckTrial;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

public class CheckIndex {

	public static void main(String[] args) throws FileNotFoundException {
		List<String> branchFiles=new ArrayList<>();
		branchFiles.add("C:\\HUE\\WorkSpace\\Develop\\internal-tool-sync\\Zeus\\src\\RealTime\\CheckTrial\\autoindex\\IndexOnIndexJoinDto.java");
		branchFiles.add("C:\\HUE\\WorkSpace\\Develop\\internal-tool-sync\\Zeus\\src\\RealTime\\CheckTrial\\autoindex\\WrongIndexDto.java");
		
		CompilationUnit parsedFile = JavaParser
				.parse(new File(
						"C:\\HUE\\WorkSpace\\Develop\\internal-tool-sync\\Zeus\\src\\RealTime\\CheckTrial\\autoindex\\WrongIndexDto.java"));
		parsedFile
		.findAll(
				ClassOrInterfaceDeclaration.class)
				.stream()
				.forEachOrdered(
						classOrInterface -> {

							List<String> annotationsOfCurrentClass = classOrInterface
									.getAnnotations()
									.stream()
									.map(AnnotationExpr::getNameAsString)
									.collect(
											Collectors
											.toList());

							for (AnnotationExpr annotation : classOrInterface
									.getAnnotations()) {
								if (annotation instanceof SingleMemberAnnotationExpr) {
									String aa=annotation.getNameAsString();
									if (annotation.getNameAsString().equals("AutoIndex")) {
										SingleMemberAnnotationExpr singleExpression = (SingleMemberAnnotationExpr) annotation;
										String baseClassFileName = singleExpression
												.getMemberValue()
												.toString().trim()
												.replace(
														".class",
														".java");
										boolean found = findUsageOfIndexOfIndex(
												baseClassFileName,
												branchFiles);
										if (found) {
											System.out.println("IndexOFIndexFoundSIngleMenember");
											//error
										}
									}
								} else if (annotation instanceof NormalAnnotationExpr) {
									Map<String, String> keyValueParamMap = new HashMap<>();
									NormalAnnotationExpr annoExpression = (NormalAnnotationExpr) annotation;
									for (MemberValuePair param : annoExpression
											.getPairs()) {
										String value = param
												.getValue()
												.toString().trim();
										String key = param
												.getNameAsString().trim();
										keyValueParamMap
										.put(key,
												value);
									}
									if (annoExpression
											.getNameAsString()
											.equals("AutoIndex")) {
										String baseClassFileName = keyValueParamMap
												.get("value")
												.replace(
														".class",
														".java");
										boolean found = findUsageOfIndexOfIndex(
												baseClassFileName,
												branchFiles);
										if (found) {
											System.out.println("IndexOFIndexFoundMoreMenember");
											//error
										}
									}
									if (annotationsOfCurrentClass
											.contains("AutoIndex")
											&& annoExpression
											.getNameAsString()
											.equals("Join")) {
										String joinClassFileName = keyValueParamMap
												.get("with")
												.replace(
														".class",
														".java");
										boolean found = findUsageOfIndexOfIndex(
												joinClassFileName,
												branchFiles);
										if (found) {
											System.out.println("JoinError");
											//error
										}

										Map<String, List<String>> primaryKeys = getAllPrimaryKeysOfJoinClass(
												joinClassFileName,
												branchFiles);
										List<String> partitionKeys = primaryKeys.get("partitionkeys");
										List<String> clusteringKeys=primaryKeys.get("clusteringkeys");
										List<String> priamryKeys=primaryKeys.get("primarykeys");
										
										
										
										List<String> whereKeys = new ArrayList<>();
										String[] whereValues = keyValueParamMap
												.get("where")
												.replaceAll(
														"[[{}\"#]]",
														StringUtils.EMPTY)
														.split(",");
										for (String whereValue : whereValues) {
											String singleParam;
											String[] keyValue = whereValue.split("==");
											if(keyValue[0].contains("#")){
												String[] joinVarSplit = keyValue[0].split("\\.");
												singleParam = joinVarSplit[1];
											}else{
												String[] joinVarSplit = keyValue[1].split("\\.");
												singleParam = joinVarSplit[1];
											}
											
//											int indexOfDot = whereValue.lastIndexOf(".");
//											String singleParam[] = whereValue
//													.split("\\.");
//											String singleParam = whereValue.substring(indexOfDot+1);
											whereKeys
											.add(singleParam
													.trim());
										}
//										if (!primaryKeys
//												.equals(whereKeys)) {
//											listOfFindingsBranch
//											.add(branch
//													.getValue()
//													.get(countOfFiles.get())
//													+ "#L"
//													+ annotation
//													.getBegin()
//													.get().line);
//										}
										
										if(whereKeys.containsAll(partitionKeys)){
											if(containsOrderedSublist(whereKeys,partitionKeys)){
												
											}else{
											System.out.println("Partition key not in order");
											}
										}else{
											System.out.println("Doesnt contain all partition keys");
											//error
										}
										List<String> clusteringKeysinWhere=new ArrayList<>();
										clusteringKeysinWhere.addAll(whereKeys);
										clusteringKeysinWhere.retainAll(clusteringKeys);
										if(containsOrderedSublist(clusteringKeys,clusteringKeysinWhere)){
											
										}else{
											System.out.println("Clustering key not in order");
											//error
										}
										if(!CollectionUtils.isSubCollection(whereKeys, priamryKeys)){
											System.out.println("Contains somenormal keys");
											//errror
										}
										else{
											if(!containsOrderedSublist(priamryKeys,whereKeys)){
												System.out.println("Key orders are not maintained");
											}
										}
										//Below Code Doesnt Find if Some elements are present in between
//										if(Collections.indexOfSubList(whereKeys, partitionKeys)==-1){
//											listOfFindingsBranch
//											.add(branch
//													.getValue()
//													.get(countOfFiles.get())
//													+ "#L"
//													+ annotation
//													.getBegin()
//													.get().line);
//										}
										
									
										
									}

								}
							}
							// System.out.println(classOrInterface.getAnnotations());
						});
	

	}
	private static Map<String, List<String>> getAllPrimaryKeysOfJoinClass(
			String joinClassFileName, List<String> branchFiles) {

		List<String> primaryKeysOfJoinFile = new ArrayList<>();
		Map<String,List<String>> primarykeys=new HashMap<>();
		List<String> partitionKeysOfJoinFile = new ArrayList<>();
		List<String> clusteringKeysOfJoinFile = new ArrayList<>();

		Optional<String> joinClassPath = branchFiles.stream()
				.filter(file -> file.endsWith(joinClassFileName)).findAny();
		if (joinClassPath.isPresent()) {
			String joinClassFilePath = joinClassPath.get();
			try {
				CompilationUnit joinFileParsed = JavaParser.parse(new File(
						joinClassFilePath));
				joinFileParsed
				.findAll(FieldDeclaration.class)
				.stream()
				.forEachOrdered(
						field -> {
							
							field.getAnnotations().stream().forEachOrdered(anno ->{
								if(anno.getNameAsString().equals("Key")){
									if (anno instanceof NormalAnnotationExpr){
										Map<String, String> keyValueParamMap = new HashMap<>();
										NormalAnnotationExpr annoExpression = (NormalAnnotationExpr)anno;
										for (MemberValuePair param : annoExpression.getPairs()) {
											String value = param.getValue().toString().trim();
											String key = param.getNameAsString().trim();
											keyValueParamMap.put(key, value);
										}
										boolean isPartitionKey=keyValueParamMap.get("isPartitionKey").equals("true");
										if(isPartitionKey){
											partitionKeysOfJoinFile.add(field
													.getVariable(0)
													.getNameAsString());
											primaryKeysOfJoinFile.add(field
													.getVariable(0)
													.getNameAsString());
											
										}else{
											clusteringKeysOfJoinFile.add(field
													.getVariable(0)
													.getNameAsString());
											primaryKeysOfJoinFile.add(field
													.getVariable(0)
													.getNameAsString());
										}
										
									}
								}
							});
							
							
							primarykeys.put("partitionkeys", partitionKeysOfJoinFile);
							primarykeys.put("clusteringkeys", clusteringKeysOfJoinFile);
							primarykeys.put("primarykeys", primaryKeysOfJoinFile);
							
//							Optional<String> primaryKey = field
//									.getAnnotations()
//									.stream()
//									.map(AnnotationExpr::getNameAsString)
//									.filter(annoName -> annoName
//											.equals("Key")).findAny();
//							if (primaryKey.isPresent()) {
//								primaryKeysOfJoinFile.add(field
//										.getVariable(0)
//										.getNameAsString());
//							}
						});
			} catch (Exception e) {
				// Cannot Parse the Base File.
			}
		}
		return primarykeys;
	}

	private static boolean findUsageOfIndexOfIndex(String baseClassFileName,
			List<String> branchFiles) {
		AtomicBoolean found = new AtomicBoolean(false);
		Optional<String> baseClassPath = branchFiles.stream()
				.filter(file -> file.endsWith(baseClassFileName)).findAny();
		if (baseClassPath.isPresent()) {
			String baseClassFilePath = baseClassPath.get();
			try {
				CompilationUnit baseFileParsed = JavaParser.parse(new File(
						baseClassFilePath));
				baseFileParsed
				.findAll(ClassOrInterfaceDeclaration.class)
				.stream()
				.forEachOrdered(
						baseClass -> {
							List<String> annotationsOfBaseClass = baseClass
									.getAnnotations()
									.stream()
									.map(AnnotationExpr::getNameAsString)
									.collect(Collectors.toList());
							if (annotationsOfBaseClass
									.contains("AutoIndex")) {
								found.set(true);
								// Error Index of Index Found
							}
						});
			} catch (Exception e) {
				// Cannot Parse the Base File.
			}
		}
		return found.get();
	}
	 private static boolean containsOrderedSublist(List<String> list,List<String> sublist){
			Iterator<String> listIter=list.iterator();
			for(String item:sublist){
				if(!listIter.hasNext()){
					//still elements in list but no in sublist
					return false;
				}
				while(listIter.hasNext()&&!listIter.next().equals(item)){
					if(!listIter.hasNext()){
						return false;
					}
					// do nothing, consume the list until item is found
				}
			}
			//entire sublist found in list
			return true;
	    }
}
