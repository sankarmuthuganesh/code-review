package com.iv.gravity.entity;

import lombok.Getter;

@Getter
public class FileContents {

   public final byte[] fileContents;

   FileContents(byte[] fileContent) {
      this.fileContents = fileContent;
   }

}
