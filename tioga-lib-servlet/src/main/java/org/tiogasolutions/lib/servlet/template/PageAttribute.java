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

package org.tiogasolutions.lib.servlet.template;

import java.util.List;
import java.util.Arrays;

/**
 * This class identifies the attributes of a page, the most common of which would be the title
 */
public class PageAttribute {

  public static String title = "org.tiogasolutions.dev.servlet.template:title";
  public static String subheading = "org.tiogasolutions.dev.servlet.template:subheading";
  public static String altTemplate = "org.tiogasolutions.dev.servlet.template:altTemplate";

  private static final PageAttribute titleAttribute = new PageAttribute(title);
  private static final PageAttribute subheadingAttribute = new PageAttribute(subheading);
  private static final PageAttribute altTemplateAttribute = new PageAttribute(altTemplate);

  private String name;

  public PageAttribute(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    String label = getName();
    label = label.substring(0,1).toUpperCase() + label.substring(1);
    return label;
  }

  public String getSetTagName() {
    String retVal = "";
    retVal += "<"+getNameSpace()+":set-";
    retVal += getName();
    retVal += " ";
    return retVal;
  }

  public String getNameSpace() {
    return "template";
  }

  public List<PageAttribute> getPageAttributes() {
    return Arrays.asList(titleAttribute, subheadingAttribute, altTemplateAttribute);
  }
}
