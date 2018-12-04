package com.iv.gravity.service.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;

public class AuthorFind {
   public AuthorFind() {

   }

   public String getAuthor(String absolutePath) throws IOException {
      String author = "NoAuthor";
      BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(absolutePath), StandardCharsets.UTF_8);
      String line = null;
      try {
         boolean firstDocFound = false;
         while (((line = bufferedReader.readLine()) != null)) {
            line = line.trim();
            if (StringUtils.startsWithAny(line, new String[] { "*", "/" })) {
               firstDocFound = true;
               if (line.contains("@author")) {
                  author = StringUtils.replaceEach(line, new String[] { "*", "@author" }, new String[] { "", "" }).trim();
               }
            }
            if (!StringUtils.startsWithAny(line, new String[] { "*", "/" }) && firstDocFound) {
               break;
            }
         }
      }
      catch (Error | RuntimeException e) {

      }
      finally {
         bufferedReader.close();

      }
      return author;
   }

}
