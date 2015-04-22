// Copyright (c) 2010-2014, Munchie Monster, LLC.
package org.tiogasolutions.lib.couchace.app;

import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public class AppConfigStore extends DefaultCouchStore<AppConfig> {

  public static final String DEFAULT_DB_NAME = "app-config";
  public static final String DEFAULT_DESIGN_NAME = "app-config";

  private String designName;
  private String databaseName;

  public AppConfigStore(CouchServer couchServer) {
    this(couchServer, DEFAULT_DB_NAME, DEFAULT_DESIGN_NAME);
  }

  public AppConfigStore(CouchServer couchServer, String databaseName) {
    this(couchServer, databaseName, DEFAULT_DESIGN_NAME);
  }

  public AppConfigStore(CouchServer couchServer, String databaseName, String designName) {
    super(couchServer, AppConfig.class);
    this.designName = designName;
    this.databaseName = databaseName;
  }

  @Override
  public String getDesignName() {
    return designName;
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }
}
