package org.tiogasolutions.lib.jaxrs.client;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test
public class BasicAuthorizationTest {

  public void testConstruction() throws Exception {
    BasicAuthorization authorization = new BasicAuthorization("free", "bird");
    assertEquals(authorization.getUsername(), "free");
    assertEquals(authorization.getPassword(), "bird");

    authorization = new BasicAuthorization(null, "bird");
    assertEquals(authorization.getUsername(), null);
    assertEquals(authorization.getPassword(), "bird");

    authorization = new BasicAuthorization("free", null);
    assertEquals(authorization.getUsername(), "free");
    assertEquals(authorization.getPassword(), null);

    authorization = new BasicAuthorization(null, null);
    assertEquals(authorization.getUsername(), null);
    assertEquals(authorization.getPassword(), null);
  }

  public void testParseUrl() throws Exception {
    assertEquals(BasicAuthorization.removeBasicAuth(null),                                         null);
    assertEquals(BasicAuthorization.removeBasicAuth(""),                                           null);
    assertEquals(BasicAuthorization.removeBasicAuth("http://google.com"),                          "http://google.com");
    assertEquals(BasicAuthorization.removeBasicAuth("http://google.com/test"),                     "http://google.com/test");
    assertEquals(BasicAuthorization.removeBasicAuth("https://google.com"),                         "https://google.com");
    assertEquals(BasicAuthorization.removeBasicAuth("https://google.com/test"),                    "https://google.com/test");
    assertEquals(BasicAuthorization.removeBasicAuth("http://username@google.com"),                 "http://google.com");
    assertEquals(BasicAuthorization.removeBasicAuth("http://username@google.com/test"),            "http://google.com/test");
    assertEquals(BasicAuthorization.removeBasicAuth("https://username@google.com"),                "https://google.com");
    assertEquals(BasicAuthorization.removeBasicAuth("https://username@google.com/test"),           "https://google.com/test");
    assertEquals(BasicAuthorization.removeBasicAuth("http://username:password@google.com"),        "http://google.com");
    assertEquals(BasicAuthorization.removeBasicAuth("http://username:password@google.com/test"),   "http://google.com/test");
    assertEquals(BasicAuthorization.removeBasicAuth("https://username:password@google.com"),       "https://google.com");
    assertEquals(BasicAuthorization.removeBasicAuth("https://username:password@google.com/test"),  "https://google.com/test");
  }

  public void testParseAuth() throws Exception {
    assertEquals(BasicAuthorization.parseBasicAuth(null),                                         null);
    assertEquals(BasicAuthorization.parseBasicAuth(""),                                           null);
    assertEquals(BasicAuthorization.parseBasicAuth("http://google.com"),                          null);
    assertEquals(BasicAuthorization.parseBasicAuth("http://google.com/test"),                     null);
    assertEquals(BasicAuthorization.parseBasicAuth("https://google.com"),                         null);
    assertEquals(BasicAuthorization.parseBasicAuth("https://google.com/test"),                    null);
    assertEquals(BasicAuthorization.parseBasicAuth("http://username@google.com"),                 "username");
    assertEquals(BasicAuthorization.parseBasicAuth("http://username@google.com/test"),            "username");
    assertEquals(BasicAuthorization.parseBasicAuth("https://username@google.com"),                "username");
    assertEquals(BasicAuthorization.parseBasicAuth("https://username@google.com/test"),           "username");
    assertEquals(BasicAuthorization.parseBasicAuth("http://username:password@google.com"),        "username:password");
    assertEquals(BasicAuthorization.parseBasicAuth("http://username:password@google.com/test"),   "username:password");
    assertEquals(BasicAuthorization.parseBasicAuth("https://username:password@google.com"),       "username:password");
    assertEquals(BasicAuthorization.parseBasicAuth("https://username:password@google.com/test"),  "username:password");
  }

  public void parseUserFromAuth() throws Exception {
    assertEquals(BasicAuthorization.parseUserFromAuth(null),                null);
    assertEquals(BasicAuthorization.parseUserFromAuth(""),                  null);
    assertEquals(BasicAuthorization.parseUserFromAuth(":"),                 null);
    assertEquals(BasicAuthorization.parseUserFromAuth("username"),          "username");
    assertEquals(BasicAuthorization.parseUserFromAuth("username:"),         "username");
    assertEquals(BasicAuthorization.parseUserFromAuth("username:password"), "username");
    assertEquals(BasicAuthorization.parseUserFromAuth(":password"),         null);
  }

  public void parsePassFromAuth() throws Exception {
    assertEquals(BasicAuthorization.parsePassFromAuth(null),                null);
    assertEquals(BasicAuthorization.parsePassFromAuth(""),                  null);
    assertEquals(BasicAuthorization.parsePassFromAuth(":"),                 null);
    assertEquals(BasicAuthorization.parsePassFromAuth("username"),          null);
    assertEquals(BasicAuthorization.parsePassFromAuth("username:"),         null);
    assertEquals(BasicAuthorization.parsePassFromAuth("username:password"), "password");
    assertEquals(BasicAuthorization.parsePassFromAuth(":password"),         "password");
  }
}