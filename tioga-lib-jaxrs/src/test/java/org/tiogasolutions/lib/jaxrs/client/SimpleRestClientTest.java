package org.tiogasolutions.lib.jaxrs.client;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import org.tiogasolutions.dev.common.*;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.fine.*;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.locality.LatLng;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.dev.testing.domain.FreeBird;
import org.glassfish.grizzly.http.server.HttpServer;
import org.testng.Assert;
import org.testng.annotations.*;
import org.tiogasolutions.lib.jaxrs.providers.FreeBirdRestServer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@Test
public class SimpleRestClientTest {

  private HttpServer server;
  private SimpleRestClient client;
  private JsonTranslator translator = new TiogaJacksonTranslator();

  @BeforeClass
  public void beforeClass() {
    server = FreeBirdRestServer.startServer();
    client = new SimpleRestClient(translator, FreeBirdRestServer.API_URI.toString(), "free", "bird");
  }

  @AfterClass
  public void afterClass() {
    server.shutdownNow();
  }

  public void testPostUrl() throws Exception {
    // Because this variant does not return an actual value, (or even
    // take a value) all we can hope for is to not throw an exception.
    client.post("/null-bird");
  }

  public void testPostUrlEntity() throws Exception {
    // Because this variant does not return an actual value,
    // all we can hope for is to not throw an exception.
    FreeBird oldBird = createFreeBird();
    client.post("/free-bird", oldBird);
  }

  public void testPostUrlWithReturn() throws Exception {
    FreeBird newBird = client.post(FreeBird.class, "/null-bird");
    validFreeBird(newBird, "This is a null bird.");
  }

  public void testPostUrlEntityWithReturn() throws Exception {
    FreeBird oldBird = createFreeBird();
    FreeBird newBird = client.post(FreeBird.class, "/free-bird", oldBird);
    validatePostedBird(oldBird, newBird);
  }

  private FreeBird createFreeBird() {
    return new FreeBird(
        "this-value", Long.MIN_VALUE, Integer.MAX_VALUE,
        new LatLng("36.7364290", "-119.7172487"),
        new org.tiogasolutions.dev.domain.money.Money("119.95"),

        java.time.LocalTime.parse("12:32:13.333"),
        java.time.LocalDate.parse("1974-09-03"),
        java.time.LocalDateTime.parse("1975-05-06T12:32:13.333"),
        java.time.ZonedDateTime.parse("1975-05-06T12:32:13.333-07:00[America/Los_Angeles]"),

        new TraitMap("first:no", "empty-value:", "null-value", "last:yes"),
        FineMessage.withText("I hate testing!"),
        (FineMessageSetImpl) new FineMessageSetBuilder()
            .withText("Are we done yet.")
            .withAll("I'm Billy", "333", new TraitMap("boy:true", "girl:false")).build());
  }

  private void validatePostedBird(FreeBird oldBird, FreeBird newBird) {
    assertNotNull(newBird);
    Assert.assertEquals(newBird, oldBird);

    Assert.assertEquals(newBird.getInjected(), oldBird.getInjected());
    Assert.assertEquals(newBird.getMissing(), oldBird.getMissing());
    Assert.assertEquals(newBird.getStringValue(), oldBird.getStringValue());
    Assert.assertEquals(newBird.getLongValue(), oldBird.getLongValue());
    Assert.assertEquals(newBird.getIntValue(), oldBird.getIntValue());
    Assert.assertEquals(newBird.getLatLng(), oldBird.getLatLng());
    Assert.assertEquals(newBird.getTiogaMoney(), oldBird.getTiogaMoney());

    Assert.assertEquals(newBird.getJavaLocalTime(), oldBird.getJavaLocalTime());
    Assert.assertEquals(newBird.getJavaLocalDate(), oldBird.getJavaLocalDate());
    Assert.assertEquals(newBird.getJavaLocalDateTime(), oldBird.getJavaLocalDateTime());
    Assert.assertEquals(newBird.getJavaZonedDateTime(), oldBird.getJavaZonedDateTime());

    Assert.assertEquals(newBird.getTraitMap(), oldBird.getTraitMap());
    Assert.assertEquals(newBird.getFineMessage(), oldBird.getFineMessage());
    Assert.assertEquals(newBird.getMessageSet(), oldBird.getMessageSet());
  }

