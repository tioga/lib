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

import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.tiogasolutions.dev.common.EqualsUtils;

public class LanguageTag extends SimpleTagSupport {

  public static class EN extends LanguageTag {
    public EN() { super("en"); }
  }
  public static class ES extends LanguageTag {
    public ES() { super("es"); }
  }

  private String exp;
  private String val;

  public LanguageTag() {
  }

  public LanguageTag(String exp) {
    setExp(exp);
  }

  @Override
  public void doTag() throws JspException, IOException {
    if (EqualsUtils.objectsEqual(getVal(), getExp())) {
      getJspBody().invoke(null);
    }
  }

  public String getExp() {
    return exp;
  }

  public void setExp(String exp) {
    this.exp = (exp == null) ? null : exp.toLowerCase();
  }

  public String getVal() {
    if (val != null) return val;

    if (getJspContext() instanceof PageContext) {
      PageContext pageContext = (PageContext)getJspContext();
      if (pageContext.getRequest() instanceof  HttpServletRequest) {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        for (Cookie cookie : request.getCookies()) {
          if (cookie.getName().equals("lang")) {
            return cookie.getValue();
          }
        }
      }
    }

    return null;
  }

  public void setVal(String val) {
    this.val = (val == null) ? null : val.toLowerCase();
  }
}
