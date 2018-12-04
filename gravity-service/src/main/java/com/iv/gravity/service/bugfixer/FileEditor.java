package com.iv.gravity.service.bugfixer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.io.FileUtils;

public class FileEditor {

   private File file;

   private int lineNumber;

   private boolean insert;

   private boolean delete;

   private boolean replace;

   private int fromLineNumber;

   private int toLineNumber;

   private boolean inAllLines;

   private String replaceWithText;

   private String text;

   private boolean action = false;

   public static FileEditor inFile(File file) {
      FileEditor fixer = new FileEditor();
      fixer.file = file;
      return fixer;
   }

   public FileEditor inLineNumber(int lineNumber) {
      this.lineNumber = lineNumber;
      return this;
   }

   public FileEditor insert() {
      this.insert = true;
      return this;
   }

   public FileEditor replaceWithText(String replaceWithText) {
      this.replace = true;
      this.replaceWithText = replaceWithText;
      return this;
   }

   public FileEditor delete() {
      this.delete = true;
      return this;
   }

   public FileEditor fromLineNumber(int fromLineNumber) {
      this.fromLineNumber = fromLineNumber;
      return this;
   }

   public FileEditor toLineNumber(int toLineNumber) {
      this.toLineNumber = toLineNumber;
      return this;
   }

   public FileEditor inAllLines() {
      this.inAllLines = true;
      return this;
   }

   public FileEditor text(String text) {
      this.text = text;
      return this;
   }

   public boolean fix() {
      List<String> lines;
      try {
         lines = FileUtils.readLines(file, Charset.defaultCharset());
         if (this.inAllLines) {
            fromLineNumber = 1;
            toLineNumber = lines.size();
         }
         if (fromLineNumber == 0 && toLineNumber == 0) {
            if (delete) {
               lines.remove(lineNumber - 1);
            }
            if (insert) {
               lines.add(lineNumber - 1, text);
            }
            if (replace) {
               if (text == null) {
                  lines.set(lineNumber - 1, replaceWithText);
               }
               else {
                  lines.set(lineNumber - 1, lines.get(lineNumber - 1).replace(text, replaceWithText));
               }
            }
         }
         else if (lineNumber == 0) {
            if (delete) {
               IntStream.rangeClosed(fromLineNumber, toLineNumber).boxed().sorted(Collections.reverseOrder()).forEachOrdered(line -> {
                  lines.remove(line - 1);
               });
            }
            if (insert) {
               lines.add(fromLineNumber - 1, text);
            }
            if (replace) {
               IntStream.rangeClosed(fromLineNumber, toLineNumber).boxed().sorted(Collections.reverseOrder()).forEachOrdered(line -> {
                  if (text == null) {
                     lines.set(line - 1, replaceWithText);
                  }
                  else {
                     lines.set(line - 1, lines.get(line - 1).replace(text, replaceWithText));
                  }
               });
            }
         }
         FileUtils.writeLines(file, lines);
         action = true;
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return action;
   }

}
