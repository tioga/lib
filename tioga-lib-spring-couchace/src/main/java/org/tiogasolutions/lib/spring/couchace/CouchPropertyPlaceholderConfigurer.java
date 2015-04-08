// Copyright (c) 2010-2014, Munchie Monster, LLC.

package org.tiogasolutions.lib.spring.couchace;

import com.couchace.core.api.CouchServer;
import java.io.IOException;
import java.net.*;
import java.util.*;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.lib.couchace.app.AppConfigStore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.core.io.Resource;

public class CouchPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

  private final Resource[] resources = new Resource[1];

  public CouchPropertyPlaceholderConfigurer(CouchServer couchServer, String entityName) throws MalformedURLException, URISyntaxException {
    this(couchServer, AppConfigStore.DEFAULT_DB_NAME, entityName);
  }

  public CouchPropertyPlaceholderConfigurer(String entityName) throws MalformedURLException, URISyntaxException {
    this(new DefaultCouchServer(), AppConfigStore.DEFAULT_DB_NAME, entityName);
  }

  public CouchPropertyPlaceholderConfigurer(CouchServer couchServer, String databaseName, String entityName) throws MalformedURLException, URISyntaxException {
    ExceptionUtils.assertNotNull(couchServer, "couchServer");
    ExceptionUtils.assertNotNull(entityName, "entityName");

    AppConfigStore store = new AppConfigStore(couchServer, databaseName);
    CouchResourceLoader loader = new CouchResourceLoader(store);
    resources[0] = loader.getResource(entityName);
  }

  @Override
  public void setLocations(Resource[] resources) {
    System.out.printf("");
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    super.setLocations(resources);
    super.postProcessBeanFactory(beanFactory);
  }

  @Override
  protected void loadProperties(Properties props) throws IOException {
    super.loadProperties(props);

    for (Map.Entry<Object,Object> entry : props.entrySet()) {
      String key = entry.getKey().toString();
      if (key.startsWith("system.")) {
        key = key.substring(7);
        System.setProperty(key, (String) entry.getValue());
      }
    }
  }
}