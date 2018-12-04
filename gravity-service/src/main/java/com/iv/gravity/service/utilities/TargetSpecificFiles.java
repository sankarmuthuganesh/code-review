package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.collect.Lists;

public class TargetSpecificFiles {

   public Set<String> getAllDomainFiles(List<String> allFilePaths, String... targetsMatch) {
      Set<String> dependentFilePaths = new HashSet<>();
      StringBuilder targetPath = new StringBuilder();
      targetPath.append(File.separator);
      Lists.newArrayList(targetsMatch).forEach(target -> {
         if (!target.isEmpty()) {
            targetPath.append(target);
            targetPath.append(File.separator);
         }
      });
      allFilePaths.stream().filter(file -> {
         File diskFile = new File(file);
         // Make a regex pattern in future to exclude lang dependent files.
         return file.contains(targetPath.toString()) && !(diskFile.isHidden()) && !(diskFile.getName().endsWith(".en.xml"))
            && !(diskFile.getName().endsWith(".ja.xml")) && !(diskFile.getName().startsWith("."));
      }).forEach(file -> {
         dependentFilePaths.add(file);
      });
      if (dependentFilePaths.isEmpty()) {
         return null;
      }
      else {
         List<String> domainJavaPaths = dependentFilePaths.stream().filter(filePath -> filePath.endsWith(".java")).collect(Collectors.toList());
         getJavaTarget(allFilePaths, domainJavaPaths, dependentFilePaths);
         return dependentFilePaths;
      }
   }

   private void getJavaTarget(List<String> allFilePaths, List<String> domainJavaPaths, Set<String> dependentFilePaths) {
      domainJavaPaths.stream().forEach(file -> {
         try {
            getDependentFiles(file, allFilePaths, dependentFilePaths);
         }
         catch (Exception e) {
            // Ignore file if its dependents cannot be manipulated
         }
      });
      try {
         getImplementations(allFilePaths, domainJavaPaths, dependentFilePaths);
      }
      catch (Exception e) {
         // Ignore file if its implementations cannot be manipulated
      }
   }

   private void getDependentFiles(String file, List<String> allFilePaths, Set<String> dependentFilePaths) throws FileNotFoundException {
      CompilationUnit compilationUnit = JavaParser.parse(new File(file));
      compilationUnit.findAll(ImportDeclaration.class).stream().forEach(importInFile -> {
         String importedClass = importInFile.getName().getIdentifier();
         String importedJava = importedClass + ".java";
         Optional<String> importedFilePath = allFilePaths.stream().filter(filePath -> new File(filePath).getName().equals(importedJava)).findFirst();
         if (importedFilePath.isPresent()) {
            if (!dependentFilePaths.contains(importedFilePath.get())) {
               dependentFilePaths.add(importedFilePath.get());
               try {
                  getDependentFiles(importedFilePath.get(), allFilePaths, dependentFilePaths);
               }
               catch (FileNotFoundException e) {

               }
            }
         }
      });
   }

   private void getImplementations(List<String> allFilePaths, List<String> domainJavaPaths, Set<String> dependentFilePaths) {
      Set<String> interfaceClasses = domainJavaPaths.stream().filter(filePath -> {
         CompilationUnit parsedFile;
         try {
            parsedFile = JavaParser.parse(new File(filePath));
            return parsedFile.findAll(ClassOrInterfaceDeclaration.class).stream().filter(declaration -> declaration.isInterface()).findAny()
               .isPresent();
         }
         catch (FileNotFoundException e) {
            return false;
         }
      }).map(filePath -> new File(filePath).getName().split("\\.")[0]).collect(Collectors.toSet());

      if (CollectionUtils.isNotEmpty(interfaceClasses)) {
         allFilePaths.stream().filter(filePath -> filePath.endsWith(".java")).forEach(filePath -> {
            try {
               // Since mostly in the first class declration itself implementing classes are defined (as
               // convention)
               // limit is set to one
               JavaParser.parse(new File(filePath)).findAll(ClassOrInterfaceDeclaration.class).stream().limit(1).forEach(type -> {
                  if (!type.getImplementedTypes().isEmpty()) {
                     List<String> implementedClasses = type.getImplementedTypes().stream().map(ClassOrInterfaceType::getNameAsString)
                        .collect(Collectors.toList());
                     if (CollectionUtils.containsAny(interfaceClasses, implementedClasses)) {
                        dependentFilePaths.add(filePath);
                        try {
                           getDependentFiles(filePath, allFilePaths, dependentFilePaths);
                        }
                        catch (FileNotFoundException e) {

                        }
                     }
                  }
               });
            }
            catch (FileNotFoundException e) {

            }
         });
      }
   }
}
