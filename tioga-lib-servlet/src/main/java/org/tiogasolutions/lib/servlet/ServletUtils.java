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

import java.io.*;
import java.net.*;
import javax.servlet.http.*;

public class ServletUtils {

  public static String encodeUrl(final String value) {
    try {
      return (value == null) ? null : URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new UnsupportedOperationException("Cannot decode string as UTF-8.", ex);
    }
  }

  public static String decodeUrl(final String value) {
    try {
      return (value == null) ? null : URLDecoder.decode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new UnsupportedOperationException("Cannot decode string as UTF-8.", ex);
    }
  }

  public static Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    cookies = (cookies != null) ? cookies : new Cookie[0];
    
    for (Cookie cookie : cookies) {
      if (name.equals(cookie.getName())) {
        return cookie;
      }
    }
    return null;
  }

  public static void setCookie(HttpServletResponse response, String name, Object value, int expiry) {
    String stringValue = (value == null) ? null : value.toString();
    Cookie cookie = new Cookie(name, stringValue);
    cookie.setMaxAge(expiry);
    response.addCookie(cookie);
  }

  public static String buildUrlFromReferrer(HttpServletRequest request, String uri) {
    return buildUrlFromReferrer(request.getHeader("referer"), uri);
  }

  public static String buildUrlFromReferrer(String referer, String uri) {

    String url = "";
    int pos = (referer == null) ? -1 : referer.indexOf("//");

    if (pos >=0 ) {
      pos = referer.indexOf("/", pos+2);
      if (pos >=0 ) {
        url = referer.substring(0, pos);        
      }
    }
    
    url += uri; 
    
    return url;
  }
  
  public static String buildUrl(HttpServletRequest request, String uri) {

    String url = "";
    url += request.getScheme();
    url += "://";
    url += request.getServerName();
    url += (request.getServerPort() == 80) ? "" : ":"+request.getServerPort();
    url += uri; 
    
    return url;
  }

  public static Long getLong(HttpServletRequest request, String propertyName) {
    String value = request.getParameter(propertyName);
    if (value == null) return null;

    try {
      return Long.valueOf(value);

    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
