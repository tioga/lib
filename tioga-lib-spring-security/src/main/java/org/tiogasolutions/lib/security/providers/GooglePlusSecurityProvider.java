package org.tiogasolutions.lib.security.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.logging.*;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.domain.account.*;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.lib.security.CurrentUser;
import org.tiogasolutions.lib.security.providers.google.GoogleAuthentication;
import org.tiogasolutions.lib.security.providers.google.UserInfo;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class GooglePlusSecurityProvider extends AbstractUserDetailsAuthenticationProvider {

  private static final Log log = LogFactory.getLog(GooglePlusSecurityProvider.class);

  private final String clientSecret;
  private final String clientId;

  private final CurrentUserStore store;
  private final ObjectMapper objectMapper;

  public GooglePlusSecurityProvider(CurrentUserStore store, ObjectMapper objectMapper, String clientId, String clientSecret) {
    this.store = store;
    this.objectMapper = objectMapper;

    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    if (ProviderUtils.isGoogleAuthentication(authentication) == false || userDetails == null) {
      // We cannot really authenticate the password with Google but this is better than nothing.
      throw new BadCredentialsException(CurrentUser.INVALID_USER_NAME_OR_PASSWORD);
    }
  }

  @Override
  protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    try {
      if (ProviderUtils.isGoogleAuthentication(authentication) == false) {
        throw new UsernameNotFoundException("Not Goolge Authentication");
      }

      String code = authentication.getCredentials().toString();
      GoogleAuthentication googleAuth = getAuthResponse(code, clientId, clientSecret);
      UserInfo userInfo = getUserInfo(googleAuth);
      CurrentUserSource source = store.getCurrentUserSourceByEmail(userInfo.getEmail());

      if (source == null) {
        throw new BadCredentialsException(CurrentUser.INVALID_USER_NAME_OR_PASSWORD);
      }
      return new CurrentUser(source);

    } catch (IOException e) {
      throw ApiException.internalServerError("Exception during Google-Authentication", e);
    }
  }

  public static UserInfo getUserInfo(GoogleAuthentication authentication) throws IOException {
    Client client = ClientBuilder.newBuilder().build();
    UriBuilder uriBuilder = UriBuilder.fromUri("https://www.googleapis.com/oauth2/v1/userinfo");
    uriBuilder.queryParam("alt", "json");
    uriBuilder.queryParam("access_token", authentication.getAccessToken());

    Response response = client.target(uriBuilder).request(MediaType.APPLICATION_JSON_TYPE).get();
    String json = response.readEntity(String.class);
    return new TiogaJacksonObjectMapper().readValue(json, UserInfo.class);
  }

  public GoogleAuthentication getAuthResponse(String code, String clientId, String clientSecret) throws IOException {

    Client client = ClientBuilder.newBuilder().build();

    Form form = new Form();
    form.param("code", code);
    form.param("client_id", clientId);
    form.param("client_secret", clientSecret);
    form.param("grant_type", "authorization_code");
    form.param("redirect_uri", "postmessage");

    UriBuilder uriBuilder = UriBuilder.fromUri("https://accounts.google.com/o/oauth2/token");
    Response jerseyResponse = client.target(uriBuilder)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

    int status = jerseyResponse.getStatus();
    String json = jerseyResponse.readEntity(String.class);

    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
    GoogleAuthentication googleAuth = objectMapper.readValue(json, GoogleAuthentication.class);

    // If there was an error in the token info, abort.
    if (StringUtils.isNotBlank(googleAuth.getError())) {
      String msg = String.format("Authentication Error: %s", googleAuth.getError());
      throw ApiException.internalServerError(msg);
    }

    return googleAuth;
  }
}
