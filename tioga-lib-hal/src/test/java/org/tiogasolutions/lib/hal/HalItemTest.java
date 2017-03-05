package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

@Test
public class HalItemTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    private final TestObject obj = new TestObject("Mickey", "Mouse", 88, TestItemStatus.DEAD);

    public void testTranslation() throws Exception {

        HalLinksBuilder lnkBuilder = new HalLinksBuilder();
        lnkBuilder.create("google", "http://www.google.com");
        lnkBuilder.create("amazon", "http://www.amazon.com");

        TestHalItem item = new TestHalItem(HttpStatusCode.OK, lnkBuilder.build(), "Mickey", "Mouse", 88, TestItemStatus.DEAD);

        Assert.assertEquals(item.getHttpStatusCode().getCode(), 200);
        Assert.assertEquals(item.get_links().size(), 2);
        Assert.assertTrue(item.get_links().hasLink("google"));
        Assert.assertTrue(item.get_links().hasLink("amazon"));

        Assert.assertEquals(item.getFirstName(), "Mickey");
        Assert.assertEquals(item.getLastName(), "Mouse");
        Assert.assertEquals(item.getAge(), 88);
        Assert.assertEquals(item.getStatus(), TestItemStatus.DEAD);

        String json = writer.writeValueAsString(item).replace("\r","");
        Assert.assertEquals(json, TestFactory.JSON);
        // System.out.printf("\n%s\n\n", json);
    }
}