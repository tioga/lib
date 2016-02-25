package org.tiogasolutions.lib.jaxrs.client;

public class BearerAuthorization implements Authorization {

  private final String accessToken;

  public BearerAuthorization(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  @Override
  public String getHeaderValue() {
    return "bearer " + accessToken;
  }
}
