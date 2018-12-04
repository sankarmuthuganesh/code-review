package com.iv.gravity.service.bugs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
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
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.PrimaryKeysCollector;
import com.iv.gravity.enums.BugCategory;

// Only Method calls that are terminated...
public class IndexError {

   public Map<String, Map<MultiKey, List<String>>> getJoinErrorFiles(List<FileUnit> storyFileUnits, List<String> branchFiles) {
      Map<String, Map<MultiKey, List<String>>> groupedFilesTotalFindings = new LinkedHashMap<>();

      storyFileUnits.stream().forEach(epicFile -> {
         File theFileToSearch = new File(epicFile.getAbsolutePath());
         if (theFileToSearch.getName().endsWith("Index.java") || theFileToSearch.getName().endsWith("Dto.java")) {
            List<BugDetails> bugList = new ArrayList<>();
            CompilationUnit parsedFile;
            try {

               parsedFile = JavaParser.parse(theFileToSearch);
               parsedFile.findAll(ClassOrInterfaceDeclaration.class).stream().forEachOrdered(classOrInterface -> {

                  List<String> annotationsOfCurrentClass = classOrInterface.getAnnotations().stream().map(AnnotationExpr::getNameAsString)
                     .collect(Collectors.toList());

                  for (AnnotationExpr annotation : classOrInterface.getAnnotations()) {
                     if (annotation instanceof SingleMemberAnnotationExpr) {
                        if (annotation.getNameAsString().equals("AutoIndex")) {
                           SingleMemberAnnotationExpr singleExpression = (SingleMemberAnnotationExpr) annotation;
                           String baseClassFileName = singleExpression.getMemberValue().toString().trim().replace(".class", ".java");
                           boolean found = findUsageOfIndexOfIndex(baseClassFileName, branchFiles);
                           if (found) {
                              BugDetails invalidField = new BugDetails();
                              invalidField.setLineNumber(String.valueOf(annotation.getBegin().get().line));
                              invalidField.setSeverityOfBug(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.getSeverity().toString());
                              invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                              invalidField.setBug("A Index File Must not Refer to Another Index File");
                              invalidField.setFileName(epicFile.getFileName());
                              invalidField.setRemotePathOfFile(epicFile.getRemotePath());

                              bugList.add(invalidField);
                           }
                        }
                     }
                     else if (annotation instanceof NormalAnnotationExpr) {
                        Map<String, String> keyValueParamMap = new LinkedHashMap<>();
                        NormalAnnotationExpr annoExpression = (NormalAnnotationExpr) annotation;

                        annoExpression.getPairs().stream().forEachOrdered(param -> {

                           String value = param.getValue().toString().trim();
                           String key = param.getNameAsString().trim();
                           keyValueParamMap.put(key, value);

                        });
                        if (annoExpression.getNameAsString().equals("AutoIndex")) {
                           String baseClassFileName = keyValueParamMap.get("value").replace(".class", ".java");
                           boolean found = findUsageOfIndexOfIndex(baseClassFileName, branchFiles);
                           if (found) {
                              BugDetails invalidField = new BugDetails();
                              invalidField.setLineNumber(String.valueOf(annotation.getBegin().get().line));
                              invalidField.setSeverityOfBug(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.getSeverity().toString());
                              invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                              invalidField.setBug("A Index File Must not Refer to Another Index File");
                              invalidField.setFileName(epicFile.getFileName());
                              invalidField.setRemotePathOfFile(epicFile.getRemotePath());
                              bugList.add(invalidField);
                           }
                        }
                        if (annotationsOfCurrentClass.contains("AutoIndex") && annoExpression.getNameAsString().equals("Join")) {
                           String joinClassFileName = keyValueParamMap.get("with").replace(".class", ".java");
                           boolean found = findUsageOfIndexOfIndex(joinClassFileName, branchFiles);
                           if (found) {

                              BugDetails invalidField = new BugDetails();
                              invalidField.setLineNumber(String.valueOf(annotation.getBegin().get().line));
                              invalidField.setSeverityOfBug(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.getSeverity().toString());
                              invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                              invalidField.setBug("A Index File Must not Refer to Another Index File");
                              invalidField.setFileName(epicFile.getFileName());
                              invalidField.setRemotePathOfFile(epicFile.getRemotePath());
                              bugList.add(invalidField);

                           }
                           PrimaryKeysCollector collector = getAllPrimaryKeysOfJoinClass(joinClassFileName, branchFiles);
                           Map<String, List<String>> primaryKeys = collector.getPrimarykeys();
                           Map<String, Integer> primaryKeysAndOrder = collector.getPrimaryKeysAndOrder();
                           List<String> partitionKeys = primaryKeys.get("partitionkeys");
                           List<String> clusteringKeys = primaryKeys.get("clusteringkeys");
                           List<String> priamryKeys = primaryKeys.get("primarykeys");

                           List<String> whereKeys = new ArrayList<>();
                           String[] whereValues = keyValueParamMap.get("where").replaceAll("[[{}\"#]]", StringUtils.EMPTY).split(",");
                           for (String whereValue : whereValues) {
                              String singleParam;
                              String[] keyValue = whereValue.split("==");
                              if (keyValue[0].contains("#")) {
                                 String[] joinVarSplit = keyValue[0].split("\\.");
                                 singleParam = joinVarSplit[1];
                              }
                              else {
                                 String[] joinVarSplit = keyValue[1].split("\\.");
                                 singleParam = joinVarSplit[1];
                              }

                              // int indexOfDot =
                              // whereValue.lastIndexOf(".");
                              // String singleParam[] = whereValue
                              // .split("\\.");
                              // String singleParam =
                              // whereValue.substring(indexOfDot+1);
                              whereKeys.add(singleParam.trim());
                           }
                           // if (!primaryKeys
                           // .equals(whereKeys)) {
                           // listOfFindingsBranch
                           // .add(branch
                           // .getValue()
                           // .get(countOfFiles.get())
                           // + "#L"
                           // + annotation
                           // .getBegin()
                           // .get().line);
                           // }

                           if (whereKeys.containsAll(partitionKeys)) {
                              // if (containsOrderedSublist(whereKeys,
                              // partitionKeys)) {
                              //
                              // } else {
                              //
                              // BugDetails invalidField = new
                              // BugDetails();
                              // invalidField.setLineNumber(String
                              // .valueOf(annotation
                              // .getBegin()
                              // .get().line));
                              // invalidField.setSeverityOfBug("Critical");
                              // invalidField
                              // .setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                              // invalidField
                              // .setBug("Partition Keys of Join File
                              // Are Not In Order");
                              // bugList.add(invalidField);
                              //
                              // }
                           }
                           else {
                              BugDetails invalidField = new BugDetails();
                              invalidField.setLineNumber(String.valueOf(annotation.getBegin().get().line));
                              invalidField.setSeverityOfBug(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.getSeverity().toString());
                              invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                              invalidField.setBug("All Partition Keys of Join File are not present in where");
                              invalidField.setFileName(epicFile.getFileName());
                              invalidField.setRemotePathOfFile(epicFile.getRemotePath());
                              bugList.add(invalidField);
                           }
                           // Common Logic to find keys in order
                           // implemented
                           // below
                           // List<String> clusteringKeysinWhere = new
                           // ArrayList<>();
                           // clusteringKeysinWhere.addAll(whereKeys);
                           // clusteringKeysinWhere.retainAll(clusteringKeys);
                           // if (containsOrderedSublist(clusteringKeys,
                           // clusteringKeysinWhere)) {
                           //
                           // } else {
                           // BugDetails invalidField = new BugDetails();
                           // invalidField.setLineNumber(String
                           // .valueOf(annotation
                           // .getBegin()
                           // .get().line));
                           // invalidField.setSeverityOfBug("Critical");
                           // invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                           // invalidField
                           // .setBug("Clustering Keys of Join File Are
                           // Not In Order");
                           // bugList.add(invalidField);
                           // }
                           if (!CollectionUtils.isSubCollection(whereKeys, priamryKeys)) {
                              BugDetails invalidField = new BugDetails();
                              invalidField.setLineNumber(String.valueOf(annotation.getBegin().get().line));
                              invalidField.setSeverityOfBug(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.getSeverity().toString());
                              invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                              invalidField.setBug("Where param contains some normal fields");
                              invalidField.setFileName(epicFile.getFileName());
                              invalidField.setRemotePathOfFile(epicFile.getRemotePath());
                              bugList.add(invalidField);
                           }
                           else {

                              AtomicBoolean orderWrong = new AtomicBoolean(false);
                              primaryKeysAndOrder.entrySet().stream().forEachOrdered(ko -> {
                                 if (whereKeys.indexOf(ko.getKey()) != ko.getValue() && whereKeys.indexOf(ko.getKey()) != -1) {
                                    orderWrong.set(true);
                                 }
                              });
                              if (orderWrong.get()) {
                                 BugDetails invalidField = new BugDetails();
                                 invalidField.setLineNumber(String.valueOf(annotation.getBegin().get().line));
                                 invalidField.setSeverityOfBug(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.getSeverity().toString());
                                 invalidField.setBugCategory(BugCategory.MISUSE_OF_AUTOINDEX_ISSUE.toString());
                                 invalidField.setBug("Key Orders Of Join Class Are Not Maintained");
                                 invalidField.setFileName(epicFile.getFileName());
                                 invalidField.setRemotePathOfFile(epicFile.getRemotePath());
                                 bugList.add(invalidField);
                              }
                              // if
                              // (!containsOrderedSublist(priamryKeys,
                              // whereKeys)) {}
                           }
                           // Below Code Doesnt Find if Some elements are
                           // present in between
                           // if(Collections.indexOfSubList(whereKeys,
                           // partitionKeys)==-1){
                           // listOfFindingsBranch
                           // .add(branch
                           // .getValue()
                           // .get(countOfFiles.get())
                           // + "#L"
                           // + annotation
                           // .getBegin()
                           // .get().line);
                           // }

                        }

                     }
                  }
                  // System.out.println(classOrInterface.getAnnotations());
               });
            }
            catch (Exception e) {
            }
            if (CollectionUtils.isNotEmpty(bugList)) {
               epicFile.getBugDetailsList().addAll(bugList);
            }
         }
      });
      return groupedFilesTotalFindings;
   }

