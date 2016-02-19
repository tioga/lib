package org.tiogasolutions.lib.jaxrs.client;

import org.tiogasolutions.dev.common.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

public class BasicAuthorization implements Authorization {

  private final String username;
  private final String password;

  public BasicAuthorization(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String getHeaderValue() {
    try {
      String token = username + ":" + password;
      return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));

    } catch (UnsupportedEncodingException ex) {
      throw new UnsupportedOperationException("Cannot encode with UTF-8", ex);
    }
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

  public static String parseBasicAuth(Object url) {
    if (StringUtils.isBlank(url)) {
      return null;
    }

    String urlString = url.toString();

    int pos;
    if (urlString.toLowerCase().startsWith("http://")) {
      pos = 7;
    } else if (urlString.toLowerCase().startsWith("https://")) {
      pos = 8;
    } else {
      String msg = "Unable to parse the specified URL - does not start with \"http://\" or \"https://\".";
      throw new IllegalArgumentException(msg);
    }

    String contents = urlString.substring(pos);
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

  public static String removeBasicAuth(Object url) {
    if (StringUtils.isBlank(url)) {
      return null;
    }

    String urlString = url.toString();

    String auth = BasicAuthorization.parseBasicAuth(urlString);

    if (StringUtils.isBlank(auth)) {
      return urlString;

    } else if (urlString.toLowerCase().startsWith("http://")) {
      return urlString.replace("http://"+auth+"@", "http://");

    } else if (urlString.toLowerCase().startsWith("https://")) {
      return urlString.replace("https://"+auth+"@", "https://");

    } else {
      String msg = "Unable to parse the specified URL - does not start with \"http://\" or \"https://\".";
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Creates a BasicAuthorization by parsing the username and password from the URL.
   * @param url the URL to parse. The value may be an instance of String, URI, URL or some other non-null value. The
   *            value is converted to a string by simply calling toString() on it.
   * @return The BasicAuthorization or null if a username and/or password was not found.
   */
  public static BasicAuthorization fromUrl(Object url) {
    String auth = parseBasicAuth(url);
    if (auth == null) return null;

    String username = parseUserFromAuth(auth);
    String password = parsePassFromAuth(auth);
    return new BasicAuthorization(username, password);
  }
}
