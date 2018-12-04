package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.FileReader;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

public class JsAuthor {

   public String getJsAuthor(String jsFilePath) {
      String author = "NoAuthor";
      if (jsFilePath.endsWith(".js")) {
         File jsFile = new File(jsFilePath);
         CompilerEnvirons env = new CompilerEnvirons();
         env.setRecordingLocalJsDocComments(true);
         env.setRecordingComments(true);
         env.setStrictMode(false);
         env.setAllowMemberExprAsFunctionName(true);
         env.setAllowSharpComments(true);
         env.setGeneratingSource(true);
         env.setIdeMode(true);
         try {
            AstRoot node = new Parser(env).parse(new FileReader(jsFile), null, 1);
            String classDoc = node.getComments().first().getValue();
            String[] commentLines = classDoc.split("\\r?\\n");
            for (int commentLine = 0; commentLine < commentLines.length; commentLine++) {
               if (commentLines[commentLine].contains("@author")) {
                  String authorLine = commentLines[commentLine];
                  author = authorLine.replace("*", "").replace("@author", "").trim();
               }
            }
         }
         catch (Exception e) {
         }
      }
      return author;
   }

}
