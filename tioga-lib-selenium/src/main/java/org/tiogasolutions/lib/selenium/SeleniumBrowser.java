/*
 * Copyright (c) 2010-2013, Munchie Monster, LLC.
 */
package org.tiogasolutions.lib.selenium;

import java.net.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;

public class SeleniumBrowser {

  private final WebDriver driver;
  private long defaultTimeout;

  public SeleniumBrowser(WebDriver driver) throws Exception {
    this(driver, 500, 10 * 1000);
  }

  public SeleniumBrowser(WebDriver driver, long implicitlyWait, long defaultTimeout) throws Exception {
    this.defaultTimeout = defaultTimeout;
    this.driver = driver;
    this.driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.MILLISECONDS);
  }

  public WebDriver getDriver() {
    return driver;
  }

  public WebDriver.Options manage() {
    return driver.manage();
  }
  public WebDriver.Window window() {
    return driver.manage().window();
  }

  public long getDefaultTimeout() {
    return defaultTimeout;
  }
  public void setDefaultTimeout(long defaultTimeout) {
    this.defaultTimeout = defaultTimeout;
  }

  public void quit() {
    driver.quit();
  }



  public void open(java.lang.String url) {
    driver.get(url);
  }
  public void open(URL url) {
    driver.get(url.toExternalForm());
  }
  public void open(URI uri) throws MalformedURLException {
    driver.get(uri.toURL().toExternalForm());
  }



  public WebElementReference get(By by) {
    return new WebElementReference(driver, by, defaultTimeout);
  }
  public WebElementReference get(String key) {
    return new WebElementReference(driver, key, defaultTimeout);
  }



  public void acceptAlert() {
    Alert alert = driver.switchTo().alert();
    alert.accept();
  }
  public void dismissAlert() {
    Alert alert = driver.switchTo().alert();
    alert.dismiss();
  }
}
