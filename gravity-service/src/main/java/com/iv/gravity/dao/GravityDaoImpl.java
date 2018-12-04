package com.iv.gravity.dao;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iv.cortex.exception.QueryException;
import com.iv.gravity.constants.GravityTables;
import com.iv.gravity.entity.CortexStoryDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.StoryReport;
import com.zaxxer.hikari.HikariDataSource;

public class GravityDaoImpl implements GravityDao {

   @Autowired
   private HikariDataSource gravityDataSource;

   @Autowired
   private SyncSchemaDataSource domainSyncDataSource;

   @Override
   public StoryReport getPreviousAnalysis(String storyId, String branch, String username, String currentlyreAnalyzed) {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      String requireOffset;
      if (currentlyreAnalyzed.equals("true")) {
         requireOffset = "OFFSET 1 LIMIT 1";
      }
      else {
         requireOffset = "LIMIT 1";
      }
      String selectQuery = "SELECT * FROM " + GravityTables.GRAVITY_HISTORY_TABLE + " WHERE " + GravityTables.COLUMN_STORYID + " = ? AND "
         + GravityTables.COLUMN_BRANCH + " = ? ORDER BY " + GravityTables.COLUMN_TIMEANALYZED + " DESC " + requireOffset;
      StoryReport storyReport = (StoryReport) gravityCustomJDBC.queryForObject(selectQuery, new Object[] { storyId, branch }, new StoryMapper());
      return storyReport;
   }

   @Override
   public boolean insertAnalysisIntoDB(Map<String, Object> storyReport, List<FileUnit> filesReport, byte[] fixedFiles) throws QueryException {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      ObjectMapper jsonConvert = new ObjectMapper();
      jsonConvert.enable(SerializationFeature.INDENT_OUTPUT);
      // GRAVITY STORY MAIN REPORT
      String storyId = String.valueOf(storyReport.get("storyId"));
      String username = String.valueOf(storyReport.get("username"));
      String storyType = String.valueOf(storyReport.get("storyType"));
      String repository = String.valueOf(storyReport.get("repository"));
      String branch = String.valueOf(storyReport.get("branch"));
      String licenseGroup = String.valueOf(storyReport.get("licenseGroup"));
      String license = String.valueOf(storyReport.get("license"));
      String subsystem = String.valueOf(storyReport.get("subsystem"));
      String epic = String.valueOf(storyReport.get("epic"));
      int totalBugs = (int) storyReport.get("totalBugs");
      int critical = (int) storyReport.get("critical");
      int major = (int) storyReport.get("major");
      int minor = (int) storyReport.get("minor");
      String analyserIp = String.valueOf(storyReport.get("analyserIp"));
      // Json
      PGobject additionalData = new PGobject();
      additionalData.setType("json");
      PGobject clientIdentity = new PGobject();
      clientIdentity.setType("inet");
      PGobject timeAnalyzed = new PGobject();
      timeAnalyzed.setType("timestamptz");
      try {
         additionalData.setValue(jsonConvert.writeValueAsString((Map<String, String>) (storyReport.get("additionalData"))));
         clientIdentity.setValue(analyserIp);
         OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.systemDefault());
         // offsetDateTime = offsetDateTime.minusHours(3);
         // offsetDateTime = offsetDateTime.minusMinutes(30);
         timeAnalyzed.setValue(String.valueOf(offsetDateTime));
      }
      catch (SQLException | JsonProcessingException e1) {
         // Cannot manipulate additional data
      }

