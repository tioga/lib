package org.tiogasolutions.lib.joda;

import java.math.BigDecimal;
import java.util.Arrays;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.*;
import org.joda.money.CurrencyUnit;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

@Test
public class JodaTranslatorTest {

  private JsonTranslator translator;

  @BeforeMethod
  public void beforeClass() throws Exception {
    translator = new TiogaJacksonTranslator(Arrays.asList(new TiogaJacksonInjectable("injected-string", "I was injected.")));
  }

  public void translateJodaMoney() {
    CurrencyUnit currency = CurrencyUnit.USD;
    org.joda.money.Money oldValue;

    oldValue = org.joda.money.Money.of(currency, new BigDecimal("19.75"));
    String json = translator.toJson(oldValue);
    assertEquals(json, "\"19.75\"");

    oldValue = org.joda.money.Money.of(currency, new BigDecimal("0"));
    json = translator.toJson(oldValue);
    assertEquals(json, "\"0.00\"");

    oldValue = org.joda.money.Money.of(currency, new BigDecimal("1000000000000000.00"));
    json = translator.toJson(oldValue);
    assertEquals(json, "\"1000000000000000.00\"");
  }

}
