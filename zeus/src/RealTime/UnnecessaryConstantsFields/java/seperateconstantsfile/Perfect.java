package RealTime.UnnecessaryConstantsFields.java.seperateconstantsfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Perfect {
    public static void main(String args[]) {
        List<String> branchFiles = new ArrayList<>();
        File fileToCheck = new File(
                "C:\\HUE\\WorkSpace\\Develop\\hue-hr-nenchomgmt\\hue-hr-nenchomgmt-biz\\src\\main\\java\\com\\worksap\\company\\hue\\hr\\biz\\nenchomgmt\\service\\gravity\\CheckFileOne.java");
        if (fileToCheck.getName().endsWith(".java")) {
            String exactFileName = fileToCheck.getName().replace(".java", StringUtils.EMPTY);
            try {
                CompilationUnit compilationUnit = JavaParser.parse(fileToCheck);

                compilationUnit.getPackageDeclaration().get().stream().forEach(ss -> {
                    System.out.println(ss);
                    System.out.println("-----------------");
                });

                if (exactFileName.endsWith("Constants")) {
                    String constantFilePackageDeclaration = compilationUnit.getPackageDeclaration().get()
                            .getNameAsString();

                    List<String> filesInTheSamePackageAsConstantFile = new ArrayList<>();
                    branchFiles.stream().forEach(fileInBranch -> {

                        if (fileToCheck.getName().endsWith(".java")) {
                            CompilationUnit otherFilesCompiled = JavaParser.parse(fileInBranch);

                        }

                    });
                }
            } catch (FileNotFoundException e) {
                // Cannot find the file in the specified absolute path.
            }
        }
    }
}
