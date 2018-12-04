package com.iv.gravity.controller;

import java.util.List;
import lombok.Getter;
import com.google.common.collect.ImmutableList;
import com.iv.cortex.util.EnvReader;

public class ApiConnector {

   // GIT
   public static final String GIT_136_URL = "192.168.41.136";

   private static final String GIT_136_USERNAME = "cobalt";

   private static final String GIT_136_PASSWORD = EnvReader.getGlobalPropertyValue("git.136.password");

   private static final String GIT_136_PRIVATETOKEN = "4DNN6cfJNgFd3VKdx3NU";

   private static final String GIT_190_URL = EnvReader.getGlobalPropertyValue("git.190.url");

   private static final String GIT_190_USERNAME = EnvReader.getGlobalPropertyValue("git.190.username");

   private static final String GIT_190_PASSWORD = EnvReader.getGlobalPropertyValue("git.190.password");

   private static final String GIT_190_PRIVATETOKEN = EnvReader.getGlobalPropertyValue("git.190.privatetoken");

   private static final String GIT_227_URL = "192.168.40.227";

   private static final String GIT_227_USERNAME = "cortexuser";

   private static final String GIT_227_PASSWORD = EnvReader.getGlobalPropertyValue("git.227.password");

   private static final String GIT_227_PRIVATETOKEN = "hFP_2jqii9xJXE9kwvEk";

   private static final String GIT_PRODUCT_CI_URL = "product-ci";

   private static final String GIT_PRODUCT_CI_USERNAME = "cortex";

   private static final String GIT_PRODUCT_CI_PASSWORD = "";

   private static final String GIT_PRODUCT_CI_PRIVATETOKEN = "ighJcGirPRGbSPqej1V4";

   // @Getter private static final List<String> GIT_URLS = ImmutableList.of(GIT_136_URL,
   // GIT_190_URL, GIT_227_URL, GIT_PRODUCT_CI_URL);
   @Getter
   private static final List<String> GIT_URLS = ImmutableList.of(GIT_136_URL);

   // SONARQUBE
   public static final String SONAR_209_URL = "192.168.41.209:8090";

   public static final String SONAR_209_USERNAME = "gravity";

   public static final String SONAR_209_PASSWORD = "gravity9";

   public static final String SONAR_233_URL = "192.168.41.233";

   // hari
   // abcd1234
   public static final String SONAR_233_USERNAME = "gravity";

   public static final String SONAR_233_PASSWORD = "gravity9";

   public static String getUsername(String URL) {
      if (URL.equals(GIT_136_URL)) {
         return GIT_136_USERNAME;
      }
      else if (URL.equals(GIT_190_URL)) {
         return GIT_190_USERNAME;
      }
      else if (URL.equals(GIT_227_URL)) {
         return GIT_227_USERNAME;
      }
      else if (URL.equals(GIT_PRODUCT_CI_URL)) {
         return GIT_PRODUCT_CI_USERNAME;
      }
      else {
         return "";
      }
   }

   public static String getPassword(String URL) {
      if (URL.equals(GIT_136_URL)) {
         return GIT_136_PASSWORD;
      }
      else if (URL.equals(GIT_190_URL)) {
         return GIT_190_PASSWORD;
      }
      else if (URL.equals(GIT_227_URL)) {
         return GIT_227_PASSWORD;
      }
      else if (URL.equals(GIT_PRODUCT_CI_URL)) {
         return GIT_PRODUCT_CI_PASSWORD;
      }
      else {
         return "";
      }
   }

   public static String getPrivateToken(String URL) {
      if (URL.equals(GIT_136_URL)) {
         return GIT_136_PRIVATETOKEN;
      }
      else if (URL.equals(GIT_190_URL)) {
         return GIT_190_PRIVATETOKEN;
      }
      else if (URL.equals(GIT_227_URL)) {
         return GIT_227_PRIVATETOKEN;
      }
      else if (URL.equals(GIT_PRODUCT_CI_URL)) {
         return GIT_PRODUCT_CI_PRIVATETOKEN;
      }
      else {
         return "";
      }
   }

}
