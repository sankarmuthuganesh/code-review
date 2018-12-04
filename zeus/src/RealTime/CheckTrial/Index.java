package RealTime.CheckTrial;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class Index {
	public static void main(String[] args) throws FileNotFoundException {
		List<String> branchFiles=new ArrayList<>();
		CompilationUnit parsedFile = JavaParser
				.parse(new File(
						"C:\\HUE\\WorkSpace\\Develop\\internal-tool-sync\\Zeus\\src\\RealTime\\CheckTrial\\JvCapitalWebApiServiceImpl.java"));
		
		parsedFile
				.findAll(ClassOrInterfaceDeclaration.class).stream()
				.forEachOrdered(
						classOrInterface -> {
							
							List<String> annotationsOfCurrentClass=classOrInterface.getAnnotations().stream().map(AnnotationExpr::getNameAsString)
									.collect(Collectors.toList());
							
							for (AnnotationExpr annotation : classOrInterface.getAnnotations()) {
								//System.out.println(annotation);
								if (annotation instanceof SingleMemberAnnotationExpr) {
									SingleMemberAnnotationExpr singleExpression = (SingleMemberAnnotationExpr)annotation;
									String baseClassFileName = singleExpression.getMemberValue().toString().replace(".class", ".java");
									boolean found = findUsageOfIndexOfIndex(baseClassFileName,branchFiles);
								}
								else if (annotation instanceof NormalAnnotationExpr) {
									Map<String, String> keyValueParamMap = new HashMap<>();
									NormalAnnotationExpr annoExpression = (NormalAnnotationExpr)annotation;
									for (MemberValuePair param : annoExpression.getPairs()) {
										String value = param.getValue().toString();
										String key = param.getNameAsString();
										keyValueParamMap.put(key, value);
									}
									if (annoExpression.getNameAsString().equals("Autoindex")) {
										String baseClassFileName = keyValueParamMap.get("value").replace(".class", ".java");
										boolean found = findUsageOfIndexOfIndex(baseClassFileName,branchFiles);
									}
									if (annotationsOfCurrentClass.contains("AutoIndex")&&annoExpression.getNameAsString().equals("Join")) {
										String joinClassFileName = keyValueParamMap.get("with").replace(".class", ".java");
										boolean found = findUsageOfIndexOfIndex(joinClassFileName,branchFiles);
									}
									if (annoExpression.getNameAsString().equals("Join")) {
										String joinClassFileName = keyValueParamMap.get("with").replace(".class", ".java");
										List<String> primaryKeys = getAllPrimaryKeysOfJoinClass(joinClassFileName,branchFiles);
										List<String> whereKeys=new ArrayList<>();
										String[] whereValues = keyValueParamMap.get("where").replaceAll("[[{}\"]]", StringUtils.EMPTY).split(",");
										for(String whereValue:whereValues){
								    		String singleParam[]=whereValue.split("==");
								    		whereKeys.add(singleParam[0].trim());
								    	}
										if(!primaryKeys.equals(whereKeys)){
											//Error
										}
									}
								}
							}
							// System.out.println(classOrInterface.getAnnotations());
						});

	}

	private static List<String> getAllPrimaryKeysOfJoinClass(
			String joinClassFileName, List<String> branchFiles) {
		
		List<String> primaryKeysOfJoinFile=new ArrayList<>();
		
		Optional<String> joinClassPath = branchFiles.stream().filter(file -> file.endsWith(joinClassFileName)).findAny();
		if(joinClassPath.isPresent()){
			String joinClassFilePath = joinClassPath.get();
			try {
				CompilationUnit joinFileParsed = JavaParser.parse(new File(joinClassFilePath));
				joinFileParsed
				.findAll(FieldDeclaration.class).stream()
				.forEachOrdered(field->{
					
					field.getAnnotations().stream().forEach(anno ->{
						if(anno.getNameAsString().equals("Key")){
							
							if (anno instanceof SingleMemberAnnotationExpr) {
								SingleMemberAnnotationExpr singleExpression = (SingleMemberAnnotationExpr)anno;
								singleExpression.getMemberValue().toString();
							}
							else if (anno instanceof NormalAnnotationExpr) {
								Map<String, String> keyValueParamMap = new HashMap<>();
								NormalAnnotationExpr annoExpression = (NormalAnnotationExpr)anno;
								for (MemberValuePair param : annoExpression.getPairs()) {
									String value = param.getValue().toString();
									String key = param.getNameAsString();
									keyValueParamMap.put(key, value);
								}
							}
							
							
							
						}
					});
					
					Optional<String> primaryKey = field.getAnnotations().stream().map(AnnotationExpr::getNameAsString)
							.filter(annoName ->annoName.equals("Key")).findAny();
					if(primaryKey.isPresent()){
						primaryKeysOfJoinFile.add(field.getVariable(0).getNameAsString());
					}
				});
			} catch (Exception e) {
			//Cannot Parse the Base File.
		}
	}
		return primaryKeysOfJoinFile;
	}

	private static boolean findUsageOfIndexOfIndex(String baseClassFileName,List<String> branchFiles) {
		AtomicBoolean found=new AtomicBoolean(false);
		Optional<String> baseClassPath = branchFiles.stream().filter(file -> file.endsWith(baseClassFileName)).findAny();
		if(baseClassPath.isPresent()){
			String baseClassFilePath = baseClassPath.get();
			try {
				CompilationUnit baseFileParsed = JavaParser.parse(new File(baseClassFilePath));
				baseFileParsed
				.findAll(ClassOrInterfaceDeclaration.class).stream()
				.forEachOrdered(baseClass->{
					List<String> annotationsOfBaseClass=baseClass.getAnnotations().stream().map(AnnotationExpr::getNameAsString)
							.collect(Collectors.toList());
					if(annotationsOfBaseClass.contains("AutoIndex")){
						found.set(true);
						//Error Index of Index Found
					}
				});
			} catch (Exception e) {
			//Cannot Parse the Base File.
		}
	}
		return found.get();
		
	}
}
