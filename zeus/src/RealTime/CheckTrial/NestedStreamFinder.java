package RealTime.CheckTrial;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;

public class NestedStreamFinder {
	public static void main(String[] args) throws FileNotFoundException {
		JavaParser paser = new JavaParser();
		CompilationUnit unit = paser
				.parse(new File(
						"C:/HUE/WorkSpace/Develop/internal-tool-sync/Zeus/src/RealTime/CheckTrial/sampleCheckFile.java"));
		List<String> streamMethods = new ArrayList<String>();
		List<String> daoMethods = new ArrayList<String>();
		
		Set<String> lineNumberSet = new HashSet<>();
		unit
		.findAll(MethodDeclaration.class)
		.stream().forEach(statements->{
			NodeList<Statement> checkValue = statements.getBody().get().getStatements();
			if(checkValue.stream().anyMatch(predicate->predicate.toString().contains(
					"stream(")  || predicate.toString().contains(
							"while(") || predicate.toString().contains(
									"for("))){
				streamMethods.add(statements.getNameAsString());
			}
			 if(checkValue.stream().anyMatch(daoCheck->daoCheck.toString().contains("Dao."))){
				daoMethods.add(statements.getNameAsString());
			}
			
				
		});
		
//		List<String> methods = unit
//				.findAll(MethodDeclaration.class)
//				.stream()
//				.filter(predicate -> predicate
//						.getBody()
//						.get()
//						.getStatements()
//						.stream()
//						.anyMatch(
//								statements -> statements.toString().contains(
//										"stream") || statements.toString().contains(
//												"Dao") || statements.toString().contains(
//														"while") || statements.toString().contains(
//																"for")))
//				.map(mapper -> mapper.getNameAsString())
//				.collect(Collectors.toList());
//		System.out.println(methods);
		unit.findAll(MethodCallExpr.class)
				.stream()
				.filter(predicate -> predicate.toString().contains("stream"))
				.forEach(
						action -> {
							int count = 0;
							Pattern streamCheck = Pattern.compile("stream");
							Matcher m = streamCheck.matcher(action.toString());
							while (m.find()) {
								count++;
							}
							if (count >= 2) {
								lineNumberSet
										.add("nested Stream found in line  "
												+ action.getBegin().get().line);
							}
							streamMethods.stream().forEach(method->{
								if(action.toString().contains(method)&&!action.toString().contains("."+method)){
									
								
									
									
										lineNumberSet
										.add("nested Stream found in line  "
												+ action.getBegin().get().line);
									
								}
								
							});
							daoMethods.stream().forEach(daoCall->{
								if(action.toString().contains(daoCall)){
									lineNumberSet
									.add("db hit inside stream in "
											+ action.getBegin().get().line);
								}
								
							});
						});
		System.out.println(lineNumberSet);

	}
}
