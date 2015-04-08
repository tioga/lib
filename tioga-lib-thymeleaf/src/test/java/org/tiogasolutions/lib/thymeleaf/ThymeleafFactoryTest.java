package org.tiogasolutions.lib.thymeleaf;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import java.io.File;

import static org.testng.Assert.assertEquals;

@Test
public class ThymeleafFactoryTest {

  private ThymeleafFactory thymeleafFactory;
  private Context context;

  @BeforeClass
  public void beforeClass() throws Exception {
    thymeleafFactory = new ThymeleafFactory();
    context = new Context();
    context.setVariable("it", new Model());
  }

  public void testResolveClassPath() throws Exception {
    String path = "classpath:/yak-lib-thymeleaf/test-template.html";
    String html = thymeleafFactory.process(path, context);
    assertEquals(html, EXPECTED_HTML);
  }

  public void testResolveFile() throws Exception {
    File file = new File("").getAbsoluteFile();

    if (file.getAbsolutePath().endsWith("tioga-lib-thymeleaf")) {
      file = new File(file, "/src/test/resources/yak-lib-thymeleaf/test-template.html");
    } else {
      file = new File(file, "/tioga-lib-thymeleaf/src/test/resources/yak-lib-thymeleaf/test-template.html");
    }

    String path = "file:" + file.getAbsolutePath();
    String html = thymeleafFactory.process(path, context);
    assertEquals(html, EXPECTED_HTML);
  }

  public void testResolveUrl() throws Exception {
    String path = "https://raw.githubusercontent.com/liquid-notifications/lqnotify/master/yak-lib-thymeleaf/src/test/resources/yak-lib-thymeleaf/test-template.html";
    String html = thymeleafFactory.process(path, context);
    assertEquals(html, EXPECTED_HTML);
  }

  private static class Model {
    private final int size = 99;
    private final String color = "blue";
    public int getSize() { return size; }
    public String getColor() { return color; }
  }

  public static final String EXPECTED_HTML =
      "<html>\n" +
      "  <head>\n" +
      "    <title>Hello World</title>\n" +
      "  </head>\n" +
      "  <body>\n" +
      "    <h1>Hello World</h1>\n" +
      "    <div>\n" +
      "      My favorite color is <span>blue</span>\n" +
      "      and my hat size is <span>99</span>.</div>\n" +
      "  </body>\n" +
      "</html>";
}