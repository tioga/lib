// Copyright (c) 2010-2014, Munchie Monster, LLC.
package org.tiogasolutions.lib.couchace.app;

import com.couchace.annotations.CouchEntity;
import com.couchace.annotations.CouchId;
import com.couchace.annotations.CouchRevision;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.StringWriter;

@CouchEntity("app-config")
public class AppConfig {

  private String revision;
  private String appConfigId;
  private String[] properties;

  @JsonCreator
  public AppConfig(@JsonProperty("appConfigId") String appConfigId,
                   @JsonProperty("revision") String revision,
                   @JsonProperty("properties") String[] properties) {

    this.appConfigId = appConfigId;
    this.revision = revision;
    this.properties = properties;
  }

  @CouchId
  public String getAppConfigId() {
    return appConfigId;
  }

  @CouchRevision
  public String getRevision() {
    return revision;
  }

  public String[] getProperties() {
    return properties;
  }

  public String getProperty(String propertyName) {
    for (String property : properties) {
      String key = propertyName+"=";
      if (property.startsWith(key)) {
        return property.substring(key.length());
      }
    }
    return null;
  }

  @JsonIgnore
  public String getPropertyString() {
    StringWriter writer = new StringWriter();

    for (String property : properties) {
      writer.write(property);
      writer.write("\n");
    }

    return writer.toString();
  }
}
