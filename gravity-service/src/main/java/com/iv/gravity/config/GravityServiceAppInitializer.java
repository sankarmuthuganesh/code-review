package com.iv.gravity.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class GravityServiceAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

   @Override
   protected Class<?>[] getRootConfigClasses() {
      return new Class[] {};
   }

   @Override
   protected Class<?>[] getServletConfigClasses() {
      return new Class[] { GravityApplicationConfiguration.class };
   }

   @Override
   protected String[] getServletMappings() {
      return new String[] { "/*" };
   }

}
