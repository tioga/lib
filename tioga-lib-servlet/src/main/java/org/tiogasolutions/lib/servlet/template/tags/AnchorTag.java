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

import org.tiogasolutions.dev.common.StringUtils;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.tiogasolutions.lib.servlet.template.decorators.Anchor;
import org.tiogasolutions.lib.servlet.template.decorators.AnchorDecorator;

public class AnchorTag extends SimpleTagSupport {

  private String before;
  private String after;
  private String each;

  @Override
  public void doTag() throws JspException {
    Object disabled = this.getJspContext().getAttribute(AnchorDecorator.ANCHORS_DISABLED, PageContext.REQUEST_SCOPE);
    if ("TRUE".equalsIgnoreCase(StringUtils.toString(disabled))) {
      return;
    }

    try {
      Anchor[] anchors = (Anchor[])getJspContext().findAttribute(AnchorDecorator.ANCHORS);
      anchors = (anchors == null) ? new Anchor[0] : anchors;
      if (anchors.length > 0) {
        getJspContext().getOut().print(before);
      }
      for (Anchor anchor : anchors) {
        String out = each;
        out = out.replaceAll("anchor.name", anchor.getName());
        out = out.replaceAll("anchor.title", anchor.getTitle());
        getJspContext().getOut().print(out);
      }
      if (anchors.length > 0) {
        getJspContext().getOut().print(after);
      }
    } catch (Exception ex) {
      throw new JspException("Exception executing tag", ex);
    }
  }

  public void setBefore(String value) {
    before = value;
  }

  public void setAfter(String value) {
    after = value;
  }

  public void setEach(String value) {
    each = value;
  }
}
