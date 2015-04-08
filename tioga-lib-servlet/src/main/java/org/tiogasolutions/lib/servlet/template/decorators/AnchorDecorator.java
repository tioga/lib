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

package org.tiogasolutions.lib.servlet.template.decorators;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This class process the data returned to the filter and adds to the request an attribute
 * that contains all the anchors in the current document
 */
public class AnchorDecorator implements Decorator {

  /** The parameter name for the anchors found in the document */
  public static final String ANCHORS = "templateAnchors";
  public static final String ANCHORS_DISABLED = "templateAnchorsDisabled";

  @Override
  public String decorate(final String data, final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
    List<Anchor> anchors = new ArrayList<Anchor>();

    boolean searching = true;
    int posA = data.indexOf("<a name");

    while (searching) {
      if (posA == -1) {
        searching = false;
      } else {
        int posB = data.indexOf(">", posA);
        if (posB == -1) {
          searching = false;
        } else {
          String tag = data.substring(posA, posB);
          Anchor anchor = new Anchor(tag);
          anchors.add(anchor);
          posA = data.indexOf("<a name", posB);
        }
      }
    }

    request.setAttribute(ANCHORS, anchors.toArray(new Anchor[anchors.size()]));

    return data;
  }
}
