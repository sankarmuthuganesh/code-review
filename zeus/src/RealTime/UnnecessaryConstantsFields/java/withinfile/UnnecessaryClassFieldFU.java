package RealTime.UnnecessaryConstantsFields.java.withinfile;

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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;

import RealTime.Entity.BugDetails;
import RealTime.Entity.FileUnit;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class UnnecessaryClassFieldFU {
    public List<FileUnit> findUnnecessaryFields(
            List<FileUnit> storyFiles) {
        return storyFiles
                .stream()
                .map(file -> {
                    // Storage Maps
                    Map<MultiKey, Integer> constantAndLineNumberMap = new HashMap<>();
                    Map<String, Integer> referenceFieldsAndLineNumberMap = new HashMap<>();
                    // Result Maps
                    List<Integer> invalidFieldLineNumbers = new ArrayList<>();
                    List<Integer> unnecessaryConstantLineNumbers = new ArrayList<>();
                    List<Integer> unnecessaryReferenceFieldLineNumbers = new ArrayList<>();

                    File fileToCheck = new File(file.getAbsolutePath());
                    if (fileToCheck.getName().endsWith(".java")) {
                        if (fileToCheck.getName().toLowerCase().contains("controller")
                                || fileToCheck.getName().toLowerCase().contains("impl")) {
                            String exactFileName = fileToCheck.getName().replace(".java",
                                    StringUtils.EMPTY);
                            // Has to Exlude Unnecessary Files.
                            try {
                                CompilationUnit compilationUnit = JavaParser.parse(fileToCheck);

                                compilationUnit
                                        .findAll(FieldDeclaration.class)
                                        .stream()
                                        .forEach(field -> {
                                            if (field.isPrivate() && field.isFinal()) {
                                                if (field.isStatic()) {
                                                    // CONSTANTS
                                                if (!(field.getElementType().isPrimitiveType()
                                                        || field.getElementType().asString()
                                                                .equals("String")
                                                        || field.getElementType().asString()
                                                                .equals("TextId")
                                                        || field.getElementType().asString()
                                                        .equals("ForneusViewId"))) {
                                                    // INVALID CONSTANTS
                                                    invalidFieldLineNumbers.add(field
                                                            .getBegin().get().line);
                                                } else {
                                                    // VALID CONSTANTS
                                                    MultiKey fieldAndType = new MultiKey(field
                                                            .getVariable(0).getName()
                                                            .asString()
                                                            , field.getElementType().asString());
                                                    constantAndLineNumberMap.put(fieldAndType,
                                                            field
                                                                    .getBegin()
                                                                    .get().line);
                                                }
                                            } else {
                                                // REFERENCE FIELDS
                                                if (field.getElementType().asString()
                                                        .equals("String")
                                                        || field.getElementType().asString()
                                                                .equals("TextId")
                                                        ||

                                                        field.getElementType().asString()
                                                                .contains("List<")
                                                        ||
                                                        field.getElementType().asString()
                                                                .contains("Map<")
                                                        ||
                                                        field.getElementType().asString()
                                                                .contains("Set<")
                                                        ||
                                                        field.getElementType()
                                                                .isPrimitiveType()) {
                                                    // INVALID FIELDS
                                                    invalidFieldLineNumbers.add(field
                                                            .getBegin().get().line);
                                                } else {
                                                    // VALID FIELDS
                                                    referenceFieldsAndLineNumberMap.put(field
                                                            .getVariable(0).getName()
                                                            .asString(),
                                                            field
                                                                    .getBegin()
                                                                    .get().line);
                                                }
                                            }
                                        } else {
                                            // INVALID FIELDS
                                            invalidFieldLineNumbers
                                                    .add(field.getBegin().get().line);
                                        }
                                    }   );

                                // CONSTANT USAGE
                                constantAndLineNumberMap
                                        .entrySet()
                                        .stream()
                                        .forEach(
                                                constant -> {
                                                    String theConstant = (String)constant
                                                            .getKey().getKey(0);
                                                    Pattern searchConstant = Pattern
                                                            .compile("\\b"
                                                                    + theConstant
                                                                    + "\\b");
                                                    AtomicInteger usageCount = new AtomicInteger(
                                                            0);

                                                    compilationUnit
                                                            .findAll(FieldDeclaration.class)
                                                            .stream()
                                                            .forEach(
                                                                    field -> {
                                                                        Optional<Expression> fieldValue = field
                                                                                .getVariable(0)
                                                                                .getInitializer();
                                                                        if (fieldValue
                                                                                .isPresent()) {
                                                                            Matcher matchFields = searchConstant
                                                                                    .matcher(fieldValue
                                                                                            .get()
                                                                                            .toString());
                                                                            while (matchFields
                                                                                    .find()) {
                                                                                usageCount
                                                                                        .incrementAndGet();
                                                                            }
                                                                        }
                                                                    });

                                                    compilationUnit
                                                            .findAll(MethodDeclaration.class)
                                                            .stream()
                                                            .forEach(
                                                                    method -> {
                                                                        Optional<BlockStmt> methodContents = method
                                                                                .getBody();
                                                                        if (methodContents
                                                                                .isPresent()) {
                                                                            Matcher matchMethodContents = searchConstant
                                                                                    .matcher(methodContents
                                                                                            .get()
                                                                                            .toString());
                                                                            while (matchMethodContents
                                                                                    .find()) {
                                                                                usageCount
                                                                                        .incrementAndGet();
                                                                            }

                                                                            List<Comment> commentsInsideMethod = methodContents
                                                                                    .get()
                                                                                    .getAllContainedComments();
                                                                            if (CollectionUtils
                                                                                    .isNotEmpty(commentsInsideMethod)) {
                                                                                commentsInsideMethod
                                                                                        .stream()
                                                                                        .forEach(
                                                                                                comment -> {
                                                                                                    Matcher matchInComments = searchConstant
                                                                                                            .matcher(comment
                                                                                                                    .getContent());
                                                                                                    while (matchInComments
                                                                                                            .find()) {
                                                                                                        usageCount
                                                                                                                .decrementAndGet();
                                                                                                    }
                                                                                                });
                                                                            }
                                                                        }

                                                                    });
                                                    String typeOfConstant = constant.getKey()
                                                            .getKey(1).toString();
                                                    if (typeOfConstant.equals("String")) {
                                                        if (usageCount.get() < 2) {
                                                            unnecessaryConstantLineNumbers
                                                                    .add(constant.getValue());
                                                        }
                                                    } else {
                                                        if (usageCount.get() == 0) {
                                                            unnecessaryConstantLineNumbers
                                                                    .add(constant.getValue());
                                                        }
                                                    }

                                                });

                                // UNUSED FIELD USAGE
                                referenceFieldsAndLineNumberMap
                                        .entrySet()
                                        .stream()
                                        .forEach(
                                                referenceField -> {

                                                    String theField = referenceField.getKey();
                                                    Pattern searchField = Pattern.compile("\\b"
                                                            + theField
                                                            + "."
                                                            + "\\b");
                                                    AtomicInteger usageCount = new AtomicInteger(
                                                            0);

                                                    compilationUnit
                                                            .findAll(MethodDeclaration.class)
                                                            .stream()
                                                            .forEach(
                                                                    method -> {
                                                                        Optional<BlockStmt> methodContents = method
                                                                                .getBody();
                                                                        if (methodContents
                                                                                .isPresent()) {
                                                                            Matcher matchMethodContents = searchField
                                                                                    .matcher(methodContents
                                                                                            .get()
                                                                                            .toString());
                                                                            while (matchMethodContents
                                                                                    .find()) {
                                                                                usageCount
                                                                                        .incrementAndGet();
                                                                            }

                                                                            List<Comment> commentsInsideMethod = methodContents
                                                                                    .get()
                                                                                    .getAllContainedComments();
                                                                            if (CollectionUtils
                                                                                    .isNotEmpty(commentsInsideMethod)) {
                                                                                commentsInsideMethod
                                                                                        .stream()
                                                                                        .forEach(
                                                                                                comment -> {
                                                                                                    Matcher matchInComments = searchField
                                                                                                            .matcher(comment
                                                                                                                    .getContent());
                                                                                                    while (matchInComments
                                                                                                            .find()) {
                                                                                                        usageCount
                                                                                                                .decrementAndGet();
                                                                                                    }
                                                                                                });
                                                                            }
                                                                        }

                                                                    });

                                                    if (usageCount.get() == 0) {
                                                        unnecessaryReferenceFieldLineNumbers
                                                                .add(referenceField.getValue());
                                                    }
                                                });
                                List<BugDetails> bugList = new ArrayList<>();
                                for (Integer invalidFieldLineNumber : invalidFieldLineNumbers) {
                                    BugDetails invalidField = new BugDetails();
                                    invalidField.setLineNumber(String.valueOf(invalidFieldLineNumber));
                                    invalidField.setSeverityOfBug("Error");
                                    invalidField.setBugCategory("InvalidClassFields");
                                    invalidField.setBug("These are Invalid Fields. Fields Should be Only private final"
                                            + "or private static final");
                                    bugList.add(invalidField);
                                }
                                for (Integer unnecessaryConstantLineNumber : unnecessaryConstantLineNumbers) {
                                    BugDetails unnecessaryConstant = new BugDetails();
                                    unnecessaryConstant.setLineNumber(String.valueOf(unnecessaryConstantLineNumber));
                                    unnecessaryConstant.setSeverityOfBug("Warning");
                                    unnecessaryConstant.setBugCategory("UnnecessaryConstants");
                                    unnecessaryConstant.setBug("These Java Constants are Unnecessary");
                                    bugList.add(unnecessaryConstant);
                                }
                                for (Integer unnecessaryFieldLineNumber : unnecessaryReferenceFieldLineNumbers) {
                                    BugDetails unnecessaryField = new BugDetails();
                                    unnecessaryField.setLineNumber(String.valueOf(unnecessaryFieldLineNumber));
                                    unnecessaryField.setSeverityOfBug("Warning");
                                    unnecessaryField.setBugCategory("UnnecessaryConstants");
                                    unnecessaryField.setBug("These References are not used.");
                                    bugList.add(unnecessaryField);
                                }
                                if (CollectionUtils.isNotEmpty(bugList)) {
                                    file.getBugDetailsList().addAll(bugList);
                                }
                            } catch (FileNotFoundException e) {
                                // Cannot find the file in the specified absolute path.
                            }
                        }
                    }
                    return file;
                }).collect(Collectors.toList());
    }
}
