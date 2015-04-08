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

package org.tiogasolutions.lib.spring.amazon;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.core.io.*;

public class S3PropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

  private final List<String> s3Locations = new ArrayList<String>();
  private final List<Resource> resources = new ArrayList<Resource>();

  public S3PropertyPlaceholderConfigurer() {
  }

  public S3PropertyPlaceholderConfigurer(String systemPropertyName) throws MalformedURLException, URISyntaxException {
    ExceptionUtils.assertNotNull(systemPropertyName, "systemPropertyName");

    String systemPropertyValue = System.getProperty(systemPropertyName);
    ExceptionUtils.assertNotNull(systemPropertyValue, systemPropertyName);

    if (systemPropertyValue.toLowerCase().startsWith("s3:")) {
      s3Locations.add(systemPropertyValue);

    } else {
      URL url = new URL(systemPropertyValue);
      resources.add(new UrlResource(url));
    }
  }

  @Override
  public void setLocations(Resource[] resources) {
    Collections.addAll(this.resources, resources);
  }

  public void setS3Locations(String[] s3Locations) {
    for (String s3Location : s3Locations) {
      String path = resolvePlaceholder(s3Location, new Properties());
      this.s3Locations.add(path);
    }
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    List<Resource> allResources = new ArrayList<Resource>();
    allResources.addAll(resources);

    if (s3Locations.isEmpty() == false) {
      S3ResourceLoader resourceLoader = new S3ResourceLoader();
      for (String s3Location : s3Locations) {
        allResources.add(resourceLoader.getResource(s3Location));
      }
    }

    super.setLocations(ReflectUtils.toArray(Resource.class, allResources));
    super.postProcessBeanFactory(beanFactory);
  }

  @Override
  protected void loadProperties(Properties props) throws IOException {
    super.loadProperties(props);

    for (Map.Entry<Object,Object> entry : props.entrySet()) {
      String key = entry.getKey().toString();
      if (key.startsWith("system.")) {
        key = key.substring(7);
        System.setProperty(key, (String)entry.getValue());
      }
    }
  }
}