      Object[] storyReportColumnMap = new Object[] { storyId, username, storyType, timeAnalyzed, repository, branch, licenseGroup, license, subsystem,
         epic, totalBugs, critical, major, minor, clientIdentity, additionalData };
      GravityCustomQueryBuilder customInsert = new GravityCustomQueryBuilder();
      // IvQueryExecutor storyQuery = ivQueryBuilder.insert().into(GRAVITY_MAIN_TABLE,
      // GravityTables.COLUMN_STORYID,
      // GravityTables.COLUMN_STORY_TYPE, GravityTables.COLUMN_USERNAME,
      // GravityTables.COLUMN_TIMEANALYZED,
      // GravityTables.COLUMN_REPOSITORY,
      // GravityTables.COLUMN_BRANCH, GravityTables.COLUMN_LICENSE_GROUP,
      // GravityTables.COLUMN_LICENSE,
      // GravityTables.COLUMN_SUBSYSTEM, GravityTables.COLUMN_EPIC,
      // GravityTables.COLUMN_TOTAL_BUGS,
      // GravityTables.COLUMN_CRITICAL_BUGS,
      // GravityTables.COLUMN_MAJOR_BUGS, GravityTables.COLUMN_MINOR_BUGS,
      // GravityTables.COLUMN_ANALYSER_IP,
      // GravityTables.COLUMN_ADDITIONAL_DATA_STORY).build()
      // .execute(storyReportColumnMap);
      // IvQueryExecutor storyQuery = customInsert.into(GRAVITY_MAIN_TABLE,
      // columnAndType).castQueryBuilder()
      // .execute(storyReportColumnMap);

      Set<String> nonUniqueColumnsStory = GravityTables.GRAVITY_MAIN_TABLE_COLUMNS.stream()
         .filter(column -> !column.equals(GravityTables.COLUMN_STORYID) || !column.equals(GravityTables.COLUMN_BRANCH)).collect(Collectors.toSet());

      // String insertQuery = customInsert.castQueryBuilder(GRAVITY_MAIN_TABLE,
      // columnAndType);
      String insertQueryStory = customInsert.upsertWithKeyList(GravityTables.GRAVITY_MAIN_TABLE, GravityTables.GRAVITY_MAIN_TABLE_COLUMNS, true,
         nonUniqueColumnsStory, GravityTables.COLUMN_STORYID, GravityTables.COLUMN_BRANCH);
      int storyInserted = 0;
      try {
         storyInserted = gravityCustomJDBC.update(insertQueryStory, storyReportColumnMap);
      }
      catch (Exception e) {

      }
      // GRAVITY STORY FILES REPORT
      List<Object[]> fileReports = filesReport.stream().map(file -> {
         // Blame Details
         PGobject blameDetails = new PGobject();
         blameDetails.setType("jsonb");
         // Bugs
         PGobject bugDetails = new PGobject();
         bugDetails.setType("jsonb");
         // Json
         PGobject additionalDataFile = new PGobject();
         additionalDataFile.setType("json");
         // Json
         PGobject fileContents = new PGobject();
         fileContents.setType("bytea");
         try {
            blameDetails.setValue(jsonConvert.writeValueAsString(file.getBlamesOfTheFile()));
            bugDetails.setValue(jsonConvert.writeValueAsString(file.getBugDetailsList()));
            Map<String, String> noDetails = new HashMap<>();
            additionalDataFile.setValue(jsonConvert.writeValueAsString(noDetails));
            fileContents.setValue(null);
         }
         catch (JsonProcessingException | SQLException jsonorsqle) {
            // Cannot convert java object to string for writing as jsonb
            // Cannot setValue as PGObject
         }

         Object[] fileReport = new Object[] { storyId + "-" + file.getFileName(), file.getNatureOfFile(), file.getRepository(), file.getBranch(),
            file.getLicenseGroup(), file.getLicense(), file.getSubSystem(), file.getEpicName(), file.getRemotePath(), file.getAuthorOfFile(),
            Integer.valueOf(file.getNumberOfLines()), file.isAutogenerated(), file.getTotalBugs(), file.getCriticalBugs(), file.getMajorBugs(),
            file.getMinorBugs(), blameDetails, storyId, username, bugDetails, fileContents, additionalDataFile };
         return fileReport;
      }).collect(Collectors.toList());
      Set<String> nonUniqueColumnsFile = GravityTables.GRAVITY_FILE_REPORT_TABLE_COLUMNS.stream()
         .filter(column -> !column.equals(GravityTables.COLUMN_FILE_NAME) || !column.equals(GravityTables.COLUMN_BRANCH_FILE))
         .collect(Collectors.toSet());

