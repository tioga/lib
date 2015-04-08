package org.tiogasolutions.lib.security.providers.google;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleAuthentication {

  private final String accessToken;
  private final String tokenType;
  private final int expiresIn;
  private final String idToken;
  private final String refreshToken;

  private final String error;
  private final String errorDescription;

  @JsonCreator
  public GoogleAuthentication(@JsonProperty("error") String error,
                              @JsonProperty("error_description") String errorDescription,
                              @JsonProperty("access_token") String accessToken,
                              @JsonProperty("token_type") String tokenType,
                              @JsonProperty("expires_in") int expiresIn,
                              @JsonProperty("id_token") String idToken,
                              @JsonProperty("refresh_token") String refreshToken) {

    this.error = error;
    this.errorDescription = errorDescription;

    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
    this.idToken = idToken;
    this.refreshToken = refreshToken;
  }

  public String getError() {
    return error;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public String getIdToken() {
    return idToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }
}
