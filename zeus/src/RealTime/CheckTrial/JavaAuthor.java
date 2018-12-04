package RealTime.CheckTrial;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

public class JavaAuthor {

    public static void main(String[] args) {
    	String absolutePathOfFile="C:\\HUE\\WorkSpace\\Develop\\Zeus\\src\\RealTime\\CheckTrial\\CancelOrderController.java";
        String authorOfFile = "NoAuthor";
        // boolean isCobaltFile = false;
        File theJavaFile = new File(absolutePathOfFile);

        try {
            CompilationUnit parsedFile = JavaParser.parse(theJavaFile);

            // Class Author
            for (ClassOrInterfaceDeclaration classOrInterface : parsedFile.findAll(ClassOrInterfaceDeclaration.class)) {
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
                        authorOfFile = "ToolGenerated";
                    }
                }
                else {
                    // No Comment is Given
                    authorOfFile = "NoAuthor";
                }
            }

            // Enum Author
            for (EnumDeclaration Enum : parsedFile.findAll(EnumDeclaration.class)) {
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
                        authorOfFile = "ToolGenerated";
                    }
                }
                else {
                    // No Comment is Given
                    authorOfFile = "NoAuthor";
                }
            }
        } catch (FileNotFoundException e) {
            // The File Cloned is not in the Specified Location.
        }

        // try {
        // new ClassOrInterfaceJava().visit(JavaParser.parse(theJavaFile), null);
        // new EnumJava().visit(JavaParser.parse(theJavaFile), null);
        // } catch (FileNotFoundException e) {
        // // The File Cloned is not in the Specified Location.
        // }

       System.out.println(authorOfFile); 
    }

    // // To Visit a Class or Interface
    // class ClassOrInterfaceJava extends VoidVisitorAdapter {
    // @Override
    // public void visit(ClassOrInterfaceDeclaration classOrInterface, Object unknownAuthor) {
    // // super.visit(classOrInterface, args);
    // if (classOrInterface.getComment().isPresent()) {
    // String comments = classOrInterface.getComment().get().getContent();
    // if (comments.contains("@author")) {
    // String[] splitValues = comments.split("\\r?\\n");
    // for (int i = 0; i < splitValues.length; i++) {
    // if (splitValues[i].contains("@author")) {
    // String finalValue = splitValues[i];
    // String author = finalValue.replace("*", "").replace("@author", "").trim();
    // if (StringUtils.isNotEmpty(author)) {
    // authorOfFile = author;
    // } else {
    // authorOfFile = "NoAuthor";
    // }
    // }
    // }
    // }
    // if (comments.contains("Cobalt Tool")) {
    // isCobaltFile = true;
    // }
    // }
    // else {
    // // No Comment is Given
    // authorOfFile = "NoAuthor";
    // }
    // }
    // }
    //
    // // To Visit a Enum
    // public class EnumJava extends VoidVisitorAdapter {
    // @Override
    // public void visit(EnumDeclaration Enum, Object unknownAuthor) {
    // // super.visit(Enum, args);
    // if (Enum.getComment().isPresent()) {
    // String comments = Enum.getComment().get().getContent();
    // if (comments.contains("@author")) {
    // String[] splitValues = comments.split("\\r?\\n");
    // for (int i = 0; i < splitValues.length; i++) {
    // if (splitValues[i].contains("@author")) {
    // String finalValue = splitValues[i];
    // String author = finalValue.replace("*", "").replace("@author", "").trim();
    // if (StringUtils.isNotEmpty(author)) {
    // authorOfFile = author;
    // } else {
    // authorOfFile = "NoAuthor";
    // }
    // }
    // }
    // }
    // if (comments.contains("Cobalt Tool")) {
    // isCobaltFile = true;
    // }
    // }
    // else {
    // // No Comment is Given
    // authorOfFile = "NoAuthor";
    // }
    // }
    // }
}
