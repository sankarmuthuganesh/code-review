package com.iv.gravity.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlameDetails implements Serializable {

   private static final long CURRENT_VERSION = 0;

   // The Commit Reference
   private String commitID;

   // The Author of Commit
   private String authorName;

   // When it is Committed
   private Date commitTime;

   // The Mail of Author
   private String mailID;

   // The Commited Lines
   private List<String> commitedLines;

   // The Commited Line Numbers
   private List<String> commitedLineNumbers;

   // Serialization
   public void write(ObjectOutputStream output) throws IOException {
      output.writeLong(CURRENT_VERSION);
      output.writeUTF(commitID);
      output.writeUTF(authorName);
      output.writeObject(commitTime);
      output.writeUTF(mailID);
      output.writeObject(commitedLines);
      output.writeObject(commitedLineNumbers);
   }

   public void read(ObjectInputStream input) throws IOException, ClassNotFoundException {
      long currentVersion = input.readLong();
      if (currentVersion == 0) {
         this.commitID = input.readUTF();
         this.authorName = input.readUTF();
         this.commitTime = (Date) input.readObject();
         this.mailID = input.readUTF();
         this.commitedLines = (List<String>) input.readObject();
         this.commitedLineNumbers = (List<String>) input.readObject();
      }

   }

}
