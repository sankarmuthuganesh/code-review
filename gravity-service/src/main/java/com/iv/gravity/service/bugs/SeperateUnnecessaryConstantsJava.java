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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.enums.BugCategory;
import com.iv.gravity.service.bugfixer.Fix;

public class SeperateUnnecessaryConstantsJava {

   public void getUnnecessaryConstants(List<FileUnit> storyFiles, Map<String, List<Fix>> fileAndFixes) {
      storyFiles.stream()
         .filter(epicFiles -> epicFiles.getFileName().endsWith(".java")
            && (epicFiles.getFileName().toLowerCase().endsWith("constants") || epicFiles.getFileName().toLowerCase().endsWith("constant")))
         .forEach(constantFile -> {
            List<Fix> fixes = new ArrayList<>();
            // Storage Maps
            Map<MultiKey, Integer> constantAndLineNumberMap = new HashMap<>();
            // Result Maps
            List<Integer> invalidFieldLineNumbers = new ArrayList<>();
            List<Integer> unnecessaryConstantLineNumbers = new ArrayList<>();
            try {
               CompilationUnit compilationUnit = JavaParser.parse(new File(constantFile.getAbsolutePath()));

               for (FieldDeclaration field : compilationUnit.findAll(FieldDeclaration.class)) {

                  if (field.isPublic() && field.isStatic() && field.isFinal()) {
                     // CONSTANTS
                     if (!(field.getElementType().isPrimitiveType() || field.getElementType().asString().equals("String")
                        || field.getElementType().asString().equals("TextId") || field.getElementType().asString().equals("ForneusViewId"))) {
                        // INVALID CONSTANTS
                        invalidFieldLineNumbers.add(field.getBegin().get().line);
                     }
                     else {
                        // VALID CONSTANTS
                        MultiKey fieldAndType = new MultiKey(
                           constantFile.getFileName().replace(".java", StringUtils.EMPTY) + "." + field.getVariable(0).getName().asString(),
                           field.getElementType().asString());
                        constantAndLineNumberMap.put(fieldAndType, field.getBegin().get().line);
                     }

                     // CONSTANT USAGE
                     constantAndLineNumberMap.entrySet().stream().forEach(constant -> {
                        String theConstant = (String) constant.getKey().getKey(0);
                        // Pattern searchConstant = Pattern
                        // .compile("\\b"
                        // + theConstant
                        // + "\\b");
                        Pattern searchConstant = Pattern.compile(theConstant);
                        AtomicInteger usageCount = new AtomicInteger(0);

                        storyFiles.stream().forEach(file -> {
                           if (file.getNatureOfFile().equals("Java")
                              && !(file.getFileName().toLowerCase().endsWith("constants") || file.getFileName().toLowerCase().endsWith("constant"))) {

                              CompilationUnit otherFiles;
                              try {
                                 otherFiles = JavaParser.parse(new File(file.getAbsolutePath()));
                                 otherFiles.findAll(FieldDeclaration.class).stream().forEach(otherFileField -> {
                                    Optional<Expression> fieldValue = otherFileField.getVariable(0).getInitializer();
                                    if (fieldValue.isPresent()) {
                                       Matcher matchFields = searchConstant.matcher(fieldValue.get().toString());
                                       while (matchFields.find()) {
                                          usageCount.incrementAndGet();
                                       }
                                    }
                                 });

                                 otherFiles.findAll(MethodDeclaration.class).stream().forEach(otherFileMethod -> {
                                    Optional<BlockStmt> methodContents = otherFileMethod.getBody();
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

                              }
                              catch (Exception e) {
                                 // Cannot Parse Other Epic Files
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

                  }
                  else {
                     // INVALID CONSTANTS
                     invalidFieldLineNumbers.add(field.getBegin().get().line);
                  }
               }
               List<BugDetails> bugList = new ArrayList<>();
               for (Integer invalidFieldLineNumber : invalidFieldLineNumbers) {
                  BugDetails invalidField = new BugDetails();
                  invalidField.setLineNumber(String.valueOf(invalidFieldLineNumber));
                  invalidField.setSeverityOfBug(BugCategory.INVALID_CLASS_FIELD.getSeverity().toString());
                  invalidField.setBugCategory(BugCategory.INVALID_CLASS_FIELD.toString());
                  invalidField.setBug("These are Invalid Constants");
                  invalidField.setFileName(constantFile.getFileName());
                  invalidField.setRemotePathOfFile(constantFile.getRemotePath());
                  bugList.add(invalidField);
               }
               for (Integer unnecessaryConstantLineNumber : unnecessaryConstantLineNumbers) {
                  BugDetails unnecessaryConstant = new BugDetails();
                  unnecessaryConstant.setLineNumber(String.valueOf(unnecessaryConstantLineNumber));
                  unnecessaryConstant.setSeverityOfBug(BugCategory.UNNECESSARY_CODE_ISSUE.getSeverity().toString());
                  unnecessaryConstant.setBugCategory(BugCategory.UNNECESSARY_CODE_ISSUE.toString());
                  unnecessaryConstant.setBug("These Java Constants are not used");
                  unnecessaryConstant.setFileName(constantFile.getFileName());
                  unnecessaryConstant.setRemotePathOfFile(constantFile.getRemotePath());
                  bugList.add(unnecessaryConstant);
               }
               if (CollectionUtils.isNotEmpty(bugList)) {
                  constantFile.getBugDetailsList().addAll(bugList);
               }
            }
            catch (FileNotFoundException e) {
               // Cannot find the file in the specified absolute path.
            }
            fileAndFixes.put(constantFile.getAbsolutePath(), fixes);
         });

   }

}
