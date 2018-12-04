package com.iv.gravity.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BugDetails implements Serializable {

   private static final long CURRENT_VERSION = 0;

   // Git URL Path
   private String remotePathOfFile;

   // File Name
   private String fileName;

   // Severity Of Bug
   public String severityOfBug;

   // Category Of Bug
   public String bugCategory;

   // Line Number Where Bug Happened
   public String lineNumber;

   // The Bug Explanation
   public String bug;

   // The Buggy Lines in the File
   public String buggyLines;

   // Serialization
   public void write(ObjectOutputStream output) throws IOException {
      output.writeLong(CURRENT_VERSION);
      output.writeUTF(remotePathOfFile);
      output.writeUTF(fileName);
      output.writeUTF(severityOfBug);
      output.writeUTF(bugCategory);
      output.writeUTF(lineNumber);
      output.writeUTF(bug);
      output.writeUTF(buggyLines);
   }

   public void read(ObjectInputStream input) throws IOException {
      long currentVersion = input.readLong();
      if (currentVersion == 0) {
         this.remotePathOfFile = input.readUTF();
         this.fileName = input.readUTF();
         this.severityOfBug = input.readUTF();
         this.bugCategory = input.readUTF();
         this.lineNumber = input.readUTF();
         this.bug = input.readUTF();
         this.buggyLines = input.readUTF();
      }

   }

}
