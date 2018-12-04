package com.iv.gravity.service.bugfixer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.iv.cortex.date.DateStyle;
import com.iv.cortex.date.DateTimeUtils;
import com.iv.cortex.date.TimeStyle;

public class BugFixer {
   public byte[] fixBug(Map<String, List<Fix>> fileAndFixes, Map<String, List<String>> hotDeployFixes) throws IOException {
      ByteArrayOutputStream zippedOutput = new ByteArrayOutputStream();
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zippedOutput);
      ZipOutputStream zipStream = new ZipOutputStream(bufferedOutputStream);
      AtomicBoolean someFixDone = new AtomicBoolean();
      fileAndFixes.entrySet().forEach(file -> {
         List<Fix> fixes = file.getValue();
         List<String> modifiedLineNumbers = new ArrayList<>();
         AtomicBoolean fixDone = new AtomicBoolean();
         if (CollectionUtils.isNotEmpty(fixes)) {
            // To avoid removing a line affecting another line fix.
            Comparator<Fix> linesReverseFix = (FixOne, FixTwo) -> {
               return Integer.compare(FixOne.getLineNumber(), FixTwo.getLineNumber());
            };
            // Do the Fix
            fixes.stream().sorted(linesReverseFix.reversed()).forEach(fix -> {
               if (fix.getOperation().equals("delete")) {
                  fixDone.set(FileEditor.inFile(new File(file.getKey())).delete().inLineNumber(fix.getLineNumber()).fix());
                  if (fixDone.get()) {
                     someFixDone.set(true);
                     modifiedLineNumbers.add(String.valueOf(fix.getLineNumber()));
                  }
               }
               else if (fix.getOperation().equals("replace")) {
                  fixDone.set(FileEditor.inFile(new File(file.getKey())).text(fix.getText()).replaceWithText(fix.getReplacementText())
                     .inLineNumber(fix.getLineNumber()).fix());
                  if (fixDone.get()) {
                     someFixDone.set(true);
                     modifiedLineNumbers.add(String.valueOf(fix.getLineNumber()));
                  }
               }
               else if (fix.getOperation().equals("insert")) {
                  // if (fixDone.get()) {
                  // someFixDone.set(true);
                  // modifiedLineNumbers.add(String.valueOf(fix.getLineNumber()));
                  // }
               }
            });

            // Doc Writer
            try {
               writeInDoc(file.getKey(), modifiedLineNumbers);
            }
            catch (IOException e) {
            }

            // Zip the File For Download
            if (fixDone.get()) {
               try {
                  zipStream.putNextEntry(new ZipEntry("FixedFiles/" + new File(file.getKey()).getName()));
                  zipStream.write(FileUtils.readFileToByteArray(new File(file.getKey())));
                  zipStream.closeEntry();
               }
               catch (IOException e) {
               }

            }
         }
      });

      hotDeployFixes.entrySet().forEach(hotDeployFixedFile -> {
         String fixedFile = hotDeployFixedFile.getKey();
         // Doc Writer
         try {
            writeInDoc(fixedFile, hotDeployFixedFile.getValue());
         }
         catch (IOException e) {
         }

         // Zip the File For Download
         try {
            zipStream.putNextEntry(new ZipEntry("FixedFiles/" + new File(fixedFile).getName()));
            zipStream.write(FileUtils.readFileToByteArray(new File(fixedFile)));
            zipStream.closeEntry();
         }
         catch (IOException e) {

         }
      });
      zipStream.close();
      bufferedOutputStream.close();
      zippedOutput.close();
      if (someFixDone.get()) {
         return zippedOutput.toByteArray();
      }
      else {
         return null;
      }
   }

   private void writeInDoc(String filePath, List<String> modifiedLineNumbers) throws IOException {
      BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);

      // Content in Docs
      String fixTime = DateTimeUtils.format(
         OffsetDateTime.parse(OffsetDateTime.now().toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toZonedDateTime(), DateStyle.LONG,
         TimeStyle.SHORT, false);
      String content = "@author Gravity CR-Tool LastModified this file at " + fixTime + ", in line nos. " + modifiedLineNumbers.toString();

      Map<Integer, String> lineNoAndContent = new LinkedHashMap<>();
      boolean modifiedAuthor = false;
      int lineNo = 0;
      String line = null;
      try {
         boolean firstDocFound = false;
         while (((line = bufferedReader.readLine()) != null)) {
            lineNo++;
            if (StringUtils.startsWithAny(line.trim(), new String[] { "*", "/" })) {
               firstDocFound = true;
               if (line.contains("@author")) {
                  line = content;
                  modifiedAuthor = true;
               }
               lineNoAndContent.put(lineNo, line);
            }
            if (!StringUtils.startsWithAny(line.trim(), new String[] { "*", "/" }) && firstDocFound) {
               break;
            }
         }
         Iterator<Integer> docLineIterator = lineNoAndContent.keySet().iterator();
         int docStartLine = docLineIterator.next();

         lineNoAndContent.entrySet().forEach(fileLine -> {
            FileEditor.inFile(new File(filePath)).inLineNumber(fileLine.getKey()).replaceWithText(fileLine.getValue()).fix();
         });
         if (!modifiedAuthor) {
            FileEditor.inFile(new File(filePath)).inLineNumber(docStartLine + lineNoAndContent.size()).insert().text(content).fix();
         }
      }
      catch (Error | RuntimeException e) {

      }
      finally {
         bufferedReader.close();

      }

   }
}
