package org.tiogasolutions.lib.jaxrs.jackson;

import java.io.*;
import java.util.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import javax.xml.bind.DatatypeConverter;
import org.tiogasolutions.dev.common.*;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.QueryResult;

public class SimpleRestClient {


  public static final Map<String,Object> EMPTY_QUERY = Collections.emptyMap();

  private boolean notFoundToNull;
  private final JsonTranslator translator;

  private final String apiUrl;
  private final String username;
  private final String password;

  public SimpleRestClient(JsonTranslator translator, String apiUrl) {
    this(false, translator, parseUrl(apiUrl), parseUserFromUrl(apiUrl), parsePassFromUrl(apiUrl));
  }

  public SimpleRestClient(JsonTranslator translator, String apiUrl, String authentication) {
    this(false, translator, parseUrl(apiUrl), parseUserFromAuth(authentication), parsePassFromAuth(authentication));
  }

  public SimpleRestClient(JsonTranslator translator, String apiUrl, String username, String password) {
    this(false, translator, parseUrl(apiUrl), username, password);
  }

  public SimpleRestClient(boolean notFoundToNull, JsonTranslator translator, String apiUrl, String username, String password) {
    this.apiUrl = apiUrl;
    this.username = username;
    this.password = password;

    this.translator = translator;
    this.notFoundToNull = notFoundToNull;
  }


  public boolean isNotFoundToNull() {
    return notFoundToNull;
  }

  public void setNotFoundToNull(boolean notFoundToNull) {
    this.notFoundToNull = notFoundToNull;
  }

  public static String parseUrl(String apiUrl) {
    if (StringUtils.isBlank(apiUrl)) {
      return null;
    }

    String auth = parseAuth(apiUrl);

    if (StringUtils.isBlank(auth)) {
      return apiUrl;
    } else if (apiUrl.toLowerCase().startsWith("http://")) {
      return apiUrl.replace("http://"+auth+"@", "http://");
    } else if (apiUrl.toLowerCase().startsWith("https://")) {
      return apiUrl.replace("https://"+auth+"@", "https://");
    } else {
      String msg = String.format("Unable to parse the specified URL - does not start with \"http://\" or \"https://\".");
      throw new IllegalArgumentException(msg);
    }
  }

  public static String parseAuth(String apiUrl) {
    int pos;
    if (StringUtils.isBlank(apiUrl)) {
      return null;
    } else if (apiUrl.toLowerCase().startsWith("http://")) {
      pos = 7;
    } else if (apiUrl.toLowerCase().startsWith("https://")) {
      pos = 8;
    } else {
      String msg = String.format("Unable to parse the specified URL - does not start with \"http://\" or \"https://\".");
      throw new IllegalArgumentException(msg);
    }

    String contents = apiUrl.substring(pos);
    pos = contents.indexOf("/");
    if (pos < 0) pos = contents.length();

    String left = contents.substring(0, pos);

    pos = left.indexOf("@");

    if (pos < 0) {
      return null;
    } else {
      return left.substring(0, pos);
    }
  }
  public static String parseUserFromUrl(String apiUrl) {
    String auth = parseAuth(apiUrl);
    return parseUserFromAuth(auth);
  }
  public static String parsePassFromUrl(String apiUrl) {
    String auth = parseAuth(apiUrl);
    return parsePassFromAuth(auth);
  }
  public static String parseUserFromAuth(String authentication) {
    if (StringUtils.isBlank(authentication)) {
      return null;
    }

    int pos = authentication.indexOf(":");
    if (pos < 0) return authentication;

    String name = authentication.substring(0, pos);
    return StringUtils.isNotBlank(name) ? name : null;
  }
  public static String parsePassFromAuth(String authentication) {
    if (StringUtils.isBlank(authentication)) {
      return null;
    }

    int pos = authentication.indexOf(":");
    if (pos < 0) return null;

    String pass = authentication.substring(pos+1);
    return StringUtils.isNotBlank(pass) ? pass : null;
  }



  public void put(String subUrl) {
    put(null, subUrl, null);
  }

  public void put(String subUrl, Object entity) {
    put(null, subUrl, entity);
  }

  public <T> T put(Class<T> returnType, String subUrl) {
    return put(returnType, subUrl, null);
  }

