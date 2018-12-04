package com.iv.gravity.service.utilities;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import com.iv.gravity.entity.AvoidKeywordBugDetails;

public class ReadWriteSynchronizer {

   public List<AvoidKeywordBugDetails> synchronizedAccess(String[] writeContent) throws IOException, URISyntaxException {
      synchronized (this) {
         if (Objects.isNull(writeContent)) {
            ReadAvoidKeywords read = new ReadAvoidKeywords();
            return read.getAvoidKeywords();
         }
         else {
            WriteAvoidKeyword write = new WriteAvoidKeyword();
            write.writeExcel(writeContent);
            return null;
         }
      }
   }

}