      String insertQueryFile = customInsert.upsertWithKeyList(GravityTables.GRAVITY_FILE_REPORT_TABLE,
         GravityTables.GRAVITY_FILE_REPORT_TABLE_COLUMNS, true, nonUniqueColumnsFile, GravityTables.COLUMN_FILE_NAME,
         GravityTables.COLUMN_BRANCH_FILE);
      // IvQueryExecutor fileQuery =
      // ivQueryBuilder.insert().into(GRAVITY_FILE_REPORT_TABLE,
      // GravityTables.COLUMN_FILE_NAME, GravityTables.COLUMN_REPOSITORY_FILE,
      // GravityTables.COLUMN_BRANCH_FILE, GravityTables.COLUMN_LICENSE_GROUP_FILE,
      // GravityTables.COLUMN_LICENSE_FILE,
      // GravityTables.COLUMN_SUBSYSTEM_FILE, GravityTables.COLUMN_EPIC_FILE,
      // GravityTables.COLUMN_REMOTE_PATH, GravityTables.COLUMN_AUTHOR_OF_FILE,
      // GravityTables.COLUMN_NUMBER_OF_LINES,
      // GravityTables.COLUMN_IS_AUTOGENERATED, GravityTables.COLUMN_TOTAL_BUGS_FILE,
      // GravityTables.COLUMN_CRITICAL_BUGS_FILE,
      // GravityTables.COLUMN_MAJOR_BUGS_FILE, GravityTables.COLUMN_MINOR_BUGS_FILE,
      // GravityTables.COLUMN_BLAME_DETAILS,
      // FOREIGN_GravityTables.COLUMN_STORY_ID,
      // FOREIGN_GravityTables.COLUMN_USER_ANALYZED,
      // GravityTables.COLUMN_BUG_DETAILS, GravityTables.COLUMN_FILE_CONTENTS,
      // GravityTables.COLUMN_ADDITIONAL_DATA_FILE).build()
      // .execute(fileReports);
      int[] fileInserted = new int[] { 0 };
      try {
         fileInserted = gravityCustomJDBC.batchUpdate(insertQueryFile, fileReports);
      }
      catch (Exception e) {

      }
      // History Maintain
      String historyQuery = customInsert.insertQueryBuilder(GravityTables.GRAVITY_HISTORY_TABLE,
         GravityTables.GRAVITY_HISTORY_TABLE_COLUMNS.toArray(new String[] {}));
      Object[] historyValues = Arrays.copyOf(storyReportColumnMap, storyReportColumnMap.length);
      PGobject filesReportHistory = new PGobject();
      filesReportHistory.setType("jsonb");
      try {
         filesReportHistory.setValue(jsonConvert.writeValueAsString(filesReport));
      }
      catch (SQLException | JsonProcessingException e1) {
      }
      Object fileReportObject = filesReportHistory;
      ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(historyValues));
      temp.add(fileReportObject);
      int historyLogged = 0;
      try {
         historyLogged = gravityCustomJDBC.update(historyQuery, temp.toArray());
      }
      catch (Exception e) {

      }

      // Bug Fixed Files Storage
      int fixFilesStored = 1;
      if (Objects.nonNull(fixedFiles)) {
         Object[] storyBugFixColumnMap = new Object[] { storyId, storyType, repository, branch, licenseGroup, license, subsystem, epic, fixedFiles,
            timeAnalyzed, username };

         Set<String> nonUniqueColumnsBugFix = GravityTables.GRAVITY_STORE_BUGFIX_TABLE_COLUMNS.stream()
            .filter(column -> !column.equals(GravityTables.COLUMN_STORYID) || !column.equals(GravityTables.COLUMN_BRANCH))
            .collect(Collectors.toSet());

         String insertQueryStoryBugFix;
         try {
            insertQueryStoryBugFix = customInsert.upsertWithKeyList(GravityTables.GRAVITY_STORE_BUGFIX_TABLE,
               GravityTables.GRAVITY_STORE_BUGFIX_TABLE_COLUMNS, true, nonUniqueColumnsBugFix, GravityTables.COLUMN_STORYID,
               GravityTables.COLUMN_BRANCH);
            fixFilesStored = gravityCustomJDBC.update(insertQueryStoryBugFix, storyBugFixColumnMap);
         }
         catch (QueryException e1) {
         }
      }
      return storyInserted == 1 && historyLogged == 1 && fixFilesStored == 1 && !Arrays.stream(fileInserted).anyMatch(insert -> insert == 0);
   }

   @Override
   public List<CortexStoryDetails> getStoriesOfUserFromCortex(String username) {
      JdbcTemplate userDomainDataSource = new JdbcTemplate(domainSyncDataSource);
      List<CortexStoryDetails> cortexStories = null;
      String userAssignedIssuesQuery = "SELECT DISTINCT " + GravityTables.COLUMN_ROOTID + " FROM " + GravityTables.GREENEYE_ISSUE_TABLE + " WHERE "
         + GravityTables.COLUMN_ASSIGNEE + " = ?";
      List<String> stories = userDomainDataSource.queryForList(userAssignedIssuesQuery, new Object[] { username }, String.class);
      stories.add("9346");
      Map<String, List<String>> paramMap = new HashMap<>();
      paramMap.put("stories", stories);
      paramMap.put("status", Arrays.asList("In Progress"));

      String storyAssignedQuery = "SELECT * FROM " + GravityTables.CORTEX_STORY_INFO + " WHERE " + GravityTables.COLUMN_CTX_STORY_ID
         + " IN (:stories)";
      // + " AND "+GravityTables.COLUMN_CTX_STORY_STATUS+" = (:status)";
      NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(userDomainDataSource.getDataSource());
      List<Map<String, Object>> storiesRows = template.queryForList(storyAssignedQuery, paramMap);
      cortexStories = storiesRows.stream().map(story -> {
         Map<String, List<String>> repositories = new HashMap<>();
         String repoQuery = "SELECT * FROM " + GravityTables.CORTEX_REPO_INFO + " WHERE " + GravityTables.COLUMN_CTX_REPO_ID + " = ? AND "
            + GravityTables.COLUMN_CTX_REPO_IDTYPE + " = ?";
         List<Map<String, Object>> repoDetails = userDomainDataSource.queryForList(repoQuery,
            new Object[] { String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_ID)), "StoryId" });
         Optional<String> primaryRepository = repoDetails.stream()
            .filter(repotype -> String.valueOf(repotype.get(GravityTables.COLUMN_CTX_REPO_TYPE)).equals("Primary"))
            .map(storyrepo -> String.valueOf(storyrepo.get(GravityTables.COLUMN_CTX_REPO_NAME))).findFirst();
         if (primaryRepository.isPresent()) {
            repositories.put("primary", Arrays.asList(primaryRepository.get()));
         }
         else {
            repositories.put("primary", new ArrayList<>());
         }
         List<String> secondaryRepositories = repoDetails.stream()
            .filter(repotype -> String.valueOf(GravityTables.COLUMN_CTX_REPO_TYPE).equals("Secondary"))
            .map(storyrepo -> String.valueOf(storyrepo.get(GravityTables.COLUMN_CTX_REPO_NAME))).collect(Collectors.toList());
         repositories.put("secondary", secondaryRepositories);
         String[] folderPath = String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_FOLDER_PATH)).split("\\.");
         String subsystem = "";
         String epic = "";
         if (!(folderPath.length == 0)) {
            epic = folderPath[0];
         }
         if (folderPath.length > 1) {
            subsystem = folderPath[1];
         }
         String repositoryGITURL = "";
         if (!CollectionUtils.isEmpty(repositories.get("primary"))) {
            String sql = "SELECT " + GravityTables.COLUMN_CORTEX_PROJECT_INFO_GITURL + " FROM " + GravityTables.CORTEX_PROJECT_INFO + " WHERE "
               + GravityTables.COLUMN_CORTEX_PROJECT_INFO_PROJECTNAME + "=? LIMIT 1";
            repositoryGITURL = (String) userDomainDataSource.queryForObject(sql, new Object[] { repositories.get("primary").get(0) }, String.class);
         }

         CortexStoryDetails cortexStory = new CortexStoryDetails(String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_ID)),
            String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_NAME)), String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_STATUS)),
            repositories, repositoryGITURL, String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_WORKING_BRANCH)), "", "", subsystem, epic,
            String.valueOf(story.get(GravityTables.COLUMN_CTX_STORY_LAUNCH_VERSION)));
         return cortexStory;
      }).collect(Collectors.toList());
      return cortexStories;
   }

   @Override
   public FileUnit getFileReport(String storyId, String branch, String username, String fileName) {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      // IvConditionBuilder storyUsernameMatch = IvConditionBuilder.instance();
      // storyUsernameMatch.eq("ivGravityFileReport.story_id","ivGravityStoryCodereview.story_id",true).and()
      // .eq("ivGravityFileReport.user_analysed",
      // "ivGravityStoryCodereview.username",true);

      // List<Map<String, Object>> filesReport =
      // ivQueryBuilder.select().get("ivGravityFileReport."+GravityTables.COLUMN_FILE_NAME,
      // "ivGravityFileReport."+GravityTables.COLUMN_REPOSITORY_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_BRANCH_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_LICENSE_GROUP_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_LICENSE_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_SUBSYSTEM_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_EPIC_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_REMOTE_PATH,
      // "ivGravityFileReport."+GravityTables.COLUMN_AUTHOR_OF_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_NUMBER_OF_LINES,
      // "ivGravityFileReport."+GravityTables.COLUMN_IS_AUTOGENERATED,
      // "ivGravityFileReport."+GravityTables.COLUMN_TOTAL_BUGS_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_CRITICAL_BUGS_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_MAJOR_BUGS_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_MINOR_BUGS_FILE,
      // "ivGravityFileReport."+GravityTables.COLUMN_BLAME_DETAILS,
      // "ivGravityFileReport."+GravityTables.COLUMN_BUG_DETAILS
      // ).from(GravityTables.GRAVITY_FILE_REPORT_TABLE, "ivGravityFileReport")
      // .join(GravityTables.GRAVITY_MAIN_TABLE, "ivGravityStoryCodereview",
      // storyUsernameMatch)
      // .where(IvConditionBuilder.instance().eq("ivGravityFileReport."+GravityTables.COLUMN_FILE_NAME,
      // fileName).and()
      // .eq("ivGravityFileReport."+GravityTables.FOREIGN_COLUMN_STORY_ID,
      // storyId).and().eq("ivGravityFileReport."+GravityTables.FOREIGN_COLUMN_USER_ANALYZED,
      // username))
      // .build(true).execute();
      String selectQuery = "SELECT * FROM " + GravityTables.GRAVITY_FILE_REPORT_TABLE + " WHERE " + GravityTables.COLUMN_FILE_NAME + " = ? AND "
         + GravityTables.COLUMN_BRANCH_FILE + " = ?";
      try {
         FileUnit fileWithReport = (FileUnit) gravityCustomJDBC.queryForObject(selectQuery, new Object[] { fileName, branch }, new FileUnitMapper());
         return fileWithReport;
      }
      catch (Exception e) {
         return null;
      }
   }

   @Override
   public StoryReport downloadStoryReport(String storyId, String branch, String username) {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      String selectQuery = "SELECT * FROM " + GravityTables.GRAVITY_MAIN_TABLE + " WHERE " + GravityTables.COLUMN_STORYID + " = ? AND "
         + GravityTables.COLUMN_BRANCH + " = ?";
      // + " AND "+GravityTables.COLUMN_USERNAME+" = ?";
      StoryReport storyReport = (StoryReport) gravityCustomJDBC.queryForObject(selectQuery, new Object[] { storyId, branch }, new StoryMapper());
      String selectQueryForFiles = "SELECT * FROM " + GravityTables.GRAVITY_FILE_REPORT_TABLE + " WHERE " + GravityTables.FOREIGN_COLUMN_STORY_ID
         + " = ? AND " + GravityTables.COLUMN_BRANCH_FILE + " = ?";
      List<FileUnit> fileReports = (List<FileUnit>) gravityCustomJDBC.query(selectQueryForFiles, new Object[] { storyId, branch },
         new FileUnitMapper());
      storyReport.setFilesWithReport(fileReports);
      return storyReport;
   }

   @Override
   public StoryReport lookForPreviousRun(String storyId, String branch, String username) {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      String selectQuery;
      Object[] values;
      if (!username.equals("none")) {
         selectQuery = "SELECT * FROM " + GravityTables.GRAVITY_HISTORY_TABLE + " WHERE " + GravityTables.COLUMN_USERNAME + " = ?" + " ORDER BY "
            + GravityTables.COLUMN_TIMEANALYZED + " DESC LIMIT 1";
         values = new Object[] { username };
      }
      else {
         selectQuery = "SELECT * FROM " + GravityTables.GRAVITY_HISTORY_TABLE + " WHERE " + GravityTables.COLUMN_STORYID + " = ? AND "
            + GravityTables.COLUMN_BRANCH + " = ?" + " ORDER BY " + GravityTables.COLUMN_TIMEANALYZED + " DESC LIMIT 1";
         values = new Object[] { storyId, branch };
      }
      StoryReport storyReport;
      try {
         storyReport = (StoryReport) gravityCustomJDBC.queryForObject(selectQuery, values, new StoryMapper());
      }
      catch (Exception e) {
         storyReport = null;
      }

      return storyReport;
   }

   @Override
   public List<StoryReport> getStoryAnalysisHistory(String storyId, String branch) {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      if (branch.equals("none")) {
         String lastBranchQuery = "SELECT " + GravityTables.COLUMN_BRANCH + " FROM " + GravityTables.GRAVITY_HISTORY_TABLE + " WHERE "
            + GravityTables.COLUMN_STORYID + "=? ORDER BY " + GravityTables.COLUMN_TIMEANALYZED + " DESC LIMIT 1";
         try {
            String lastAnalyzedBranch = (String) gravityCustomJDBC.queryForObject(lastBranchQuery, new Object[] { storyId }, String.class);
            branch = lastAnalyzedBranch;
         }
         catch (EmptyResultDataAccessException e) {

         }
      }
      String selectQueryForStory = "SELECT * FROM " + GravityTables.GRAVITY_HISTORY_TABLE + " WHERE " + GravityTables.COLUMN_STORYID + " = ? AND "
         + GravityTables.COLUMN_BRANCH + " = ?" + " ORDER BY " + GravityTables.COLUMN_TIMEANALYZED + " ASC LIMIT 8";
      List<StoryReport> storyBugHistory = (List<StoryReport>) gravityCustomJDBC.query(selectQueryForStory, new Object[] { storyId, branch },
         new StoryMapper());

      return storyBugHistory;
   }

   @Override
   public String getGitURLOfRepository(String repository) {
      JdbcTemplate userDomainDataSource = new JdbcTemplate(domainSyncDataSource);
      String sql = "SELECT " + GravityTables.COLUMN_CORTEX_PROJECT_INFO_GITURL + " FROM " + GravityTables.CORTEX_PROJECT_INFO + " WHERE "
         + GravityTables.COLUMN_CORTEX_PROJECT_INFO_PROJECTNAME + "=? LIMIT 1";
      String gitURL = (String) userDomainDataSource.queryForObject(sql, new Object[] { repository }, String.class);
      return gitURL;
   }

   @Override
   public List<String> getManagerStories(String manager, String offset, String limit, String searchKey) {
      JdbcTemplate userDomainDataSource = new JdbcTemplate(domainSyncDataSource);
      String searchStory = "";
      Object[] parameters;
      if (!searchKey.equals("none")) {
         searchStory = "AND " + GravityTables.COLUMN_ROOTID + " = ? ";
         parameters = new Object[] { manager, searchKey };
      }
      else {
         parameters = new Object[] { manager };
      }
      String managerStoriesQuery = "SELECT DISTINCT " + GravityTables.COLUMN_ROOTID + " FROM " + GravityTables.GREENEYE_ISSUE_TABLE + " WHERE "
         + GravityTables.COLUMN_INCHARGE_INPUT02 + " = ? " + searchStory + "LIMIT " + limit + " OFFSET " + offset;

      List<String> stories = userDomainDataSource.queryForList(managerStoriesQuery, parameters, String.class);
      return stories;
   }

   @Override
   public byte[] getBugFixedFiles(String storyId, String branch) {
      JdbcTemplate gravityCustomJDBC = new JdbcTemplate(gravityDataSource);
      String selectQuery;
      Object[] values;
      selectQuery = "SELECT " + GravityTables.COLUMN_BUG_FIXED_FILES + " FROM " + GravityTables.GRAVITY_STORE_BUGFIX_TABLE + " WHERE "
         + GravityTables.COLUMN_STORYID + " = ? AND " + GravityTables.COLUMN_BRANCH + " = ?";
      values = new Object[] { storyId, branch };
      byte[] storyBugFixedFiles = null;
      try {
         storyBugFixedFiles = gravityCustomJDBC.queryForObject(selectQuery, values, (rs, rowNum) -> rs.getBytes(1));
      }
      catch (Exception e) {

      }
      return storyBugFixedFiles;
   }

}
