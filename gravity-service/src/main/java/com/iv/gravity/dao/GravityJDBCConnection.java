package com.iv.gravity.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import com.iv.cortex.ivcortexplus.util.IvQueryUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class GravityJDBCConnection {

   // Database Connection

   public static final String POSTGRES_DB_GREENEYE = "jdbc:postgresql://192.168.40.218:5432/greeneye";

   public static final String GREENEYE_USERNAME = "greeneye";

   public static final String GREENEYE_PASSWORD = "greeneye";

   public static final String GREENEYE_SCHEMA = "greeneye";

   public static final String POSTGRES_DB_HUECORTEX = "jdbc:postgresql://192.168.40.218:5432/hue_cortex";

   public static final String HUECORTEX_USERNAME = "hue_cortex";

   public static final String HUECORTEX_PASSWORD = "hue_cortex";

   private static final String HUECORTEX_SCHEMA = "hue_cortex";

   public static JdbcTemplate connectGreeneye() {
      HikariConfig config = new HikariConfig();
      config.setDriverClassName(IvQueryUtils.POSTGRES);
      config.setJdbcUrl(POSTGRES_DB_GREENEYE);
      config.setUsername(GREENEYE_USERNAME);
      config.setPassword(GREENEYE_PASSWORD);
      config.setSchema(GREENEYE_SCHEMA);
      return getJDBCTemplate(config);
   }

   public static JdbcTemplate connectHueCortex() {
      HikariConfig config = new HikariConfig();
      config.setDriverClassName(IvQueryUtils.POSTGRES);
      config.setJdbcUrl(POSTGRES_DB_HUECORTEX);
      config.setUsername(HUECORTEX_USERNAME);
      config.setPassword(HUECORTEX_PASSWORD);
      config.setSchema(HUECORTEX_SCHEMA);
      return getJDBCTemplate(config);
   }

   private static JdbcTemplate getJDBCTemplate(HikariConfig config) {
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.addDataSourceProperty("useServerPrepStmts", "true");
      // config.setMaximumPoolSize(10);
      // config.setMinimumIdle(5);
      // config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
      // config.setIdleTimeout(TimeUnit.SECONDS.toMillis(300));
      // config.setMaxLifetime(TimeUnit.SECONDS.toMillis(900));
      // config.setAutoCommit(false);
      // config.setConnectionTestQuery("SELECT 1");
      // config.setLeakDetectionThreshold(30000);
      return new JdbcTemplate(new HikariDataSource(config));
   }

}
