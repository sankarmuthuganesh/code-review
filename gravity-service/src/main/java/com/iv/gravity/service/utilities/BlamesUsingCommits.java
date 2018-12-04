package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.iv.gravity.entity.BlameDetails;

public class BlamesUsingCommits {

   public List<BlameDetails> getBlameDetails(String absolutePath, String gitHome, Repository repo) {
      // Since the below alternative approach for finding gitHome is adopted.
      /*
       * ---Manipulation of Paths----
       */
      // Assumption - Cloned in to %temp% folder
      // int indexUntilClonedFolder=StringUtils.ordinalIndexOf(absolutePath, "\\", 12);
      // String filePath =
      // absolutePath.substring(indexUntilClonedFolder+1).replaceAll("\\\\",
      // "/").replaceAll("//",
      // "/");
      // String gitHomeFolderPath = new FolderIteratorFinder().getGitHome(absolutePath);

      // Patch for server absolutePath
      // String afterGravityFolder =
      // absolutePath.substring(absolutePath.indexOf("Gravity"));
      // int index;
      // if (afterGravityFolder.contains(File.separator)) {
      // index = StringUtils.ordinalIndexOf(afterGravityFolder, File.separator, 3);
      // afterGravityFolder = afterGravityFolder.replace(File.separator, "/");
      // } else if (afterGravityFolder.contains("/")) {
      // index = StringUtils.ordinalIndexOf(afterGravityFolder, "/", 3);
      // } else {
      // index = StringUtils.ordinalIndexOf(afterGravityFolder, "\\", 3);
      // afterGravityFolder = afterGravityFolder.replace("\\", "/");
      // }
      //
      // String filePath = afterGravityFolder.substring(index + 1);

      // String filePath = absolutePath.replace(gitHome,
      // StringUtils.EMPTY).replace(File.separator, "/");

      String filePath = absolutePath.replace(gitHome, StringUtils.EMPTY).replace(File.separator, "/").substring(1);
      // String gitPath = gitHome + ".git";

      // FileRepositoryBuilder fileRepoBuilder = new FileRepositoryBuilder();
      // fileRepoBuilder.addCeilingDirectory(new File(gitHome));
      // fileRepoBuilder.findGitDir(new File(absolutePath));

      // Repository repo = null;
      // try {
      // if(fileRepoBuilder.getGitDir()!=null){
      // repo = fileRepoBuilder.build();
      // }
      // } catch (IOException e1) {
      // }
      // try {
      // repo = fileRepoBuilder.findGitDir(new File(gitPath)).readEnvironment().build();
      // repo = fileRepoBuilder.setGitDir(new
      // File(gitPath)).readEnvironment().findGitDir().build();
      // } catch (IOException e) {
      // ***Error Reading Repository
      // }
      // Git gitUtils = new Git(repo);

      BlameCommand blamer = new BlameCommand(repo);
      // HEAD~~ LAST TWO COMMITS OMMITED
      ObjectId commitID = null;
      try {
         commitID = repo.resolve(Constants.HEAD);
      }
      catch (RevisionSyntaxException | IOException e) {
         // Error Resolving Repo Head.
      }
      blamer.setStartCommit(commitID);
      blamer.setFilePath(filePath);
      BlameResult blame = null;
      try {
         blame = blamer.call();
      }
      catch (GitAPIException e) {
         // Cannot Make A Blame Call.
      }

      int lines = 0;
      try {
         lines = Files.readAllLines(Paths.get(absolutePath)).size();
      }
      catch (IOException e) {
         // Error Calculating the Lines of File in Commit.
      }

      List<BlameDetails> blameDetailsOfAFile = new ArrayList<>();
      for (int i = 0; i < lines; i++) {
         RevCommit commit = blame.getSourceCommit(i);
         boolean previousCommit = false;
         for (BlameDetails blames : blameDetailsOfAFile) {
            if (blames.getCommitID().equals(commit.getName())) {
               blames.getCommitedLines().add(blame.getResultContents().getString(i));
               blames.getCommitedLineNumbers().add(String.valueOf(i + 1));
               previousCommit = true;
            }
         }
         if (!previousCommit) {
            BlameDetails details = new BlameDetails();
            PersonIdent commiter = commit.getCommitterIdent();
            // Author
            details.setAuthorName(commiter.getName());
            // Commit ID
            details.setCommitID(commit.getName());
            // MailID
            details.setMailID(commiter.getEmailAddress());
            // Commit Time
            details.setCommitTime(commiter.getWhen());
            // Committed Lines
            List<String> commitedLines = new ArrayList<>();
            commitedLines.add(blame.getResultContents().getString(i));
            details.setCommitedLines(commitedLines);
            // Committed Line Numbers
            List<String> commitedLineNumbers = new ArrayList<>();
            commitedLineNumbers.add(String.valueOf(i + 1));
            details.setCommitedLineNumbers(commitedLineNumbers);

            blameDetailsOfAFile.add(details);
         }
      }

      /*
       * To Display Total Number of Lines for the File.
       */
      // final int currentLines;
      // try(final FileInputStream input = new FileInputStream(absolutePath)){
      // currentLines=IOUtils.readLines(input, "UTF-8").size();
      // }
      // System.out.println("Displayed commits responsible for "+"lines of ,current
      // version has"+currentLines+"
      // lines");

      return blameDetailsOfAFile;

   }

   // private int countLinesOfFileIncommit(Repository repo, ObjectId commitID, String filePath)
   // throws IOException {
   // try (RevWalk revWalk = new RevWalk(repo)) {
   // RevCommit commit = revWalk.parseCommit(commitID);
   // RevTree tree = commit.getTree();
   //
   // // now try to find a specific file
   // try (TreeWalk treeWalk = new TreeWalk(repo)) {
   // treeWalk.addTree(tree);
   // treeWalk.setRecursive(true);
   // treeWalk.setFilter(PathFilter.create(filePath));
   // if (!treeWalk.next()) {
   // throw new IllegalStateException("Did not find the expected File");
   // }
   // ObjectId objectId = treeWalk.getObjectId(0);
   // ObjectLoader loader = repo.open(objectId);
   //
   // // laod the content of the file into a stream
   // ByteArrayOutputStream stream = new ByteArrayOutputStream();
   // loader.copyTo(stream);
   // revWalk.dispose();
   // // Assumption UTF-8
   // return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()), "UTF-8").size();
   // }
   // }
   // }

}