   private static PrimaryKeysCollector getAllPrimaryKeysOfJoinClass(String joinClassFileName, List<String> branchFiles) {
      PrimaryKeysCollector collector = new PrimaryKeysCollector();
      Map<String, Integer> primaryKeysAndOrder = new HashMap<>();
      List<String> primaryKeysOfJoinFile = new ArrayList<>();
      Map<String, List<String>> primarykeys = new HashMap<>();
      List<String> partitionKeysOfJoinFile = new ArrayList<>();
      List<String> clusteringKeysOfJoinFile = new ArrayList<>();

      Optional<String> joinClassPath = branchFiles.stream().filter(file -> new File(file).getName().equals(joinClassFileName)).findAny();
      if (joinClassPath.isPresent()) {
         String joinClassFilePath = joinClassPath.get();
         try {
            CompilationUnit joinFileParsed = JavaParser.parse(new File(joinClassFilePath));
            joinFileParsed.findAll(FieldDeclaration.class).stream().forEachOrdered(field -> {

               field.getAnnotations().stream().forEachOrdered(anno -> {
                  if (anno.getNameAsString().equals("Key")) {
                     if (anno instanceof NormalAnnotationExpr) {
                        Map<String, String> keyValueParamMap = new HashMap<>();
                        NormalAnnotationExpr annoExpression = (NormalAnnotationExpr) anno;
                        for (MemberValuePair param : annoExpression.getPairs()) {
                           String value = param.getValue().toString().trim();
                           String key = param.getNameAsString().trim();
                           keyValueParamMap.put(key, value);
                        }
                        boolean isPartitionKey;
                        if (!keyValueParamMap.containsKey("isPartitionKey")) {
                           isPartitionKey = false;
                        }
                        else {
                           isPartitionKey = keyValueParamMap.get("isPartitionKey").equals("true");
                        }
                        int order;
                        if (!keyValueParamMap.containsKey("order")) {
                           order = 0;
                        }
                        else {
                           order = Integer.parseInt(keyValueParamMap.get("order"));
                        }
                        if (isPartitionKey) {
                           partitionKeysOfJoinFile.add(field.getVariable(0).getNameAsString());
                           primaryKeysAndOrder.put(field.getVariable(0).getNameAsString(), order);
                           primaryKeysOfJoinFile.add(field.getVariable(0).getNameAsString());
                        }
                        else {
                           clusteringKeysOfJoinFile.add(field.getVariable(0).getNameAsString());
                           primaryKeysOfJoinFile.add(field.getVariable(0).getNameAsString());
                           primaryKeysAndOrder.put(field.getVariable(0).getNameAsString(), order);
                        }
                     }
                     if (anno instanceof MarkerAnnotationExpr) {
                        int order = 0;
                        boolean isPartitionKey = true;
                        partitionKeysOfJoinFile.add(field.getVariable(0).getNameAsString());
                        primaryKeysAndOrder.put(field.getVariable(0).getNameAsString(), order);
                        primaryKeysOfJoinFile.add(field.getVariable(0).getNameAsString());

                     }
                  }
               });

               primarykeys.put("partitionkeys", partitionKeysOfJoinFile);
               primarykeys.put("clusteringkeys", clusteringKeysOfJoinFile);
               primarykeys.put("primarykeys", primaryKeysOfJoinFile);
               collector.setPrimarykeys(primarykeys);
               collector.setPrimaryKeysAndOrder(primaryKeysAndOrder);
               // Optional<String> primaryKey = field
               // .getAnnotations()
               // .stream()
               // .map(AnnotationExpr::getNameAsString)
               // .filter(annoName -> annoName
               // .equals("Key")).findAny();
               // if (primaryKey.isPresent()) {
               // primaryKeysOfJoinFile.add(field
               // .getVariable(0)
               // .getNameAsString());
               // }
            });
         }
         catch (Exception e) {
            // Cannot Parse the Base File.
         }
      }
      return collector;
   }

