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

import org.tiogasolutions.lib.servlet.template.PageAttribute;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PrintSubheadingTag extends SimpleTagSupport {

  @Override
  public void doTag() throws JspException {
    try {
      String value = (String)getJspContext().findAttribute(PageAttribute.subheading);
      value = (value == null) ? "" : value.trim();
      getJspContext().getOut().print(value);

    } catch (Exception ex) {
      throw new JspException("Exception executing tag", ex);
    }
  }
}