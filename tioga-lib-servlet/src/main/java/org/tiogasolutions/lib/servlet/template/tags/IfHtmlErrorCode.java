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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class IfHtmlErrorCode extends SimpleTagSupport {

  public int errorCode;

  public void setErrorCode(final int value) {
    errorCode = value;
  }

  @Override
  public void doTag() throws JspException {
    try {

      Integer actualCode = (Integer)getJspContext().findAttribute("javax.servlet.error.status_code");
      actualCode =(actualCode == null) ? 0 : actualCode;

      if (errorCode == actualCode) {
        // We are looking at the expected error code.
        JspFragment body = getJspBody();
        body.invoke(null);
      }
    } catch (Exception ex) {
      throw new JspException("Exception executing tag", ex);
    }
  }
}