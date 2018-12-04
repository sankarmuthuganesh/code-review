package RealTime.UnnecessaryConstantsFields.java.seperateconstantsfile;

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

public class UnnecessaryConstantsFU {
    public List<FileUnit> getUnnecessaryConstants(List<FileUnit> storyFiles) {
        return storyFiles
                .stream()
                .filter(epicFiles -> epicFiles.getFileName().endsWith(".java")
                        && epicFiles.getFileName().contains("Constants"))
                .map(constantFile -> {
                    // Storage Maps
                    Map<MultiKey, Integer> constantAndLineNumberMap = new HashMap<>();
                    // Result Maps
                    List<Integer> invalidFieldLineNumbers = new ArrayList<>();
                    List<Integer> unnecessaryConstantLineNumbers = new ArrayList<>();
                    try {
                        CompilationUnit compilationUnit = JavaParser.parse(new File(constantFile.getAbsolutePath()));

                        for (FieldDeclaration field : compilationUnit
                                .findAll(FieldDeclaration.class)) {

                            if (field.isPrivate() && field.isStatic() && field.isFinal()) {
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
                                    MultiKey fieldAndType = new MultiKey(constantFile.getFileName().replace(
                                            ".java", StringUtils.EMPTY)
                                            + "."
                                            + field
                                                    .getVariable(0).getName()
                                                    .asString()
                                            , field.getElementType().asString());
                                    constantAndLineNumberMap.put(fieldAndType,
                                            field
                                                    .getBegin()
                                                    .get().line);
                                }

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

                                                    storyFiles
                                                            .stream()
                                                            .forEach(
                                                                    file -> {
                                                                        if (file.getNatureOfFile().equals(
                                                                                "Java")
                                                                                && !file.getFileName()
                                                                                        .contains(
                                                                                                "Constants")) {

                                                                            CompilationUnit otherFiles;
                                                                            try {
                                                                                otherFiles = JavaParser
                                                                                        .parse(new File(
                                                                                                file
                                                                                                        .getAbsolutePath()));
                                                                                otherFiles
                                                                                        .findAll(
                                                                                                FieldDeclaration.class)
                                                                                        .stream()
                                                                                        .forEach(
                                                                                                otherFileField -> {
                                                                                                    Optional<Expression> fieldValue = otherFileField
                                                                                                            .getVariable(
                                                                                                                    0)
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

                                                                                otherFiles
                                                                                        .findAll(
                                                                                                MethodDeclaration.class)
                                                                                        .stream()
                                                                                        .forEach(
                                                                                                otherFileMethod -> {
                                                                                                    Optional<BlockStmt> methodContents = otherFileMethod
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

                                                                            } catch (Exception e) {
                                                                                // Cannot Parse Other Epic Files
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

                            } else {
                                // INVALID CONSTANTS
                                invalidFieldLineNumbers
                                        .add(field.getBegin().get().line);
                            }
                            List<BugDetails> bugList = new ArrayList<>();
                            for (Integer invalidFieldLineNumber : invalidFieldLineNumbers) {
                                BugDetails invalidField = new BugDetails();
                                invalidField.setLineNumber(String.valueOf(invalidFieldLineNumber));
                                invalidField.setSeverityOfBug("Error");
                                invalidField.setBugCategory("InvalidClassFields");
                                invalidField
                                        .setBug("These are Invalid Fields. Fields Should be Only private final"
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
                            if (CollectionUtils.isNotEmpty(bugList)) {
                                constantFile.getBugDetailsList().addAll(bugList);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        // Cannot find the file in the specified absolute path.
                    }
                    return constantFile;
                }).collect(Collectors.toList());

    }
}
