package com.iv.gravity.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iv.gravity.constants.GravityTables;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.StoryReport;

public class StoryMapper implements RowMapper {

   @Override
   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
      ObjectMapper jsonConvert = new ObjectMapper();
      StoryReport storyReport = new StoryReport();
      storyReport.setStoryId(rs.getString(GravityTables.COLUMN_STORYID));
      storyReport.setUsername(rs.getString(GravityTables.COLUMN_USERNAME));
      storyReport.setStoryType(rs.getString(GravityTables.COLUMN_STORY_TYPE));
      storyReport.setTimeAnalyzed(rs.getString(GravityTables.COLUMN_TIMEANALYZED));
      storyReport.setRepository(rs.getString(GravityTables.COLUMN_REPOSITORY));
      storyReport.setBranch(rs.getString(GravityTables.COLUMN_BRANCH));
      storyReport.setLicenseGroup(rs.getString(GravityTables.COLUMN_LICENSE_GROUP));
      storyReport.setLicense(rs.getString(GravityTables.COLUMN_LICENSE));
      storyReport.setSubsystem(rs.getString(GravityTables.COLUMN_SUBSYSTEM));
      storyReport.setEpic(rs.getString(GravityTables.COLUMN_EPIC));
      storyReport.setTotalBugs(String.valueOf(rs.getInt(GravityTables.COLUMN_TOTAL_BUGS)));
      storyReport.setCriticalBugs(String.valueOf(rs.getInt(GravityTables.COLUMN_CRITICAL_BUGS)));
      storyReport.setMajorBugs(String.valueOf(rs.getInt(GravityTables.COLUMN_MAJOR_BUGS)));
      storyReport.setMinorBugs(String.valueOf(rs.getInt(GravityTables.COLUMN_MINOR_BUGS)));
      storyReport.setAnalyserIp(rs.getString(GravityTables.COLUMN_ANALYSER_IP));
      storyReport.setAdditionalData(null);
      try {
         storyReport
            .setFilesWithReport(jsonConvert.readValue(rs.getString(GravityTables.COLUMN_FILES_REPORT_HISTORY), new TypeReference<List<FileUnit>>() {
            }));
         // storyReport.setAdditionalData(jsonConvert.readValue(rs.getString(GravityTables.COLUMN_ADDITIONAL_DATA_STORY),
         // new TypeReference<Map<String,String>>() {}));
      }
      catch (Exception e) {
      }
      return storyReport;
   }

}