   private static boolean findUsageOfIndexOfIndex(String baseClassFileName, List<String> branchFiles) {
      AtomicBoolean found = new AtomicBoolean(false);
      Optional<String> baseClassPath = branchFiles.stream().filter(file -> new File(file).getName().equals(baseClassFileName)).findAny();
      if (baseClassPath.isPresent()) {
         String baseClassFilePath = baseClassPath.get();
         try {
            CompilationUnit baseFileParsed = JavaParser.parse(new File(baseClassFilePath));
            baseFileParsed.findAll(ClassOrInterfaceDeclaration.class).stream().forEachOrdered(baseClass -> {
               List<String> annotationsOfBaseClass = baseClass.getAnnotations().stream().map(AnnotationExpr::getNameAsString)
                  .collect(Collectors.toList());
               if (annotationsOfBaseClass.contains("AutoIndex")) {
                  found.set(true);
                  // Error Index of Index Found
               }
            });
         }
         catch (Exception e) {
            // Cannot Parse the Base File.
         }
      }
      return found.get();
   }

   private static boolean containsOrderedSublist(List<String> list, List<String> sublist) {
      Iterator<String> listIter = list.iterator();
      for (String item : sublist) {
         if (!listIter.hasNext()) {
            // still elements in list but no in sublist
            return false;
         }
         while (listIter.hasNext() && !listIter.next().equals(item)) {
            if (!listIter.hasNext()) {
               return false;
            }
            // do nothing, consume the list until item is found
         }
      }
      // entire sublist found in list
      return true;
   }

}
