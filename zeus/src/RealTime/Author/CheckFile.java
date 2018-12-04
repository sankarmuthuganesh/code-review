package RealTime.Author;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;







import org.apache.commons.collections4.CollectionUtils;







import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;




public class CheckFile {

	public static void main(String[] args) throws ParseException, IOException {
		
		CompilationUnit compilationUnit=JavaParser.parse(new File("C:\\HUE\\WorkSpace\\Develop\\internal-tool-sync\\Zeus\\src\\RealTime\\CheckTrial\\JvCapitalWebApiServiceImpl.java"));
		 //compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream().forEach(method -> {
			// System.out.println(method.getImplementedTypes());
//
//             Optional<BlockStmt> methodContents = method.getBody();
//             if (methodContents.isPresent()) {
//            	 PrettyPrinterConfiguration kk=new PrettyPrinterConfiguration();
//        		 kk.setPrintComments(false);
//            	  String oo = methodContents.get().toString(kk);
//            	System.out.println(oo);
//                 List<Comment> commentsInsideMethod = methodContents.get().getAllContainedComments();
//                // System.out.println(commentsInsideMethod);
//             }

     //   });
		// new CheckFile().new MethodCheck().visit(compilationUnit, null);
		 System.out.println(compilationUnit.getPackageDeclaration().get().getNameAsString());
	}
	
//	 private class MethodCheck extends VoidVisitorAdapter {
//	        @Override
//	        public void visit(ImportDeclaration declaration, Object path) {
//	
//	        	System.out.println(declaration.getName().getIdentifier());
//	        }
//	 }
	
}
