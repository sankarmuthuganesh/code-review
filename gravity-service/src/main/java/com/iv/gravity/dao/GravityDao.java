package com.iv.gravity.dao;

import java.util.List;
import java.util.Map;
import com.iv.cortex.exception.QueryException;
import com.iv.gravity.entity.CortexStoryDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.GravityCodeReviewStory;
import com.iv.gravity.entity.StoryReport;

public interface GravityDao {

   /**
    * @param username
    * @return List<CortexStoryDetails> stories and details of user fetched from cortex and greeneye
    */
   public List<CortexStoryDetails> getStoriesOfUserFromCortex(String username);

   /**
    * @param username
    * @return StoryReport
    */
   public StoryReport lookForPreviousRun(String storyId, String branch, String username);

   /**
    * insertAnalysisIntoDB insert analysis details to gravityDB.
    * 
    * @param analysisDetails
    * @param filesReport
    * @param fixedFiles
    * @return boolean
    * @throws QueryException
    */
   public boolean insertAnalysisIntoDB(Map<String, Object> analysisDetails, List<FileUnit> filesReport, byte[] fixedFiles) throws QueryException;

   /**
    * getPreviousAnalysis finds previous story analysis by the user.
    * 
    * @param storyId
    * @param branch
    * @param username
    * @param currentlyreAnalyzed to detect if download require offset
    * @return StoryReport - if null is returned expected anlysis is not run.
    */
   public StoryReport getPreviousAnalysis(String storyId, String branch, String username, String currentlyreAnalyzed);

   /**
    * @param storyId
    * @param username
    * @param fileName
    * @return FileUnit
    */
   public FileUnit getFileReport(String storyId, String branch, String username, String fileName);

   /**
    * downloadStoryReport downloads the analyzed story report to the user.
    * 
    * @param storyId
    * @param branch
    * @param username
    * @return StoryReport
    */
   public StoryReport downloadStoryReport(String storyId, String branch, String username);

   /**
    * getStoryAnalysisHistory gets story bug history from db.
    * 
    * @param storyId
    * @param branch
    * @return List<StoryReport>
    */
   public List<StoryReport> getStoryAnalysisHistory(String storyId, String branch);

   /**
    * getGitURLOfRepository gets story bug history from db.
    * 
    * @param repository
    * @return GITURL
    */
   public String getGitURLOfRepository(String repository);

   /**
    * getManagerStories gets stories related to manager.
    * 
    * @param manager
    * @return stories list
    */
   public List<String> getManagerStories(String manager, String offset, String limit, String searchKey);

   /**
    * getBugFixedFiles is used to get bug fixed files of a story.
    * 
    * @param storyId - String
    * @param branch - String
    * @return byte[] zipped bug fixed files
    */
   public byte[] getBugFixedFiles(String storyId, String branch);

}
