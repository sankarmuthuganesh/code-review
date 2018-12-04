package com.iv.gravity.service.utilities;

import java.io.IOException;
import java.net.URISyntaxException;

public class HotDeployWriteSynchronizer {

   public void synchronizedAccess(String[] writeContent) throws IOException, URISyntaxException {
      synchronized (this) {
         HotDeployBugFinderDetailsWriter write = new HotDeployBugFinderDetailsWriter();
         write.writeExcel(writeContent);
      }
   }

}
