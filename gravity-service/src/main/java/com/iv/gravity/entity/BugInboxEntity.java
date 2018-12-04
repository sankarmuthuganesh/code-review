package com.iv.gravity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugInboxEntity {

   /** The file Name. */
   private String fileName;

   /** The bug Count. */
   private String bugCount;

}
