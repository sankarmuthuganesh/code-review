package com.iv.gravity.service.bugs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.springframework.beans.factory.annotation.Autowired;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.hotdeploy.BugDetails;

public class ExternalBugFinder {

   @Autowired
   private AddHotDeployBugs addHotDeployBugs;

   public Map<String, List<String>> hotDeployedBugFinder(List<FileUnit> storyFiles) throws URISyntaxException {
      Map<String, List<String>> fileAndFixedLineNumebers = new HashMap<>();
      Path path = Paths.get(getClass().getClassLoader().getResource("hotdeploybugfinderjars").toURI());
      File bugsFolder = path.toFile();
      storyFiles.stream().forEach(storyFile -> {
         List<BugDetails> bugList = new ArrayList<>();
         for (File bugJar : bugsFolder.listFiles()) {
            if (bugJar.isFile() && bugJar.getName().endsWith(".jar")) {
               JarFile jarFile;
               try {
                  jarFile = new JarFile(bugJar);
                  Enumeration<JarEntry> e = jarFile.entries();
                  // ClassPool cp = ClassPool.getDefault();
                  while (e.hasMoreElements()) {
                     JarEntry je = e.nextElement();
                     if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        continue;
                     }
                     // -6 because of .class
                     String className = je.getName().substring(0, je.getName().length() - 6);
                     className = className.replace('/', '.');
                     Class<?> cls;
                     try {
                        cls = Class.forName(className);
                        Object object = cls.newInstance();
                        Method bugFinderLogic = cls.getMethod("bugFinderLogic", File.class);
                        Method bugFixLogic = cls.getMethod("bugFixLogic", File.class);

                        Map<Boolean, List<String>> bugFixDone = addHotDeployBugs.executeAndFindBugs(storyFile, (so) -> {
                           try {
                              return (Map<Boolean, BugDetails>) bugFinderLogic.invoke(object, so);
                           }
                           catch (Exception ee) {
                              return null;
                           }
                        }, (so) -> {
                           try {
                              return (Map<Boolean, List<String>>) bugFixLogic.invoke(object, so);
                           }
                           catch (Exception ee) {
                              return null;
                           }
                        });
                        if (bugFixDone.keySet().iterator().next()) {
                           List<String> fixedLineNumebers = bugFixDone.values().iterator().next();
                           fileAndFixedLineNumebers.put(storyFile.getAbsolutePath(), fixedLineNumebers);
                        }
                     }
                     catch (InstantiationException | RuntimeException | ClassNotFoundException | AssertionError | IllegalAccessException
                        | NoSuchMethodException e1) {
                     }
                     // try {
                     // CtClass ctClass = cp.get(className);
                     // System.out.println(ctClass);
                     // } catch (NotFoundException ec) {
                     // }
                  }
               }
               catch (IOException e2) {
               }
            }
         }
      });
      return fileAndFixedLineNumebers;
   }
}
