package org.tiogasolutions.lib.jaxrs.client;

import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import org.tiogasolutions.dev.common.*;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.QueryResult;

@SuppressWarnings("unused")
public class SimpleRestClient {

  protected final String rootUrl;
  protected final JsonTranslator translator;

  protected Authorization authorization;
  protected boolean notFoundToNull;
  protected boolean ignoringCertificates;

  public SimpleRestClient(JsonTranslator translator, Object rootUrl) {
    this(translator, rootUrl, BasicAuthorization.fromUrl(rootUrl));
  }

  @Deprecated
  public SimpleRestClient(JsonTranslator translator, Object rootUrl, String username, String password) {
    this(false, translator, BasicAuthorization.removeBasicAuth(rootUrl), new BasicAuthorization(username, password));
  }

  public SimpleRestClient(JsonTranslator translator, Object rootUrl, Authorization authorization) {
    this(false, translator, BasicAuthorization.removeBasicAuth(rootUrl), authorization);
  }

  public SimpleRestClient(boolean notFoundToNull, JsonTranslator translator, Object rootUrl, Authorization authorization) {
    this.rootUrl = (rootUrl == null) ? null : rootUrl.toString();
    this.authorization = authorization;

    this.translator = translator;
    this.notFoundToNull = notFoundToNull;
  }


  public boolean isNotFoundToNull() {
    return notFoundToNull;
  }

  public void setNotFoundToNull(boolean notFoundToNull) {
    this.notFoundToNull = notFoundToNull;
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

    Response response;
    Invocation.Builder builder = builder(subUrl, Collections.<String,Object>emptyMap(), MediaType.APPLICATION_JSON);

    if (entity instanceof Form) {
      Form form = (Form)entity;
      response = builder.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

    } else {
      String json = (entity == null) ? null : translator.toJson(entity);
      response = builder.post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));
    }
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

  public <T> T translateResponse(Class<T> returnType, Response response) {

    String content = response.readEntity(String.class);

    if (response.getStatus() == 404 && notFoundToNull) return null;
    assertResponse(response.getStatus(), content);

    if (returnType == null) {
      return null;

    } else if (Response.class.equals(returnType)) {
      returnType.cast(response);
    }

    return translateResponse(returnType, content);
  }

  public <T> T translateResponse(Class<T> returnType, String content) {

    if (String.class.equals(returnType)) {
      // A simple string - clean up after MS Windows
      Object retValue = content.replaceAll("\r", "");
      return returnType.cast(retValue);

    } else {
      // A json object to be translated.
      Object retValue = translator.fromJson(returnType, content);
      return returnType.cast(retValue);
    }
  }

  public byte[] getBytes(String subUrl, Map<String, Object> queryMap, String...acceptedResponseTypes) throws IOException {
    Invocation.Builder builder = builder(subUrl, queryMap, acceptedResponseTypes);
    Response response = builder.get(Response.class);

    InputStream in = (InputStream)response.getEntity();
    byte[] bytes = IoUtils.toBytes(in);

    if (response.getStatus() == 404 && notFoundToNull) return null;
    assertResponse(response.getStatus(), bytes.length, null);

    return bytes;
  }

  public <T> List<T> getList(Class<T> returnType, String subUrl, String...queryStrings) {
    Map<String,Object> queryMap = new HashMap<>();
    queryMap.putAll(toMap(queryStrings));
    return getList(returnType, subUrl, queryMap);
  }

  public <T> List<T> getList(Class<T> returnType, String subUrl, Map<String, Object> queryMap) {
    Invocation.Builder builder = builder(subUrl, queryMap, "application/json");
    Response response = builder.get();

    String content = response.readEntity(String.class);

    if (response.getStatus() == 404 && notFoundToNull) return null;
    assertResponse(response.getStatus(), content);

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

  public String getRootUrl() {
    return rootUrl;
  }

  public Authorization getAuthorization() {
    return authorization;
  }

  public void setAuthorization(Authorization authorization) {
    this.authorization = authorization;
  }

  public boolean isIgnoringCertificates() {
    return ignoringCertificates;
  }

  public void setIgnoringCertificates(boolean ignoringCertificates) {
    this.ignoringCertificates = ignoringCertificates;
  }

  protected void assertResponse(int status, String content) {
    int length = (content == null) ? -1 : content.length();
    assertResponse(status, length, content);
  }

  protected void assertResponse(int status, int length, String content) {
    HttpStatusCode statusCode = HttpStatusCode.findByCode(status);
    if (statusCode.isSuccess() == false) {
      String msg = String.format("Unexpected response: %s %s", status, statusCode.getReason());
      String[] traits = {
          String.format("length:%s", length),
          String.format("content:%s", content)
      };
      throw ApiException.fromCode(statusCode, msg, traits);
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

  protected ClientBuilder createClient() {
    ClientBuilder builder = ClientBuilder.newBuilder();
    if (!ignoringCertificates) return builder;

    try {
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }

      }}, new java.security.SecureRandom());
      return builder.sslContext(sslcontext).hostnameVerifier((s1, s2) -> true);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected Invocation.Builder builder(String url, Map<String, Object> queryMap, String...acceptedResponseTypes) {

    Client client = createClient().build();

    UriBuilder uriBuilder = UriBuilder.fromUri(getRootUrl()).path(url);

    for (Map.Entry<String,Object> queryParam : queryMap.entrySet()) {
      uriBuilder.queryParam(queryParam.getKey(), queryParam.getValue());
    }

    WebTarget target = client.target(uriBuilder);
    Invocation.Builder builder = target.request(acceptedResponseTypes);

    if (authorization != null) {
      builder.header("Authorization", authorization.getHeaderValue());
    }

    return builder;
  }
}
