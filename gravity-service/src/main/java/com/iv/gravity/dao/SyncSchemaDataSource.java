package com.iv.gravity.dao;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import com.iv.cortex.util.EnvReader;
import com.zaxxer.hikari.HikariDataSource;

public class SyncSchemaDataSource extends HikariDataSource {
   @Autowired
   private HikariDataSource gravityDataSource;

   @Override
   public Connection getConnection() throws SQLException {
      Connection c = gravityDataSource.getConnection();
      c.setSchema(EnvReader.getGlobalPropertyValue("gravity.userdomainschema"));
      return c;
   }
}
