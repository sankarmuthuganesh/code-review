package com.iv.gravity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.iv.cortex.config.IvServiceConfig;
import com.iv.cortex.ivcortexplus.util.IvQueryUtils;
import com.iv.cortex.util.EnvReader;
import com.iv.gravity.dao.GravityDao;
import com.iv.gravity.dao.GravityDaoImpl;
import com.iv.gravity.dao.SyncSchemaDataSource;
import com.iv.gravity.service.bugfixer.BugFixer;
import com.iv.gravity.service.bugs.AddHotDeployBugs;
import com.iv.gravity.service.bugs.AvoidKeywordBugs;
import com.iv.gravity.service.bugs.ExternalBugFinder;
import com.iv.gravity.service.bugs.IndexError;
import com.iv.gravity.service.bugs.NestedStream;
import com.iv.gravity.service.bugs.SeperateUnnecessaryConstantsJava;
import com.iv.gravity.service.bugs.UnnecessaryClassFieldJava;
import com.iv.gravity.service.utilities.AuthorFind;
import com.iv.gravity.service.utilities.BlamesUsingCommits;
import com.iv.gravity.service.utilities.HotDeployWriteSynchronizer;
import com.iv.gravity.service.utilities.MailSender;
import com.iv.gravity.service.utilities.PieChart;
import com.iv.gravity.service.utilities.ReadWriteSynchronizer;
import com.iv.gravity.service.utilities.TargetSpecificFiles;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.iv.gravity,com.iv.cortex")
@Import({ IvServiceConfig.class })
public class GravityApplicationConfiguration {

   // ---------------Database Beans---------------
   @Bean
   public GravityDao gravityDao() {
      return new GravityDaoImpl();
   }

   @Bean(destroyMethod = "close")
   public HikariDataSource gravityDataSource() {
      HikariConfig config = new HikariConfig();
      config.setDriverClassName(IvQueryUtils.POSTGRES);
      config.setJdbcUrl(EnvReader.getGlobalPropertyValue("gravity.url"));
      config.setUsername(EnvReader.getGlobalPropertyValue("gravity.user"));
      config.setPassword(EnvReader.getGlobalPropertyValue("gravity.password"));
      config.setSchema(EnvReader.getGlobalPropertyValue("gravity.schema"));
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.addDataSourceProperty("useServerPrepStmts", "true");
      // For always active connection minimumIdle to 0 in this case, and a fairly aggressive idleTimeout.
      // config.setMaximumPoolSize(10);
      // config.setMinimumIdle(5);
      // config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
      // config.setIdleTimeout(TimeUnit.SECONDS.toMillis(300));
      // config.setMaxLifetime(TimeUnit.SECONDS.toMillis(900));
      // config.setAutoCommit(false);
      // config.setConnectionTestQuery("SELECT 1");
      // config.setLeakDetectionThreshold(30000);
      return new HikariDataSource(config);
   }

   @Bean(destroyMethod = "close")
   public SyncSchemaDataSource domainSyncDataSource() {
      return new SyncSchemaDataSource();
   }

   // ---------------Synchronised Writer Beans---------------

   @Bean
   public ReadWriteSynchronizer avoidKeywordReadWriteSync() {
      return new ReadWriteSynchronizer();
   }

   @Bean
   public HotDeployWriteSynchronizer hotDeployBugFinderDetailsSync() {
      return new HotDeployWriteSynchronizer();
   }

   // ---------------Files Manipulation Beans---------------

   @Bean
   public AuthorFind findAuthor() {
      return new AuthorFind();
   }

   @Bean
   public BlamesUsingCommits blameDetails() {
      return new BlamesUsingCommits();
   }

   @Bean
   public TargetSpecificFiles manipulateDomain() {
      return new TargetSpecificFiles();
   }

   @Bean
   public PieChart pieChart() {
      return new PieChart();
   }

   // ---------------Bug Finders Beans---------------
   @Bean
   public AvoidKeywordBugs avoidKeywordBugs() {
      return new AvoidKeywordBugs();
   }

   @Bean
   public UnnecessaryClassFieldJava unnecessaryClassFieldJava() {
      return new UnnecessaryClassFieldJava();
   }

   @Bean
   public SeperateUnnecessaryConstantsJava seperateUnnecessaryConstantsJava() {
      return new SeperateUnnecessaryConstantsJava();
   }

   @Bean
   public NestedStream nestedStream() {
      return new NestedStream();
   }

   @Bean
   public IndexError indexError() {
      return new IndexError();
   }

   @Bean
   public AddHotDeployBugs addHotDeployBugs() {
      return new AddHotDeployBugs();
   }

   @Bean
   public ExternalBugFinder externalBugFinder() {
      return new ExternalBugFinder();
   }

   @Bean
   public BugFixer bugFixer() {
      return new BugFixer();
   }

   // ---------------Mail Sender---------------

   @Bean
   public MailSender mailSender() {
      return new MailSender();
   }
}
