package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class HalItemWrapperTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    private final TestObject obj = new TestObject("Mickey", "Mouse", 88, TestItemStatus.DEAD);

    public void testTranslation() throws Exception {

        HalLinksBuilder linksBuilder = new HalLinksBuilder();
        linksBuilder.create("google", "http://www.google.com");
        linksBuilder.create("amazon", "http://www.amazon.com");

        HalItemWrapper<TestObject> pubItem = new HalItemWrapper<>(obj, 200, linksBuilder.build());

        Assert.assertEquals(pubItem.getHttpStatusCode().getCode(), 200);
        Assert.assertEquals(pubItem.get_links().size(), 2);
        Assert.assertTrue(pubItem.get_links().hasLink("google"));
        Assert.assertTrue(pubItem.get_links().hasLink("amazon"));

        TestObject item = pubItem.getItem();
        Assert.assertEquals(item.getFirstName(), "Mickey");
        Assert.assertEquals(item.getLastName(), "Mouse");
        Assert.assertEquals(item.getAge(), 88);
        Assert.assertEquals(item.getStatus(), TestItemStatus.DEAD);

        String json = writer.writeValueAsString(pubItem).replace("\r","");
        Assert.assertEquals(json, TestFactory.JSON);
        // System.out.printf("\n%s\n\n", json);
    }
}