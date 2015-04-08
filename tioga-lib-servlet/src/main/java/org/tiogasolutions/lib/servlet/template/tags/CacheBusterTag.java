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
import java.util.Random;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class CacheBusterTag extends SimpleTagSupport {

  private static Random random = new Random(System.currentTimeMillis());

  public CacheBusterTag() {
  }

  @Override
  public void doTag() throws JspException, IOException {
    StringWriter writer = new StringWriter();
    getJspBody().invoke(writer);

    // We are working from the assumption that this tag was used
    // on a URL for the purpose of augmenting the query parameters.
    String urlString = writer.toString();

    PageContext context = (PageContext)getJspContext();
    String serverName = context.getRequest().getServerName();
    if ("localhost".equals(serverName)) {
      getJspBody().getJspContext().getOut().write(urlString);
      return;
    }

    String anchor = "";

    urlString = urlString.trim();
    int pos = urlString.indexOf("#");
    if (pos >= 0) {
      anchor = urlString.substring(pos);
      urlString = urlString.substring(0, pos);
    }

    pos = urlString.indexOf("?");

    if (pos < 0) {
      // We have no query parameters so we can just add it.
      urlString += "?";

    } else if (pos != urlString.length() - 1) {
      if (urlString.endsWith("&") == false) {
        // We have existing query parameters, just add one more
        urlString += "&";
      }
    }

    urlString += getParamName();
    urlString += "=";
    urlString += getKey();
    urlString += anchor;

    getJspBody().getJspContext().getOut().write(urlString);
  }

  public long getKey() {
    return random.nextLong();
  }

  public String getParamName() {
    return "cb";
  }
}
