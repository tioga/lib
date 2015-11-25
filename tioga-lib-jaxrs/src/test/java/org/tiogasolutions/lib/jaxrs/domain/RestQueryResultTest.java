package org.tiogasolutions.lib.jaxrs.domain;

import org.testng.annotations.Test;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class RestQueryResultTest {

  private UriInfo uriInfo = new MockUriInfo();

  public void testNewEmpty() {

    RestQueryResult<String> result = RestQueryResult.newEmpty(uriInfo, "/widgets", String.class);

    assertEquals(result.getSize(), 0);
    assertEquals(result.getOffset(), 0);
    assertEquals(result.getLimit(), 0);
    assertTrue(result.isTotalExact());

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=0&limit=0"));
    assertEquals(result.getLinks().get("first"), null);
    assertEquals(result.getLinks().get("prev"), null);
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), null);
  }

  public void testNewSingle() throws Exception {

    RestQueryResult<String> result = RestQueryResult.newSingle(
        uriInfo, "/widgets",
        String.class,
        "kitten");

    assertEquals(result.getSize(), 1);
    assertEquals(result.getOffset(), 0);
    assertEquals(result.getLimit(), 1);
    assertEquals(result.getFirst(), "kitten");
    assertTrue(result.isTotalExact());

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=0&limit=1"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=1"));
    assertEquals(result.getLinks().get("prev"), null);
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=0&limit=1"));
  }

  public void testNewComplete_Collection() {
    RestQueryResult<String> result = RestQueryResult.newComplete(
        uriInfo, "/widgets",
        String.class,
        Arrays.asList("kitten", "puppy", "birdie"));

    validateNewComplete(result);
  }

  public void testNewComplete_Array() {
    RestQueryResult<String> result = RestQueryResult.newComplete(
        uriInfo, "/widgets",
        String.class,
        "kitten", "puppy", "birdie");

    validateNewComplete(result);
  }

  private void validateNewComplete(RestQueryResult<String> result) {
    assertEquals(result.getSize(), 3);
    assertEquals(result.getOffset(), 0);
    assertEquals(result.getLimit(), 3);
    assertEquals(result.getFirst(), "kitten");
    assertEquals(result.getLast(), "birdie");
    assertTrue(result.isTotalExact());

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), null);
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
  }

  public void testNewResult_Collection() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        4,  // offset
        20, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    validateNewResult(result);
  }

  public void testNewResult_Array() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        4,  // offset
        20, // total
        true,
        "kitten", "puppy", "birdie");

    validateNewResult(result);
  }

  private void validateNewResult(RestQueryResult<String> result) {
    assertEquals(result.getSize(), 3);
    assertEquals(result.getOffset(), 4);
    assertEquals(result.getLimit(), 3);
    assertEquals(result.getFirst(), "kitten");
    assertEquals(result.getLast(), "birdie");
    assertTrue(result.getAt(1).contains("puppy"));
    assertTrue(result.isTotalExact());

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=4&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=1&limit=3"));
    assertEquals(result.getLinks().get("next"), URI.create("http://example.com/api/widgets?offset=7&limit=3"));
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=17&limit=3"));
  }

  public void testNewResults_page1() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        0,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), null);
    assertEquals(result.getLinks().get("next"), URI.create("http://example.com/api/widgets?offset=3&limit=3"));
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));

  }

  public void testNewResults_page2() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        3,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=3&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("next"), URI.create("http://example.com/api/widgets?offset=6&limit=3"));
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));

  }

  public void testNewResults_page3() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        6,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=6&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=3&limit=3"));
    assertEquals(result.getLinks().get("next"), URI.create("http://example.com/api/widgets?offset=9&limit=3"));
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));

  }

  public void testNewResults_page4a() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        9,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=9&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=6&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }

  public void testNewResults_page4b() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        10,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=10&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=7&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }

  public void testNewResults_page4c() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        11,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=11&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }

  public void testNewResults_page4d() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        12,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=12&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=9&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }

  public void testNewResults_page4e() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        13,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=13&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=10&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }

  public void testNewResults_page4f() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        14,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=14&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=10&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }

  public void testNewResults_page4g() {
    RestQueryResult<String> result = RestQueryResult.newResult(
        uriInfo, "/widgets",
        String.class,
        3,  // limit
        15,  // offset
        11, // total
        true,
        Arrays.asList("kitten", "puppy", "birdie"));

    assertEquals(result.getLinks().size(), 5);
    assertEquals(result.getLinks().get("self"), URI.create("http://example.com/api/widgets?offset=15&limit=3"));
    assertEquals(result.getLinks().get("first"), URI.create("http://example.com/api/widgets?offset=0&limit=3"));
    assertEquals(result.getLinks().get("prev"), URI.create("http://example.com/api/widgets?offset=10&limit=3"));
    assertEquals(result.getLinks().get("next"), null);
    assertEquals(result.getLinks().get("last"), URI.create("http://example.com/api/widgets?offset=8&limit=3"));
  }
}