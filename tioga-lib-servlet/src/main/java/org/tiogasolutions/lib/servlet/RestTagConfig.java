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

import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class RestTagConfig {

  private String apiUrl;
  private String resourceUrl;

  private long domainId;
  private String apiKey;

  public RestTagConfig() {
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    ExceptionUtils.assertNotNull(apiKey, "apiKey");
    this.apiKey = apiKey;
  }

  /** @return The id that identifies the root domain object. */
  public long getDomainId() {
    return domainId;
  }

  public void setDomainId(long domainId) {
    ExceptionUtils.assertNotNull(domainId, "domainId");
    this.domainId = domainId;
  }

  /** @return The url of the api. */
  public String getApiUrl() {
    return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
    ExceptionUtils.assertNotNull(apiUrl, "apiUrl");
    if (apiUrl.endsWith("/")) {
      apiUrl = apiUrl.substring(0, apiUrl.length()-1);
    }
    this.apiUrl = apiUrl;
  }

  public String getResourceUrl() {
    return resourceUrl;
  }

  public void setResourceUrl(String resourceUrl) {
    ExceptionUtils.assertNotNull(resourceUrl, "resourceUrl");
    if (resourceUrl.endsWith("/")) {
      resourceUrl = resourceUrl.substring(0, resourceUrl.length()-1);
    }
    this.resourceUrl = resourceUrl;
  }
}
