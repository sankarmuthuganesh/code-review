package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;

public class FindOtherEpicRelatedFiles {

   public Map<String, Set<String>> findDependentFiles(List<String> epicFilePaths) {

      // Repository and Other Dependent Files.
      Map<String, Set<String>> repositoryAndDependentFiles = new HashMap<>();
      // Collects All Epic Java File Names in this String.
      List<String> epicFileList = epicFilePaths.stream().map(epicFile -> {
         File file = new File(epicFile);
         return file.getName().split("\\.")[0];
      }).collect(Collectors.toList());

      epicFilePaths.stream().forEach(file -> {
         CompilationUnit compilationUnit;
         try {
            if (file.endsWith(".java")) {
               compilationUnit = JavaParser.parse(new File(file));
               Optional<PackageDeclaration> packages = compilationUnit.getPackageDeclaration();
               if (packages.isPresent()) {
                  String[] packageValue = packages.get().getNameAsString().split("\\.");
                  compilationUnit.findAll(ImportDeclaration.class).stream().forEach(importInFile -> {
                     String importedClass = importInFile.getName().getIdentifier();
                     // check for usage of reflection by checking imported
                     // classes.
                     String fullImportName = importInFile.getNameAsString();
                     String[] importPathArray = fullImportName.split("\\.");

                     List<String> importPathList = Arrays.asList(importPathArray);

                     if (fullImportName.contains("com.worksap.company") && !importPathArray[3].contains("framework")
                        && !fullImportName.contains("ivtl")) {

                        String key = "hue-".concat(importPathArray[4].trim()).concat("-").concat(importPathArray[6].trim());

                        if (repositoryAndDependentFiles.containsKey(key)) {
                           Set<String> intermediateSet = repositoryAndDependentFiles.get(key);
                           intermediateSet.add(importedClass);
                           repositoryAndDependentFiles.put(key, intermediateSet);
                        }
                        else {
                           Set<String> filesSet = new HashSet<>();
                           filesSet.add(importedClass);
                           repositoryAndDependentFiles.put(key, filesSet);
                        }
                     }
                  });
               }
            }
         }
         catch (FileNotFoundException | IndexOutOfBoundsException e) {
            // File Not Found
         }
      });
      repositoryAndDependentFiles.entrySet().stream()
         .forEach(repoDependentMap -> repoDependentMap.getValue().removeIf(dependentFile -> epicFileList.contains(dependentFile)));
      return repositoryAndDependentFiles;
   }

}
