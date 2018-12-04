package RealTime.Author;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class JavaAuthor {

	String authorOfFile = "NoAuthor";
    public boolean isCobaltFile = false;

    public String getJavaAuthor(String absolutePathOfFile) {
        
        File theJavaFile = new File(absolutePathOfFile);
        try {
			new ClassOrInterfaceJava().visit(JavaParser.parse(theJavaFile),null);
			 new EnumJava().visit(JavaParser.parse(theJavaFile), null);
		} catch (FileNotFoundException e) {
			// The File Cloned is not in the Specified Location.
		}
       
        return authorOfFile;
    }

    // To Visit a Class or Interface
    class ClassOrInterfaceJava extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration classOrInterface, Object unknownAuthor) {
            // super.visit(classOrInterface, args);
            if (classOrInterface.getComment().isPresent()) {
                String comments = classOrInterface.getComment().get().getContent();
                if (comments.contains("@author")) {
                    String[] splitValues = comments.split("\\r?\\n");
                    for (int i = 0; i < splitValues.length; i++) {
                        if (splitValues[i].contains("@author")) {
                            String finalValue = splitValues[i];
                            String author = finalValue.replace("*", "").replace("@author", "").trim();
                            if (StringUtils.isNotEmpty(author)) {
                                authorOfFile = author;
                            } else {
                                authorOfFile = "NoAuthor";
                            }
                        }
                    }
                }
                if (comments.contains("Cobalt Tool")) {
                    isCobaltFile = true;
                }
            }
            else {
                // No Comment is Given
                authorOfFile = "NoAuthor";
            }
        }
    }

    // To Visit a Enum
    public class EnumJava extends VoidVisitorAdapter {
        @Override
        public void visit(EnumDeclaration Enum, Object unknownAuthor) {
            // super.visit(Enum, args);
            if (Enum.getComment().isPresent()) {
                String comments = Enum.getComment().get().getContent();
                if (comments.contains("@author")) {
                    String[] splitValues = comments.split("\\r?\\n");
                    for (int i = 0; i < splitValues.length; i++) {
                        if (splitValues[i].contains("@author")) {
                            String finalValue = splitValues[i];
                            String author = finalValue.replace("*", "").replace("@author", "").trim();
                            if (StringUtils.isNotEmpty(author)) {
                                authorOfFile = author;
                            } else {
                                authorOfFile = "NoAuthor";
                            }
                        }
                    }
                }
                if (comments.contains("Cobalt Tool")) {
                    isCobaltFile = true;
                }
            }
            else {
                // No Comment is Given
                authorOfFile = "NoAuthor";
            }
        }
    }
}
