package com.iv.gravity.service.bugs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.enums.BugCategory;
import com.iv.gravity.service.bugfixer.Fix;

public class UnnecessaryClassFieldJava {

   public void findUnnecessaryFields(List<FileUnit> storyFiles, Map<String, List<Fix>> fileAndFixes) {
      storyFiles.stream().forEach(file -> {
         List<Fix> fixes = new ArrayList<>();
         // Storage Maps
         Map<MultiKey, Integer> constantAndLineNumberMap = new HashMap<>();
         Map<String, Integer> referenceFieldsAndLineNumberMap = new HashMap<>();
         // Result Maps
         List<Integer> invalidFieldLineNumbers = new ArrayList<>();
         List<Integer> unnecessaryConstantLineNumbers = new ArrayList<>();
         List<Integer> unnecessaryReferenceFieldLineNumbers = new ArrayList<>();

         if (file.getNatureOfFile().equals("Java")) {
            String fileFullName = file.getFileName();
            String className = fileFullName.replace(".java", "");
            if (!className.toLowerCase().endsWith("constant") && !className.toLowerCase().endsWith("constants")
               && !className.toLowerCase().endsWith("dto") && !className.toLowerCase().endsWith("entity")
               && !className.toLowerCase().endsWith("index") && !className.toLowerCase().endsWith("values")
               && !className.toLowerCase().endsWith("pojo") && !className.toLowerCase().endsWith("applicationcontents")
               && !className.toLowerCase().endsWith("type") && !className.toLowerCase().endsWith("enum") && !className.toLowerCase().endsWith("model")
               && !className.toLowerCase().endsWith("status") && !className.toLowerCase().endsWith("contents")
               && !className.toLowerCase().endsWith("vo")) {
               // Has to Exlude Unnecessary Files.
               try {
                  CompilationUnit compilationUnit = JavaParser.parse(new File(file.getAbsolutePath()));

                  compilationUnit.findAll(FieldDeclaration.class).stream().forEach(field -> {
                     if (field.isPrivate() && field.isFinal()) {
                        if (field.isStatic()) {
                           // CONSTANTS
                           if (!(field.getElementType().isPrimitiveType() || field.getElementType().asString().equals("String")
                              || field.getElementType().asString().equals("TextId") || field.getElementType().asString().equals("ForneusViewId"))) {
                              // INVALID CONSTANTS
                              invalidFieldLineNumbers.add(field.getBegin().get().line);
                           }
                           else {
                              // VALID CONSTANTS
                              MultiKey fieldAndType = new MultiKey(field.getVariable(0).getName().asString(), field.getElementType().asString());
                              constantAndLineNumberMap.put(fieldAndType, field.getBegin().get().line);
                           }
                        }
                        else {
                           // REFERENCE FIELDS
                           if (field.getElementType().asString().equals("String") || field.getElementType().asString().equals("TextId") ||

                              field.getElementType().asString().contains("List<") || field.getElementType().asString().contains("Map<")
                              || field.getElementType().asString().contains("Set<") || field.getElementType().isPrimitiveType()) {
                              // INVALID FIELDS
                              invalidFieldLineNumbers.add(field.getBegin().get().line);
                           }
                           else {
                              // VALID FIELDS
                              referenceFieldsAndLineNumberMap.put(field.getVariable(0).getName().asString(), field.getBegin().get().line);
                           }
                        }
                     }
                     else {
                        // INVALID FIELDS
                        invalidFieldLineNumbers.add(field.getBegin().get().line);
                        fixes.add(Fix.operation("replace").lineNumber(field.getBegin().get().line)
                           .text(field.getModifiers().stream().map(Modifier::asString).collect(Collectors.joining(" ")))
                           .replacementText("private final"));
                     }
                  });

                  // CONSTANT USAGE
                  constantAndLineNumberMap.entrySet().stream().forEach(constant -> {
                     String theConstant = (String) constant.getKey().getKey(0);
                     // Since some constants used are missed
                     // Pattern searchConstant = Pattern
                     // .compile("\\b"
                     // + theConstant
                     // + "\\b");
                     Pattern searchConstant = Pattern.compile(theConstant);
                     AtomicInteger usageCount = new AtomicInteger(0);

                     compilationUnit.findAll(FieldDeclaration.class).stream().forEach(field -> {
                        Optional<Expression> fieldValue = field.getVariable(0).getInitializer();
                        if (fieldValue.isPresent()) {
                           Matcher matchFields = searchConstant.matcher(fieldValue.get().toString());
                           while (matchFields.find()) {
                              usageCount.incrementAndGet();
                           }
                        }
                     });

                     compilationUnit.findAll(MethodDeclaration.class).stream().forEach(method -> {
                        Optional<BlockStmt> methodContents = method.getBody();
                        if (methodContents.isPresent()) {
                           Matcher matchMethodContents = searchConstant.matcher(methodContents.get().toString());
                           while (matchMethodContents.find()) {
                              usageCount.incrementAndGet();
                           }

                           List<Comment> commentsInsideMethod = methodContents.get().getAllContainedComments();
                           if (CollectionUtils.isNotEmpty(commentsInsideMethod)) {
                              commentsInsideMethod.stream().forEach(comment -> {
                                 Matcher matchInComments = searchConstant.matcher(comment.getContent());
                                 while (matchInComments.find()) {
                                    usageCount.decrementAndGet();
                                 }
                              });
                           }
                        }

                     });
                     String typeOfConstant = constant.getKey().getKey(1).toString();
                     if (typeOfConstant.equals("String")) {
                        if (usageCount.get() < 2) {
                           unnecessaryConstantLineNumbers.add(constant.getValue());
                        }
                     }
                     else {
                        if (usageCount.get() == 0) {
                           unnecessaryConstantLineNumbers.add(constant.getValue());
                           fixes.add(Fix.operation("delete").lineNumber(constant.getValue()));
                        }
                     }

                  });

                  // UNUSED FIELD USAGE
                  referenceFieldsAndLineNumberMap.entrySet().stream().forEach(referenceField -> {

                     String theField = referenceField.getKey();
                     // Pattern searchField = Pattern.compile("\\b"
                     // + theField
                     // + "."
                     // + "\\b");
                     Pattern searchField = Pattern.compile(theField + ".");
                     AtomicInteger usageCount = new AtomicInteger(0);

                     compilationUnit.findAll(MethodDeclaration.class).stream().forEach(method -> {
                        Optional<BlockStmt> methodContents = method.getBody();
                        if (methodContents.isPresent()) {
                           Matcher matchMethodContents = searchField.matcher(methodContents.get().toString());
                           while (matchMethodContents.find()) {
                              usageCount.incrementAndGet();
                           }

                           List<Comment> commentsInsideMethod = methodContents.get().getAllContainedComments();
                           if (CollectionUtils.isNotEmpty(commentsInsideMethod)) {
                              commentsInsideMethod.stream().forEach(comment -> {
                                 Matcher matchInComments = searchField.matcher(comment.getContent());
                                 while (matchInComments.find()) {
                                    usageCount.decrementAndGet();
                                 }
                              });
                           }
                        }

                     });

                     if (usageCount.get() == 0) {
                        unnecessaryReferenceFieldLineNumbers.add(referenceField.getValue());
                        fixes.add(Fix.operation("delete").lineNumber(referenceField.getValue()));
                     }
                  });
                  List<BugDetails> bugList = new ArrayList<>();
                  for (Integer invalidFieldLineNumber : invalidFieldLineNumbers) {
                     BugDetails invalidField = new BugDetails();
                     invalidField.setLineNumber(String.valueOf(invalidFieldLineNumber));
                     invalidField.setSeverityOfBug("Critical");
                     invalidField.setBugCategory(BugCategory.INVALID_CLASS_FIELD.toString());
                     invalidField.setBug("These are Invalid Fields. Fields Should be Only private final" + " or private static final");
                     invalidField.setFileName(file.getFileName());
                     invalidField.setRemotePathOfFile(file.getRemotePath());
                     bugList.add(invalidField);

                  }
                  for (Integer unnecessaryConstantLineNumber : unnecessaryConstantLineNumbers) {
                     BugDetails unnecessaryConstant = new BugDetails();
                     unnecessaryConstant.setLineNumber(String.valueOf(unnecessaryConstantLineNumber));
                     unnecessaryConstant.setSeverityOfBug(BugCategory.UNNECESSARY_CODE_ISSUE.getSeverity().toString());
                     unnecessaryConstant.setBugCategory(BugCategory.UNNECESSARY_CODE_ISSUE.toString());
                     unnecessaryConstant.setBug("These Java Constants are Unnecessary");
                     unnecessaryConstant.setFileName(file.getFileName());
                     unnecessaryConstant.setRemotePathOfFile(file.getRemotePath());
                     bugList.add(unnecessaryConstant);
                  }
                  for (Integer unnecessaryFieldLineNumber : unnecessaryReferenceFieldLineNumbers) {
                     BugDetails unnecessaryField = new BugDetails();
                     unnecessaryField.setLineNumber(String.valueOf(unnecessaryFieldLineNumber));
                     unnecessaryField.setSeverityOfBug(BugCategory.UNNECESSARY_CODE_ISSUE.getSeverity().toString());
                     unnecessaryField.setBugCategory(BugCategory.UNNECESSARY_CODE_ISSUE.toString());
                     unnecessaryField.setBug("These References are not used.");
                     unnecessaryField.setFileName(file.getFileName());
                     unnecessaryField.setRemotePathOfFile(file.getRemotePath());
                     bugList.add(unnecessaryField);
                  }
                  if (CollectionUtils.isNotEmpty(bugList)) {

                     file.getBugDetailsList().addAll(bugList);
                  }
               }
               catch (FileNotFoundException e) {
                  // Cannot find the file in the specified absolute path.
               }
            }
         }
         fileAndFixes.put(file.getAbsolutePath(), fixes);
      });
   }

}
