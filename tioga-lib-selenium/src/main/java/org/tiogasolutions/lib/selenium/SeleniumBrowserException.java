/*
 * Copyright (c) 2010-2013, Munchie Monster, LLC.
 */

package org.tiogasolutions.lib.selenium;

import org.openqa.selenium.WebDriverException;

public class SeleniumBrowserException extends RuntimeException {

  public SeleniumBrowserException() {
  }

  public SeleniumBrowserException(java.lang.String message) {
    super(message);
  }

  public SeleniumBrowserException(String message, Throwable cause) {
    super(message, cause);
  }

  public SeleniumBrowserException(WebDriverException cause) {
    super(parseMsg(cause), cause);
  }

  public SeleniumBrowserException(InterruptedException e) {
    super(e);
  }

  private static String parseMsg(WebDriverException e) {
    String msg = e.getMessage();
    int pos = msg.indexOf("\n");
    msg = msg.substring(0, pos);
    return msg.trim();
  }
}
