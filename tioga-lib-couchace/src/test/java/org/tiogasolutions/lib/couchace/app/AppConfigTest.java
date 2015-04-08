package org.tiogasolutions.lib.couchace.app;

import org.tiogasolutions.dev.common.ComparisonResults;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class AppConfigTest  {

  private final AppConfig appConfig = new AppConfig("sample-config", "lw3kjo13485ndc7034758d2j38945d7",
      new String[]{"test=true", "color=red", "size=small"});

  public void testAccessorMethods() throws Exception {
    assertEquals(appConfig.getAppConfigId(), "sample-config");
    assertEquals(appConfig.getRevision(), "lw3kjo13485ndc7034758d2j38945d7");
    assertEquals(appConfig.getPropertyString(), "test=true\n" +
        "color=red\n" +
        "size=small\n");

    assertEquals(appConfig.getProperties(), new String[]{"test=true","color=red","size=small"});

    assertEquals(appConfig.getProperty("test"), "true");
    assertEquals(appConfig.getProperty("color"), "red");
    assertEquals(appConfig.getProperty("size"), "small");
  }

  public void translate() throws Exception {

    JsonTranslator translator= new TiogaJacksonTranslator();

    String json = translator.toJson(appConfig);
    assertEquals(json, "{\n" +
        "  \"appConfigId\" : \"sample-config\",\n" +
        "  \"revision\" : \"lw3kjo13485ndc7034758d2j38945d7\",\n" +
        "  \"properties\" : [ \"test=true\", \"color=red\", \"size=small\" ]\n" +
        "}");

    AppConfig newValue = translator.fromJson(AppConfig.class, json);
    ComparisonResults results = EqualsUtils.compare(newValue, appConfig);
    results.assertValidationComplete();
  }
}