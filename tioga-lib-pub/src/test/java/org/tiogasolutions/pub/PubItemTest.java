package org.tiogasolutions.pub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class PubItemTest {

    public void testTranslation() throws Exception {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

        PubItem item = new PubItem(200);

        Assert.assertEquals(item.get_response().getCode(), 200);
        Assert.assertEquals(item.get_response().getMessage(), "OK");

        Assert.assertTrue(item.get_links().isEmpty());

        String json = writer.writeValueAsString(item);
        System.out.println(json);
    }
}