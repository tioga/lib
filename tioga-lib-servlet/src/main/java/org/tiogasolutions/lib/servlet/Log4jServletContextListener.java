/*
 * Copyright 2012 Jacob D Parr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.lib.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Log4jServletContextListener implements ServletContextListener {
  
  public static final String LOG4J_RELATIVE_PATH = "log4j-relative-path";
  public static final String LOG4J_PATTERN =       "log4j-pattern";
  public static final String LOG4J_FILE_NAME =     "log4j-file-name";
  public static final String LOG4J_LOG_LEVEL =     "log4j-log-level";

  public Log4jServletContextListener() {
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext context = sce.getServletContext();

      String logLevel = context.getInitParameter(LOG4J_LOG_LEVEL);
      Level level = (logLevel == null) ? Level.WARN : Level.toLevel(logLevel);
      Logger.getRootLogger().setLevel(level);

      String stndPattern = context.getInitParameter(LOG4J_PATTERN);
      String custPattern = context.getInitParameter(LOG4J_PATTERN);

      if (stndPattern == null) stndPattern = "%-5p: %d{MM-dd-yy HH:mm:ss} [%t] %c %x- %m%n";
      ConsoleAppender appender = new ConsoleAppender(new PatternLayout(stndPattern));
      appender.setThreshold(Level.WARN);
      Logger.getRootLogger().addAppender(appender);

      if (custPattern == null) custPattern = "%-5p: %d{MM-dd-yy HH:mm:ss} [%t] %c %x- %m%n";
      appender = new ConsoleAppender(new PatternLayout(custPattern));
      appender.setThreshold(level);
      Logger.getLogger("com.munchiemonster").addAppender(appender);

      Log log = LogFactory.getLog(Log4jServletContextListener.class);
      log.info("log4j initialized, standard pattern: "+stndPattern);
      log.info("log4j initialized, custom pattern: "+custPattern);

    } catch (Throwable e) {
      e.printStackTrace(); // the best we can do...
    }
  }

//  private void createFileAppender(ServletContext context, PatternLayout layout, Level level) throws IOException {
//
//  log4j.appender.tail=org.apache.log4j.FileAppender
//  log4j.appender.tail.File=${catalina.base}/logs/tail_catalina.log
//  log4j.appender.tail.layout=org.apache.log4j.PatternLayout
//  log4j.appender.tail.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

//    File appDir = new File(context.getRealPath("/"));
//    String relPath = context.getInitParameter(LOG4J_RELATIVE_PATH);
//    if (relPath == null) relPath = "../logs";
//    File logDir = new File(appDir, relPath);
//
//    if (logDir.exists() == false && logDir.mkdirs() == false) {
//      System.out.println("** ERROR ** Cannot create the log required log directory.");
//      return;
//    }

//    File appDir = new File(context.getRealPath("/"));
//    String fileName = context.getInitParameter(LOG4J_FILE_NAME);
//    if (fileName == null) fileName = appDir.getName()+".log";
//
//    FileAppender appender = new FileAppender(layout, "/opt/tomcat7/logs/"+fileName, true);
//    appender.setThreshold(level);
//    Logger.getRootLogger().addAppender(appender);
//  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    /* do nothing */
  }
}
