package org.tiogasolutions.lib.selenium;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class WebElementReference {

  private final By by;
  private final long defaultTimeout;
  private final WebDriver webDriver;
  private final SearchContext searchContext;

  public WebElementReference(WebDriver webDriver, By by, long defaultTimeout) {
    this(webDriver, webDriver, by, defaultTimeout);
  }

  public WebElementReference(WebDriver webDriver, String key, long defaultTimeout) {
    this(webDriver, webDriver, key, defaultTimeout);
  }

  public WebElementReference(WebDriver webDriver, SearchContext searchContext, By by, long defaultTimeout) {
    this.by = by;
    this.webDriver = webDriver;
    this.searchContext = searchContext;
    this.defaultTimeout = defaultTimeout;
  }

  public WebElementReference(WebDriver webDriver, SearchContext searchContext, String key, long defaultTimeout) {
    this.webDriver = webDriver;
    this.searchContext = searchContext;
    this.defaultTimeout = defaultTimeout;

    if (key.startsWith("#")) {
      key = key.substring(1);
      this.by = By.id(key);

    } else if (key.startsWith(".")) {
      key = key.substring(1);
      this.by = By.className(key);

    } else {
      String msg = String.format("Cannot get element for key \"%s\".", key);
      throw new SeleniumBrowserException(msg);
    }

  }



  private void sleep(int duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      throw new SeleniumBrowserException(e);
    }
  }

  
  
  public boolean isPresent() {
    return searchContext.findElements(by).isEmpty() == false;
  }
  public boolean isNotPresent() {
    return searchContext.findElements(by).isEmpty();
  }

  
  
  public Point getCenter() throws SeleniumBrowserException {
    return getCenter(defaultTimeout);
  }
  public Point getCenter(long timeout) throws SeleniumBrowserException {
    WebElement element = waitFor(timeout);
    Point location = element.getLocation();
    Dimension dimension = element.getSize();

    location.x = location.x + (int)(dimension.width/2d);
    location.y = location.y + (int)(dimension.height/2d);

    return location;
  }

  
  
  public WebElementReference click() throws SeleniumBrowserException {
    return click(defaultTimeout);
  }
  public WebElementReference click(long timeout) throws SeleniumBrowserException {
    try {
      waitFor(timeout).click();
      return this;

    } catch (WebDriverException e) {
      String msg = String.format("Unexpected exception clicking %s with %s ms timeout.", by, defaultTimeout);
      throw new SeleniumBrowserException(msg, e);
    }
  }

  
  
  public WebElementReference submit() {
    return submit(defaultTimeout);
  }
  public WebElementReference submit(long timeout) {
    waitFor(timeout).submit();
    return this;
  }

  
  
  public WebElementReference sendKeys(CharSequence text) throws SeleniumBrowserException {
    return sendKeys(text, defaultTimeout);
  }
  public WebElementReference sendKeys(CharSequence text, long timeout) throws SeleniumBrowserException {
    try {
      waitFor(timeout).sendKeys(text);
      return this;

    } catch (WebDriverException e) {
      String msg = String.format("Unexpected exception sending keys to \"%s\" with %s ms timeout.", by, defaultTimeout);
      throw new SeleniumBrowserException(msg, e);
    }
  }

  
  
  public WebElementReference clear() throws SeleniumBrowserException {
    return clear(defaultTimeout);
  }
  public WebElementReference clear(long timeout) throws SeleniumBrowserException {
    try {
      waitFor(timeout).clear();
      return this;

    } catch (WebDriverException e) {
      String msg = String.format("Unexpected exception clearing \"%s\" with %s ms timeout.", by, defaultTimeout);
      throw new SeleniumBrowserException(msg, e);
    }
  }

  

  public String getTagName() {
    return getTagName(defaultTimeout);
  }
  public String getTagName(long timeout) {
    return waitFor(timeout).getTagName();
  }

  
  
  public String getAttribute(String name) {
    return getAttribute(name, defaultTimeout);
  }
  public String getAttribute(String name, long timeout) {
    return waitFor(timeout).getAttribute(name);
  }

  
  
  public boolean isSelected() {
    return isSelected(defaultTimeout);
  }
  public boolean isSelected(long timeout) {
    return waitFor(timeout).isSelected();
  }

  
  
  public boolean isEnabled() {
    return isEnabled(defaultTimeout);
  }
  public boolean isEnabled(long timeout) {
    return waitFor(timeout).isEnabled();
  }

  
  
  public boolean isDisplayed() {
    return isDisplayed(defaultTimeout);
  }
  public boolean isDisplayed(long timeout) {
    return waitFor(timeout).isDisplayed();
  }

  
  
  public Point getLocation() {
    return getLocation(defaultTimeout);
  }
  public Point getLocation(long timeout) {
    return waitFor(timeout).getLocation();
  }

  
  
  public Dimension getSize() {
    return getSize(defaultTimeout);
  }
  public Dimension getSize(long timeout) {
    return waitFor(timeout).getSize();
  }

  
  
  public String getCssValue(String propertyName) {
    return getCssValue(propertyName, defaultTimeout);
  }
  public String getCssValue(String propertyName, long timeout) throws SeleniumBrowserException {
    return waitFor(timeout).getCssValue(propertyName);
  }

  
  
  public WebElementReference moveTo() throws SeleniumBrowserException {
    return moveTo(defaultTimeout);
  }
  public WebElementReference moveTo(long timeout) throws SeleniumBrowserException {
    try {
      new Actions(webDriver).moveToElement(waitFor(timeout));
      return this;

    } catch (WebDriverException e) {
      String msg = String.format("Unexpected exception moving to \"%s\" with %s ms timeout.", by, defaultTimeout);
      throw new SeleniumBrowserException(msg, e);
    }
  }
  
  

  public String getText() throws SeleniumBrowserException {
    return getText(defaultTimeout);
  }
  public String getText(long timeout) throws SeleniumBrowserException {
    try {
      return waitFor(timeout).getText();

    } catch (WebDriverException e) {
      String msg = String.format("Unexpected exception getting text for %s with %s ms timeout.", by, defaultTimeout);
      throw new SeleniumBrowserException(msg, e);
    }
  }

  
  
  public WebElementReference waitForText(String message) throws SeleniumBrowserException {
    return waitForText(message, defaultTimeout);
  }
  public WebElementReference waitForText(String message, long timeout) throws SeleniumBrowserException {
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < timeout) {

      long elapsed = System.currentTimeMillis() - start;
      long remaining = timeout - elapsed;

      String text = waitFor(remaining).getText();
      if (text.equals(message)) {
        return this;
      }

      sleep(250);

    }
    String msg = String.format("The element \"%s\" did not have a value of \"%s\" after %s milliseconds.", by, message, timeout);
    throw new SeleniumBrowserException(msg);
  }



  public WebElement getWebElement() {
    return searchContext.findElement(by);
  }
  public List<WebElement> getWebElements() {
    return searchContext.findElements(by);
  }



  public WebElementReference waitToAppear() throws SeleniumBrowserException {
    return waitToAppear(defaultTimeout);
  }
  public WebElementReference waitToAppear(long timeout) throws SeleniumBrowserException {
    waitFor(timeout);
    return this;
  }

  private WebElement waitFor(long timeout) {

    long start = System.currentTimeMillis();
    List<WebElement> elements = new ArrayList<>();

    while (System.currentTimeMillis() - start < timeout) {
      elements = searchContext.findElements(by);

      if (elements.isEmpty()) {
        sleep(250);
      } else if (elements.get(0).isDisplayed() == false) {
        sleep(250);

      } else {
        return elements.get(0);
      }
    }

    if (elements.isEmpty()) {
      String msg = String.format("The element \"%s\" was not found after %s milliseconds.", by, timeout);
      throw new SeleniumBrowserException(msg);

    } else {
      String msg = String.format("The element \"%s\" was found but is not displayed after %s milliseconds.", by, timeout);
      throw new SeleniumBrowserException(msg);
    }
  }



  public void waitToDisappear() throws SeleniumBrowserException {
    waitToDisappear(defaultTimeout);
  }
  public void waitToDisappear(long timeout) throws SeleniumBrowserException {

    long start = System.currentTimeMillis();
    List<WebElement> elements = new ArrayList<>();

    while (System.currentTimeMillis() - start < timeout) {
      elements = searchContext.findElements(by);
      if (elements.isEmpty()) {
        return; // doesn't exist...
      }

      int visible = 0;
      for (WebElement element : elements) {
        try {
          if (element.isDisplayed()) {
            visible++;
          }
        } catch (StaleElementReferenceException ignored) {
          // the element exists, but is not attached
        }
      }

      if (visible == 0) {
        return; // No displayed elements
      } else {
        sleep(250); // It's here and displayed.
      }
    }

    if (elements.isEmpty() == false) {
      String msg = String.format("The element \"%s\" was found after %s milliseconds.", by, timeout);
      throw new SeleniumBrowserException(msg);
    }
  }






  public WebElementReference isMultiSelect() {
    return isMultiSelect(defaultTimeout);
  }
  public WebElementReference isMultiSelect(long timeout) {
    new Select(waitFor(timeout)).isMultiple();
    return this;
  }



  public List<WebElement> getSelectOptions() {
    return getSelectOptions(defaultTimeout);
  }
  public List<WebElement> getSelectOptions(long timeout) {
    return new Select(waitFor(timeout)).getOptions();
  }



  public List<WebElement> getAllSelectedOptions() {
    return getAllSelectedOptions(defaultTimeout);
  }
  public List<WebElement> getAllSelectedOptions(long timeout) {
    return new Select(waitFor(timeout)).getAllSelectedOptions();
  }



  public WebElement getFirstSelectedOption() {
    return getFirstSelectedOption(defaultTimeout);
  }
  public WebElement getFirstSelectedOption(long timeout) {
    return new Select(waitFor(timeout)).getFirstSelectedOption();
  }



  public WebElementReference selectByVisibleText(String text) {
    return selectByVisibleText(text, defaultTimeout);
  }
  public WebElementReference selectByVisibleText(String text, long timeout) {
    new Select(waitFor(timeout)).selectByVisibleText(text);
    return this;
  }



  public WebElementReference selectByIndex(int index) {
    return selectByIndex(index, defaultTimeout);
  }
  public WebElementReference selectByIndex(int index, long timeout) {
    new Select(waitFor(timeout)).selectByIndex(index);
    return this;
  }



  public WebElementReference selectByValue(String value) {
    return selectByValue(value, defaultTimeout);
  }
  public WebElementReference selectByValue(String value, long timeout) {
    new Select(waitFor(timeout)).selectByValue(value);
    return this;
  }



  public WebElementReference deselectAll() {
    return deselectAll(defaultTimeout);
  }
  public WebElementReference deselectAll(long timeout) {
    new Select(waitFor(timeout)).deselectAll();
    return this;
  }



  public WebElementReference deselectByValue(String value) {
    return deselectByValue(value, defaultTimeout);
  }
  public WebElementReference deselectByValue(String value, long timeout) {
    new Select(waitFor(timeout)).deselectByValue(value);
    return this;
  }



  public WebElementReference deselectByIndex(int index) {
    return deselectByIndex(index, defaultTimeout);
  }
  public WebElementReference deselectByIndex(int index, long timeout) {
    new Select(waitFor(timeout)).deselectByIndex(index);
    return this;
  }



  public WebElementReference deselectByVisibleText(String text) {
    return deselectByVisibleText(text, defaultTimeout);
  }
  public WebElementReference deselectByVisibleText(String text, long timeout) {
    new Select(waitFor(timeout)).deselectByVisibleText(text);
    return this;
  }
}
