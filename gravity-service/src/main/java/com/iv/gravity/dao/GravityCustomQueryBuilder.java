package com.iv.gravity.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import com.iv.cortex.auditlog.AuditLog;
import com.iv.cortex.context.ContextBean;
import com.iv.cortex.exception.QueryException;
import com.iv.cortex.exception.ServerSideValidationException;
import com.iv.cortex.querybuider.IvQueryExecutor;
import com.iv.cortex.querybuider.IvQueryTransactionManager;
import com.iv.cortex.querybuider.QueryType;
import com.iv.cortex.querybuider.SelectQueryBuilder;
import com.iv.cortex.validation.ConnectionUtil;
import com.iv.cortex.validation.DomainValidation;

/**
 * Query Builder class for INSERT query
 */
public class GravityCustomQueryBuilder extends IvQueryTransactionManager<GravityCustomQueryBuilder> {

   private String query;

   private String conflictQuery = StringUtils.EMPTY;

   private Map<String, String> columnsAndTheirTypes;

   private static final int INITIAL_VALUE = 0;

   private static final int START_VALUE = 1;

   private static final String UPPER_CASE_REGEX = "([A-Z])";

   private static final String REPLACE_STRING = "_$1";

   public GravityCustomQueryBuilder() {

   }

   /**
    * Builds the INSERT Query
    * 
    * @return this statement.
    * @throws QueryException
    */
   public String castQueryBuilder(String tableName, Map<String, String> columnsAndTheirTypes) throws QueryException {
      String query;
      StringBuilder builder = new StringBuilder();
      StringBuilder valueBuilder = new StringBuilder();
      StringBuilder columnBuilder = new StringBuilder();
      builder.append("INSERT INTO " + tableName + " ( ");

      Iterator<Entry<String, String>> columnIterator = columnsAndTheirTypes.entrySet().iterator();
      while (columnIterator.hasNext()) {
         Entry<String, String> currentColumn = columnIterator.next();
         String columnName = currentColumn.getKey().replaceAll(UPPER_CASE_REGEX, REPLACE_STRING).toLowerCase();
         columnBuilder.append(columnName);
         valueBuilder.append(" ? ");
         // if a casting type is mentioned
         if (!currentColumn.getValue().isEmpty()) {
            valueBuilder.append("::" + currentColumn.getValue());
         }
         if (columnIterator.hasNext()) {
            valueBuilder.append(" , ");
            columnBuilder.append(" , ");
         }
      }
      builder.append(columnBuilder.toString());
      builder.append(" ) VALUES ( ");
      builder.append(valueBuilder.toString());
      builder.append(" ) ");
      if (StringUtils.isNotEmpty(conflictQuery)) {
         builder.append(conflictQuery);
      }
      query = builder.toString();
      if (columnsAndTheirTypes.size() <= INITIAL_VALUE) {
         throw new QueryException("Insert Column is missing");
      }
      return query;
   }

   /**
    * Builds the INSERT Query
    * 
    * @return this statement.
    * @throws QueryException
    */
   public String insertQueryBuilder(String tableName, String[] columns) throws QueryException {
      String query;
      StringBuilder builder = new StringBuilder();
      StringBuilder valueBuilder = new StringBuilder();
      StringBuilder columnBuilder = new StringBuilder();
      builder.append("INSERT INTO " + tableName + " ( ");
      for (int count = INITIAL_VALUE; count < columns.length; count++) {
         columns[count] = columns[count].replaceAll(UPPER_CASE_REGEX, REPLACE_STRING).toLowerCase();
         columnBuilder.append(columns[count]);
         valueBuilder.append(" ? ");
         if (count != columns.length - START_VALUE) {
            valueBuilder.append(" , ");
            columnBuilder.append(" , ");
         }
      }
      builder.append(columnBuilder.toString());
      builder.append(" ) VALUES ( ");
      builder.append(valueBuilder.toString());
      builder.append(" ) ");
      if (StringUtils.isNotEmpty(conflictQuery)) {
         builder.append(conflictQuery);
      }
      query = builder.toString();
      if (columns.length <= INITIAL_VALUE) {
         throw new QueryException("Insert Column is missing");
      }
      return query;

   }

   /**
    * upsertWithKeyList is bulk insert with update on unique constraint .
    * 
    * @param valuesList(key should be tableName.columnName)
    * @param tableName
    * @param doUpdate (true will do update ,false will do nothing)
    * @param updateColumnsList (columns to update on unique constraint)
    * @param constraintKey (need to pass all primarykey columns)
    * @return
    * @throws QueryException
    */

   public String upsertWithKeyList(String tableName, Set<String> columnsSet, boolean doUpdate, Set<String> updateColumnsList, String... constraintKey)
      throws QueryException {
      StringBuilder builder = new StringBuilder();
      final String tableNameString = tableName.replaceAll(UPPER_CASE_REGEX, REPLACE_STRING).toLowerCase();

      builder.append("INSERT INTO " + tableNameString + " ( ");
      if (CollectionUtils.isEmpty(columnsSet)) {
         throw new QueryException("Column name should be tableName.columnName");
      }
      builder.append(StringUtils.join(columnsSet.toArray(), " , "));
      builder.append(" ) VALUES ( ");
      builder.append(StringUtils.join(columnsSet.stream().map(mapper -> {
         return " ? ";
      }).collect(Collectors.toList()), " , "));
      builder.append(" ) ");

      if (constraintKey.length > INITIAL_VALUE) {
         builder.append(" ON CONFLICT ( " + StringUtils.join(constraintKey, " , ").replaceAll(UPPER_CASE_REGEX, REPLACE_STRING).toLowerCase() + ") ");
      }
      else {
         throw new ServerSideValidationException("Constraint key should not be empty");
      }
      doUpdateWithList(doUpdate, builder, updateColumnsList);
      query = builder.toString();
      return query;
   }

   /**
    * doUpdateWithList is used build query with sub query on conflict.
    * 
    * @param doUpdate
    * @param builder
    * @param updateColumnsList
    */
   private void doUpdateWithList(boolean doUpdate, StringBuilder builder, Collection<String> updateColumnsList) {
      if (doUpdate) {
         AtomicInteger colCount = new AtomicInteger(START_VALUE);
         builder.append("  DO UPDATE SET  ");
         updateColumnsList.stream().forEach(action -> {
            String column = action.replaceAll(UPPER_CASE_REGEX, REPLACE_STRING).toLowerCase();
            builder.append(column + " = EXCLUDED." + column);
            if (colCount.getAndIncrement() < updateColumnsList.size()) {
               builder.append(" , ");
            }
         });
      }
      else {
         builder.append(" DO NOTHING ");
      }
   }

}