  public <T> T put(Class<T> returnType, String subUrl, Object entity) {

    Invocation.Builder builder = builder(subUrl, Collections.<String,Object>emptyMap(), MediaType.APPLICATION_JSON);
    String json = (entity == null) ? null : translator.toJson(entity);

    Response response = builder.put(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));
    return translateResponse(returnType, response);
  }



  public void post(String subUrl) {
    post(null, subUrl, null);
  }

  public void post(String subUrl, Object entity) {
    post(null, subUrl, entity);
  }

  public <T> T post(Class<T> returnType, String subUrl) {
    return post(returnType, subUrl, null);
  }

  public <T> T post(Class<T> returnType, String subUrl, Object entity) {

    Invocation.Builder builder = builder(subUrl, Collections.<String,Object>emptyMap(), MediaType.APPLICATION_JSON);
    String json = (entity == null) ? null : translator.toJson(entity);

    Response response = builder.post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));
    return translateResponse(returnType, response);
  }


  @SuppressWarnings("unchecked")
  public <T> QueryResult<T> getQueryResult(Class<T> returnType, String subUrl, String... queryStrings) {
    return get(QueryResult.class, subUrl, queryStrings);
  }

  public <T> T get(Class<T> returnType, String subUrl, String... queryStrings) {
    Map<String,Object> queryMap = new HashMap<>();
    queryMap.putAll(toMap(queryStrings));
    return get(returnType, subUrl, queryMap, "application/json");
  }

  public <T> T get(Class<T> returnType, String subUrl, Map<String, Object> queryMap) {
    return get(returnType, subUrl, queryMap, "application/json");
  }

  public <T> T get(Class<T> returnType, String subUrl, Map<String, Object> queryMap, String...acceptedResponseTypes) {

    Invocation.Builder builder = builder(subUrl, queryMap, acceptedResponseTypes);

    Response response = builder.get(Response.class);
    return translateResponse(returnType, response);
  }



  private <T> T translateResponse(Class<T> returnType, Response response) {

    if (response.getStatus() == 404 && notFoundToNull) return null;
    assertResponse(response.getStatus());

    Object retValue;

    if (returnType == null) {
      // return object not expected
      return null;

    } else if (Response.class.equals(returnType)) {
      retValue = response;

    } else {
      // The return type will use the string content...
      String content = response.readEntity(String.class);

      if (String.class.equals(returnType)) {
        // A simple string - clean up after MS Windows
        retValue = content.replaceAll("\r", "");

      } else {
        // A json object to be translated.
        retValue = translator.fromJson(returnType, content);
      }
    }

    // noinspection unchecked
    return returnType.cast(retValue);
  }


  public byte[] getBytes(String subUrl, Map<String, Object> queryMap, String...acceptedResponseTypes) throws IOException {
    Invocation.Builder builder = builder(subUrl, queryMap, acceptedResponseTypes);
    Response response = builder.get(Response.class);

    if (response.getStatus() == 404 && notFoundToNull) return null;
    assertResponse(response.getStatus());

    InputStream in = (InputStream)response.getEntity();
    return IoUtils.toBytes(in);
  }

  public <T> List<T> getList(Class<T> returnType, String subUrl, String...queryStrings) {
    Map<String,Object> queryMap = new HashMap<>();
    queryMap.putAll(toMap(queryStrings));
    return getList(returnType, subUrl, queryMap);
  }

  public <T> List<T> getList(Class<T> returnType, String subUrl, Map<String, Object> queryMap) {
    Invocation.Builder builder = builder(subUrl, queryMap, "application/json");
    Response response = builder.get();

    if (response.getStatus() == 404 && notFoundToNull) return null;
    assertResponse(response.getStatus());

    String content = response.readEntity(String.class);

    // Create a new list of the correct type.
    List<T> list = new ArrayList<>();

    // Then loop through everything - we have to treat the items in the list as objects
    for (Object object : translator.fromJson(List.class, content, returnType)) {
      list.add(returnType.cast(object));
    }

    return list;
  }



  public JsonTranslator getTranslator() {
    return translator;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  protected void assertResponse(int status) {
    HttpStatusCode statusCode = HttpStatusCode.findByCode(status);
    if (statusCode.isSuccess() == false) {
      String msg = String.format("Unexpected response: %s %s", status, statusCode.getReason());
      throw ApiException.fromCode(statusCode, msg);
    }
  }

  protected Map<String,Object> toMap(String...keyValuePairs) {

    if (keyValuePairs == null) {
      return new HashMap<>();
    }

    Map<String,Object> map = new HashMap<>();
    for (String pair : keyValuePairs) {
      int pos = (pair == null) ? -1 : pair.indexOf("=");
      if (pair == null) {
        map.put(null,null);
      } else if (pos < 0) {
        map.put(pair, null);
      } else {
        String key = pair.substring(0, pos);
        String value = pair.substring(pos+1);
        map.put(key, value);
      }
    }
    return map;
  }

  protected Invocation.Builder builder(String url, Map<String, Object> queryMap, String...acceptedResponseTypes) {
    Client client = ClientBuilder.newBuilder().build();
    UriBuilder uriBuilder = UriBuilder.fromUri(getApiUrl()).path(url);

    for (Map.Entry<String,Object> queryParam : queryMap.entrySet()) {
      uriBuilder.queryParam(queryParam.getKey(), queryParam.getValue());
    }

    WebTarget target = client.target(uriBuilder);
    Invocation.Builder builder = target.request(acceptedResponseTypes);

    if (StringUtils.isNotBlank(getUsername())) {
      builder.header("Authorization", getBasicAuthentication(getUsername(), getPassword()));
    }

    return builder;
  }

  private static String getBasicAuthentication(String username, String password) {
    try {
      String token = username + ":" + password;
      return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));

    } catch (UnsupportedEncodingException ex) {
      throw new IllegalStateException("Cannot encode with UTF-8", ex);
    }
  }
}
