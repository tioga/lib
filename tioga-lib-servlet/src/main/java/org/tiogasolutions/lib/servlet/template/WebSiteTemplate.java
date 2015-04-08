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

import org.tiogasolutions.lib.servlet.ServletUtils;
import org.tiogasolutions.dev.common.StringUtils;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.tiogasolutions.lib.servlet.template.decorators.AnchorDecorator;
import org.tiogasolutions.lib.servlet.template.decorators.CompressionDecorator;
import org.tiogasolutions.lib.servlet.template.decorators.Decorator;
import org.tiogasolutions.lib.servlet.template.tags.PrintPageContentTag;

/**
 * This class takes the requested page and processes it through the container. The resulting
 * content is then stored in a request attribute (see {@link #PAGE_DATA} and {@link PrintPageContentTag}
 * while the request is frowarded to the specified JSP file which will then take responsibility for re-injecting
 * the page data.
 * @author Jacob D. Parr
 */
public class WebSiteTemplate implements Filter {

  public static final String ACTUAL_REQUEST_URI = "scmsActualRequestUri";
  public static final String ACTUAL_REQUEST_DIR = "scmsActualRequestDir";
  public static final String ACTUAL_QUERY_STRING = "scmsActualQueryString";

  /**
   * The parameter name for the data being edited. This data differs from PAGE_CONTENT
   * in that it is the raw content of the file, unprocessed by the Servlet Container
   */
  public static final String PAGE_DATA = "templatePageData";

  protected FilterConfig filterConfig;

  /** The file name of the template to be used. */
  protected String templateFileName;

  public WebSiteTemplate() {
  }

  @Override
  public void init(final FilterConfig newConfig) {
    filterConfig = newConfig;

    templateFileName = getServletContext().getInitParameter("templateFileName");
    templateFileName = (templateFileName == null) ? "_WebSiteTemplate.jsp" : templateFileName;
  }

  private FilterConfig getFilterConfig() {
    return filterConfig;
  }

  protected ServletContext getServletContext() {
    return getFilterConfig().getServletContext();
  }

  @Override
  public void destroy() {
    filterConfig = null;
  }

  protected String getFileName(HttpServletRequest request) {
//    return decode(request.getRequestURI());
    return request.getServletPath();
  }

  /**
   * This implementation always assumes we do not need to redirect
   * @param request The HttpServletRequest used to fetch attributes and parameters from.
   * @param response The HttpServletResponse with which redirects are sent.
   * @return true if the response was redirected in which case the {@link #doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
   * should return without futher processing.
   * @throws java.io.IOException on exception
   */
  protected boolean redirectResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return false;
  }

  /** 
   * This implementation always assumes we can apply the decorators
   * @param request The HttpServletRequest used to fetch attributes and parameters from.
   * @return true if the decorators can be applied.
   */
  protected boolean canApplyDecorators(HttpServletRequest request) {
    return true;
  }

  /**
   * The default implementation does not inject any new attributes
   * @param request The HttpServletRequest into which attributes will be injected
   * @throws IOException on exception
   */
  protected void injectAttributes(HttpServletRequest request) throws IOException {
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)resp;

    // The reuqest URI, when called from the template will not be what one would expect so we cache it.
    request.setAttribute(ACTUAL_REQUEST_URI, request.getRequestURI());
    request.setAttribute(ACTUAL_QUERY_STRING, ServletUtils.encodeUrl(request.getQueryString()));
    int pos = request.getRequestURI().lastIndexOf("/");
    pos = (pos >= 0) ? pos+=1 : request.getRequestURI().length();
    String dir = request.getRequestURI().substring(0, pos);
    request.setAttribute(ACTUAL_REQUEST_DIR, dir);

    // if we must redirect (ie the file doesn't exist but we know of another) then return here
    if (redirectResponse(request, response)) return;

    // Our wrapper will ensure that we can process the rest of the
    // chain without sending anything back down prematurely.
    CharResponseWrapper wrapper = new CharResponseWrapper(response);
    // invoke the filter
    chain.doFilter(request, wrapper);
    String data = wrapper.toString();

    String htmlHead = StringUtils.getTagContents(data, "head", 0);
    String htmlTitle = StringUtils.getTagContents(htmlHead, "title", 0);
    request.setAttribute(PageAttribute.title, htmlTitle);
    
    String htmlBody = StringUtils.getTagContents(data, "body", 0);
    if (htmlBody != null) data = htmlBody;
    
    // Before we give them the data, apply any decorators
    List<Decorator> decorators = new ArrayList<Decorator>();
    if (canApplyDecorators(request)) {
      decorators.add(new AnchorDecorator());
      decorators.add(new CompressionDecorator());
      for (Decorator decorator : decorators) {
        data = decorator.decorate(data, request, response, chain);
      }
    }

    // now add to the attributes the resulting HTML to be used by the template
    request.setAttribute(PAGE_DATA, data);
    // now inject attributes into the request
    injectAttributes(request);

    // the parent dir in which we will look for our resources
    String fileName = getFileName(request);
    File parentDir = new File(getServletContext().getRealPath(fileName)).getParentFile();

	  if (response.isCommitted() == false) {
      // We are not commited, go ahead and forward to our template.
	    // Start by checking for a named template
	    String templateFileUri = (String)request.getAttribute(PageAttribute.altTemplate);

      if (templateFileUri == null) {
        // We do not have a template, start by checking the current folder and working backwords.
        File templateFile = new File(parentDir, templateFileName);
        while (templateFile.exists() == false) {
          parentDir = parentDir.getParentFile();
          if (parentDir == null) {
            // We have no more paths to check...
            throw new ServletException("The template file "+templateFileName+" was not found.");
          }
          templateFile = new File(parentDir, templateFileName);
        }
        templateFileUri = toUri(templateFile, getServletContext());
      }

	    // Now forward to our template
      getServletContext().getRequestDispatcher(templateFileUri).forward(request, response);
	  }
  }

  private String toUri(final File file, final ServletContext context) {
    String base = context.getRealPath("/");
    String uri = file.getAbsolutePath().substring(base.length()-1);
    uri = uri.replace('\\', '/');
    return uri;
  }

  private class CharResponseWrapper extends
    HttpServletResponseWrapper {
    private CharArrayWriter output;
    @Override public String toString() {
       return output.toString();
    }
    private CharResponseWrapper(final HttpServletResponse response){
       super(response);
       output = new CharArrayWriter();
    }
    @Override public PrintWriter getWriter(){
       return new PrintWriter(output);
    }
  }
}
