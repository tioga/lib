// Copyright (c) 2010-2014, Munchie Monster, LLC.
package org.tiogasolutions.lib.spring.couchace;

import org.tiogasolutions.couchace.core.api.CouchServer;
import org.springframework.core.io.*;
import org.tiogasolutions.lib.couchace.app.AppConfig;
import org.tiogasolutions.lib.couchace.app.AppConfigStore;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

public class CouchResourceLoader implements ResourceLoader {

  private final AppConfigStore store;
  private final Properties defaultProperties;

  public CouchResourceLoader(AppConfigStore store) {
    this.store = store;
    this.defaultProperties = new Properties();
  }

  public CouchResourceLoader(AppConfigStore store, Properties defaultProperties) {
    this.store = store;
    this.defaultProperties = defaultProperties;
  }

  public CouchResourceLoader(CouchServer couchServer) {
    this(new AppConfigStore(couchServer), new Properties());
  }

  public CouchResourceLoader(CouchServer couchServer, Properties defaultProperties) {
    this(new AppConfigStore(couchServer), defaultProperties);
  }

  @Override
  public java.lang.ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

  @Override
  public Resource getResource(String entityName) {

    try {
      AppConfig appConfig = store.getByDocumentId(entityName);

      if (appConfig == null) {
        String msg = String.format("The application-configuration entity \"%s\" is missing from the \"%s\" database.", entityName, store.getDatabase().getDatabaseName());
        throw new UnsupportedOperationException(msg);
      }

      String content = appConfig.getPropertyString();
      defaultProperties.load(new StringReader(content));

      StringWriter writer = new StringWriter();
      defaultProperties.store(writer, null);

      byte[] bytes = writer.toString().getBytes();
      return new ByteArrayResource(bytes, entityName);

    } catch (IOException e) {
      throw new RuntimeException("Exception reading properties from java.lang.String.", e);
    }
  }
}