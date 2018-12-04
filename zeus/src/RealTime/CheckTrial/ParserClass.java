package RealTime.CheckTrial;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ParserClass {
public static void main(String[] args) throws FileNotFoundException {
	CompilationUnit compi = JavaParser.parse(new File("C:\\Gravity\\Clones\\Temp\\hue-ac-asset\\river\\hue-ac-asset-biz\\src\\main\\java\\com\\worksap\\company\\hue\\ac\\biz\\asset\\service\\fixedasset\\assetclosingmanagement\\portal\\AssetClosingPortalBatchServiceImpl.java"));
	new MethodVariableVisitor().visit(compi, null);
}
}
class MethodVariableVisitor extends VoidVisitorAdapter<Object> {
    @Override
    public void visit(MethodDeclaration method, Object path) {
    	if(method.getBody().get().toString().contains("System.out.println")){
    		System.out.println(method.getNameAsString() + " contains sysout. Avoid This.");
    	}
    	
    }
}