  public void testGetQueryString() throws Exception {
    Map<String,Object> query = new HashMap<>();
    query.put("firstMessage", "This is my first message.");

    FreeBird freeBird = client.get(FreeBird.class, "/free-bird", query);
    validFreeBird(freeBird, "This is my first message.");
  }

  public void testGetQueryMap() throws Exception {
    Map<String,Object> query = new HashMap<>();
    query.put("firstMessage", "What do you think of this?");

    FreeBird freeBird = client.get(FreeBird.class, "/free-bird", query);
    validFreeBird(freeBird, "What do you think of this?");
  }

  public void testGetListOfDates() throws Exception {
    try {
      client.get(LocalDate.class, "/date/2014-12-13/dates-in-month");
      fail("Expected exception.");
    } catch (Exception e) {
      assertEquals(e.getMessage(), "Exception translating java.time.LocalDate from json.");
    }

    List<LocalDate> result = client.getList(LocalDate.class, "/date/2014-12-13/dates-in-month", SimpleRestClient.EMPTY_QUERY);
    List<LocalDate> dates = new ArrayList<>(result);
    assertNotNull(dates);

    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-01")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-02")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-03")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-04")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-05")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-06")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-07")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-08")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-09")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-10")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-11")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-12")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-13")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-14")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-15")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-16")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-17")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-18")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-19")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-20")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-21")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-22")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-23")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-24")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-25")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-26")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-27")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-28")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-29")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-30")));
    assertTrue(dates.remove(DateUtils.toLocalDate("2014-12-31")));

    assertEquals(dates.size(), 0);
  }

  public void testGetListOfStrings() throws Exception {

    List<String> result = client.getList(String.class, "/date/2014-12-13/dates-in-month", SimpleRestClient.EMPTY_QUERY);
    List<String> dates = new ArrayList<>(result);
    assertNotNull(dates);

    assertTrue(dates.remove("2014-12-01"));
    assertTrue(dates.remove("2014-12-02"));
    assertTrue(dates.remove("2014-12-03"));
    assertTrue(dates.remove("2014-12-04"));
    assertTrue(dates.remove("2014-12-05"));
    assertTrue(dates.remove("2014-12-06"));
    assertTrue(dates.remove("2014-12-07"));
    assertTrue(dates.remove("2014-12-08"));
    assertTrue(dates.remove("2014-12-09"));
    assertTrue(dates.remove("2014-12-10"));
    assertTrue(dates.remove("2014-12-11"));
    assertTrue(dates.remove("2014-12-12"));
    assertTrue(dates.remove("2014-12-13"));
    assertTrue(dates.remove("2014-12-14"));
    assertTrue(dates.remove("2014-12-15"));
    assertTrue(dates.remove("2014-12-16"));
    assertTrue(dates.remove("2014-12-17"));
    assertTrue(dates.remove("2014-12-18"));
    assertTrue(dates.remove("2014-12-19"));
    assertTrue(dates.remove("2014-12-20"));
    assertTrue(dates.remove("2014-12-21"));
    assertTrue(dates.remove("2014-12-22"));
    assertTrue(dates.remove("2014-12-23"));
    assertTrue(dates.remove("2014-12-24"));
    assertTrue(dates.remove("2014-12-25"));
    assertTrue(dates.remove("2014-12-26"));
    assertTrue(dates.remove("2014-12-27"));
    assertTrue(dates.remove("2014-12-28"));
    assertTrue(dates.remove("2014-12-29"));
    assertTrue(dates.remove("2014-12-30"));
    assertTrue(dates.remove("2014-12-31"));

    assertEquals(dates.size(), 0);
  }

  public void testGetBytes() throws Exception {
    InputStream in = getClass().getResourceAsStream("/tioga-lib-jaxrs-jackson/sample.pdf");
    assertNotNull(in);
    byte[] testBytes = IoUtils.toBytes(in);
    byte[] pdfBytes = client.getBytes("/sample.pdf", SimpleRestClient.EMPTY_QUERY, "application/pdf");
    assertEquals(pdfBytes, testBytes);
  }

  public void testGetQueryContent() throws Exception {

    FreeBird freeBird = client.get(FreeBird.class, "/free-bird", SimpleRestClient.EMPTY_QUERY, "application/json");
    validFreeBird(freeBird, "Hi, my name is Moe!");


    try {
      client.get(FreeBird.class, "/free-bird", SimpleRestClient.EMPTY_QUERY, "text/plain");
      fail("Expected exception.");
    } catch (ApiException e) {
      assertEquals(e.getStatusCode(), 406);
      assertEquals(e.getHttpStatusCode(), HttpStatusCode.NOT_ACCEPTABLE);
      assertEquals(e.getMessage(), "Unexpected response: 406 Not Acceptable");
    }


    Map<String,Object> query = new HashMap<>();
    query.put("signature", "- Micky Mouse");
    String plainText = client.get(String.class, "/plain-text", query, "text/plain");
    assertEquals(plainText, "" +
        "This is plain text.\n" +
        "There really isn't much to it.\n" +
        "\n" +
        "\t- Micky Mouse");

    String json = client.get(String.class, "/free-bird", SimpleRestClient.EMPTY_QUERY, "application/json");
    assertEquals(json, "{\n" +
        "  \"stringValue\" : \"string-value\",\n" +
        "  \"longValue\" : 9223372036854775807,\n" +
        "  \"intValue\" : -2147483648,\n" +
        "  \"latLng\" : {\n" +
        "    \"latitude\" : \"37.3382030\",\n" +
        "    \"longitude\" : \"-119.7085060\",\n" +
        "    \"city\" : null,\n" +
        "    \"state\" : null,\n" +
        "    \"country\" : null\n" +
        "  },\n" +
        "  \"tiogaMoney\" : \"19.95\",\n" +

        "  \"javaLocalTime\" : \"12:32:13.111\",\n" +
        "  \"javaLocalDate\" : \"1974-09-03\",\n" +
        "  \"javaLocalDateTime\" : \"1975-05-06T10:32:13.222\",\n" +
        "  \"javaZonedDateTime\" : \"1997-07-11T01:32:13.333-07:00[America/Los_Angeles]\",\n" +

        "  \"traitMap\" : {\n" +
        "    \"empty-value\" : \"\",\n" +
        "    \"first\" : \"yes\",\n" +
        "    \"last\" : \"ok\",\n" +
        "    \"null-value\" : null\n" +
        "  },\n" +
        "  \"fineMessage\" : {\n" +
        "    \"text\" : \"This message is fine!\",\n" +
        "    \"id\" : null,\n" +
        "    \"traitMap\" : { }\n" +
        "  },\n" +
        "  \"messageSet\" : {\n" +
        "    \"messages\" : [ {\n" +
        "      \"text\" : \"Hi, my name is Moe!\",\n" +
        "      \"id\" : null,\n" +
        "      \"traitMap\" : { }\n" +
        "    }, {\n" +
        "      \"text\" : \"I'm Suzie\",\n" +
        "      \"id\" : \"999\",\n" +
        "      \"traitMap\" : {\n" +
        "        \"boy\" : \"false\",\n" +
        "        \"girl\" : \"true\"\n" +
        "      }\n" +
        "    } ]\n" +
        "  },\n" +
        "  \"injected\" : null,\n" +
        "  \"missing\" : null\n" +
        "}");
  }

  private void validFreeBird(FreeBird freeBird, final String FIRST_MESSAGE) {
    assertNotNull(freeBird);

    Assert.assertEquals(freeBird.getStringValue(), "string-value");
    Assert.assertEquals(freeBird.getLongValue(), Long.MAX_VALUE);
    Assert.assertEquals(freeBird.getIntValue(), Integer.MIN_VALUE);
    Assert.assertEquals(freeBird.getLatLng(), new LatLng("37.3382030", "-119.7085060"));

    Assert.assertEquals(freeBird.getTiogaMoney(), new org.tiogasolutions.dev.domain.money.Money("19.95"));

    Assert.assertEquals(freeBird.getJavaLocalTime(), java.time.LocalTime.parse("12:32:13.111"));
    Assert.assertEquals(freeBird.getJavaLocalDate(), java.time.LocalDate.parse("1974-09-03"));
    Assert.assertEquals(freeBird.getJavaLocalDateTime(), java.time.LocalDateTime.parse("1975-05-06T10:32:13.222"));
    Assert.assertEquals(freeBird.getJavaZonedDateTime(), java.time.ZonedDateTime.parse("1997-07-11T01:32:13.333-07:00[America/Los_Angeles]"));

    TraitMap traitMap = freeBird.getTraitMap();
    assertNotNull(traitMap);
    assertEquals(traitMap.getValue("first"), "yes");
    assertEquals(traitMap.getValue("empty-value"), "");
    assertEquals(traitMap.getValue("null-value"), null);
    assertEquals(traitMap.getValue("last"), "ok");
    assertEquals(traitMap.getSize(), 4);

    FineMessage fineMessage = freeBird.getFineMessage();
    assertNotNull(fineMessage);
    assertEquals(fineMessage.getText(), "This message is fine!");
    assertEquals(fineMessage.getId(), null);
    traitMap = fineMessage.getTraitMap();
    assertNotNull(traitMap);
    assertTrue(fineMessage.getTraitMap().isEmpty());

    FineMessageSet messageSet = freeBird.getMessageSet();
    assertNotNull(messageSet);
    assertEquals(messageSet.size(), 2);

    fineMessage = messageSet.getMessages().get(0);
    assertEquals(fineMessage.getText(), FIRST_MESSAGE);
    assertEquals(fineMessage.getId(), null);
    traitMap = fineMessage.getTraitMap();
    assertNotNull(traitMap);
    assertTrue(fineMessage.getTraitMap().isEmpty());

    fineMessage = messageSet.getMessages().get(1);
    assertEquals(fineMessage.getText(), "I'm Suzie");
    assertEquals(fineMessage.getId(), "999");
    traitMap = fineMessage.getTraitMap();
    assertNotNull(traitMap);
    traitMap = fineMessage.getTraitMap();
    assertEquals(traitMap.getValue("boy"), "false");
    assertEquals(traitMap.getValue("GIRL"), "true");
  }

  public void testGetApiUrl() throws Exception {
    assertEquals(client.getApiUrl(), FreeBirdRestServer.API_URI.toString());
  }

  public void testGetUserName() throws Exception {
    assertEquals(client.getUsername(), "free");
  }

  public void testGetPassword() throws Exception {
    assertEquals(client.getPassword(), "bird");
  }

  public void testAssertResponse() throws Exception {

    SimpleRestClient client = new SimpleRestClient(null, null, null, null);

    client.assertResponse(200);

    validateResponseException(client, 400, "Unexpected response: 400 Bad Request");
    validateResponseException(client, 404, "Unexpected response: 404 Not Found");
    validateResponseException(client, 500, "Unexpected response: 500 Internal Server Error");
    validateResponseException(client, 201, "Unexpected response: 201 Created");
  }

  private void validateResponseException(SimpleRestClient client, int code, String message) {
    try {
      client.assertResponse(code);
    } catch (Exception e) {
      assertEquals(e.getMessage(), message);
    }
  }

  public void testToMap() throws Exception {

    SimpleRestClient client = new SimpleRestClient(null, null, null, null);

    Map map = client.toMap();
    assertEquals(map.size(), 0);

    String[] nullArray = null;
    // noinspection ConstantConditions
    map = client.toMap(nullArray);
    assertEquals(map.size(), 0);

    map = client.toMap("red=ff0000", "yellow", "green=", null, "blue=0000ff");
    validateMapEntry(map, "red", "ff0000");
    validateMapEntry(map, "yellow", null);
    validateMapEntry(map, "green", "");
    validateMapEntry(map, null, null);
    validateMapEntry(map, "blue", "0000ff");
  }

  private void validateMapEntry(Map map, String key, String value) {
    assertTrue(map.containsKey(key));
    assertEquals(map.get(key), value);
  }

  public void testParseUrl() throws Exception {
    assertEquals(SimpleRestClient.parseUrl(null),                                         null);
    assertEquals(SimpleRestClient.parseUrl(""),                                           null);
    assertEquals(SimpleRestClient.parseUrl("http://google.com"),                          "http://google.com");
    assertEquals(SimpleRestClient.parseUrl("http://google.com/test"),                     "http://google.com/test");
    assertEquals(SimpleRestClient.parseUrl("https://google.com"),                         "https://google.com");
    assertEquals(SimpleRestClient.parseUrl("https://google.com/test"),                    "https://google.com/test");
    assertEquals(SimpleRestClient.parseUrl("http://username@google.com"),                 "http://google.com");
    assertEquals(SimpleRestClient.parseUrl("http://username@google.com/test"),            "http://google.com/test");
    assertEquals(SimpleRestClient.parseUrl("https://username@google.com"),                "https://google.com");
    assertEquals(SimpleRestClient.parseUrl("https://username@google.com/test"),           "https://google.com/test");
    assertEquals(SimpleRestClient.parseUrl("http://username:password@google.com"),        "http://google.com");
    assertEquals(SimpleRestClient.parseUrl("http://username:password@google.com/test"),   "http://google.com/test");
    assertEquals(SimpleRestClient.parseUrl("https://username:password@google.com"),       "https://google.com");
    assertEquals(SimpleRestClient.parseUrl("https://username:password@google.com/test"),  "https://google.com/test");
  }

  public void testParseAuth() throws Exception {
    assertEquals(SimpleRestClient.parseAuth(null),                                          null);
    assertEquals(SimpleRestClient.parseAuth(""),                                            null);
    assertEquals(SimpleRestClient.parseAuth("http://google.com"),                          null);
    assertEquals(SimpleRestClient.parseAuth("http://google.com/test"),                     null);
    assertEquals(SimpleRestClient.parseAuth("https://google.com"),                         null);
    assertEquals(SimpleRestClient.parseAuth("https://google.com/test"),                    null);
    assertEquals(SimpleRestClient.parseAuth("http://username@google.com"),                 "username");
    assertEquals(SimpleRestClient.parseAuth("http://username@google.com/test"),            "username");
    assertEquals(SimpleRestClient.parseAuth("https://username@google.com"),                "username");
    assertEquals(SimpleRestClient.parseAuth("https://username@google.com/test"),           "username");
    assertEquals(SimpleRestClient.parseAuth("http://username:password@google.com"),        "username:password");
    assertEquals(SimpleRestClient.parseAuth("http://username:password@google.com/test"),   "username:password");
    assertEquals(SimpleRestClient.parseAuth("https://username:password@google.com"),       "username:password");
    assertEquals(SimpleRestClient.parseAuth("https://username:password@google.com/test"),  "username:password");
  }

  public void parseUserFromAuth() throws Exception {
    assertEquals(SimpleRestClient.parseUserFromAuth(null),                null);
    assertEquals(SimpleRestClient.parseUserFromAuth(""),                  null);
    assertEquals(SimpleRestClient.parseUserFromAuth(":"),                 null);
    assertEquals(SimpleRestClient.parseUserFromAuth("username"),          "username");
    assertEquals(SimpleRestClient.parseUserFromAuth("username:"),         "username");
    assertEquals(SimpleRestClient.parseUserFromAuth("username:password"), "username");
    assertEquals(SimpleRestClient.parseUserFromAuth(":password"),         null);
  }

  public void parsePassFromAuth() throws Exception {
    assertEquals(SimpleRestClient.parsePassFromAuth(null),                null);
    assertEquals(SimpleRestClient.parsePassFromAuth(""),                  null);
    assertEquals(SimpleRestClient.parsePassFromAuth(":"),                 null);
    assertEquals(SimpleRestClient.parsePassFromAuth("username"),          null);
    assertEquals(SimpleRestClient.parsePassFromAuth("username:"),         null);
    assertEquals(SimpleRestClient.parsePassFromAuth("username:password"), "password");
    assertEquals(SimpleRestClient.parsePassFromAuth(":password"),         "password");
  }

/*
  public void testOptions() throws Exception {
    String goodUrl = "/account/jacob.parr@gmail.com";
    String badId =  "/CUSTOMER/mickey.mouse@disney.com";
    String badUrl =  "/CUSTOMER-x/jacob.parr@gmail.com";

    client = new SimpleRestClient(translator, "http://www.thomas-bayer.com/sqlrest", "free", "bird");
    // client.setNotFoundToNull(false);

    String response = client.get(String.class, goodUrl);
    assertNotNull(response);

    response = client.get(String.class, badId);
    assertNull(response);

    response = client.get(String.class, badUrl);
    assertNull(response);
  }
*/
}