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

package org.tiogasolutions.lib.servlet.template.tags;

import java.io.*;
import java.net.*;
import java.util.Properties;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.tiogasolutions.dev.common.StringUtils;

public class LanguageResourceTag extends SimpleTagSupport {

  public static final String languageTagDefault = "language-tag-default";
  public static final String languageTagResourcePath = "language-tag-resource-path";

  private String key;

  public LanguageResourceTag() {
  }

  @Override
  public void doTag() throws JspException, IOException {
    try {
      Object path = getJspContext().findAttribute(languageTagResourcePath);
      if (path == null) {
        getJspContext().getOut().print("missing attribute "+languageTagResourcePath);
        return;
      }

      URL url = getClass().getResource(path.toString());
      if (url == null) {
        getJspContext().getOut().print("missing resource "+path);
        return;
      }

      Properties properties = new Properties();
      properties.load(new FileInputStream(new File(url.toURI())));
      String propertyName = getLang()+"."+key;
      String value = properties.getProperty(propertyName);
      if (StringUtils.isNotBlank(value)) {
        getJspContext().getOut().print(value);
      } else {
        getJspContext().getOut().print("missing key "+propertyName);
      }
    } catch (URISyntaxException e) {
      getJspContext().getOut().print(e.getMessage());
    }
  }

  public void setKey(String key) {
    this.key = (key == null) ? null : key.toLowerCase();
  }

  public String getLang() {
    if (getJspContext() instanceof PageContext) {
      PageContext pageContext = (PageContext)getJspContext();
      if (pageContext.getRequest() instanceof  HttpServletRequest) {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        Cookie[] cookies = (request.getCookies() == null) ? new Cookie[0] : request.getCookies();
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("lang")) {
            return cookie.getValue();
          }
        }
      }
    }

    Object defaultLang = getJspContext().findAttribute(languageTagDefault);
    return (defaultLang == null) ? "en" : defaultLang.toString();
  }
}
