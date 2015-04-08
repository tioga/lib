// Copyright (c) 2010-2014, Munchie Monster, LLC.
package org.tiogasolutions.lib.spring.couchace;

import com.couchace.core.api.CouchServer;
import org.springframework.core.io.*;
import org.tiogasolutions.lib.couchace.app.AppConfig;
import org.tiogasolutions.lib.couchace.app.AppConfigStore;

public class CouchResourceLoader implements ResourceLoader {

  private final AppConfigStore store;

  public CouchResourceLoader(AppConfigStore store) {
    this.store = store;
  }

  public CouchResourceLoader(CouchServer couchServer) {
    this(new AppConfigStore(couchServer));
  }

  @Override
  public java.lang.ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

  @Override
  public Resource getResource(String entityName) {

    AppConfig appConfig = store.getByDocumentId(entityName);
    if (appConfig == null) {
      String msg = String.format("The application-configuration entity \"%s\" is missing from the \"%s\" database.", entityName, store.getDatabase().getDatabaseName());
      throw new UnsupportedOperationException(msg);
    }

    String content = appConfig.getPropertyString();

    byte[] bytes = content.getBytes();
    return new ByteArrayResource(bytes, entityName);
  }
}