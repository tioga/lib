package org.tiogasolutions.pub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class PubItemWrapperTest {

    public void testTranslation() throws Exception {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

        TestObject obj = new TestObject("Mickey", "Mouse", 88, TestObject.Status.DEAD);
        PubItemWrapper<TestObject> pubItem = new PubItemWrapper<>(200, obj);

        Assert.assertEquals(pubItem.get_response().getCode(), 200);
        Assert.assertEquals(pubItem.get_response().getMessage(), "OK");

        Assert.assertTrue(pubItem.get_links().isEmpty());

        TestObject item = pubItem.getItem();
        Assert.assertEquals(item.getFirstName(), "Mickey");
        Assert.assertEquals(item.getLastName(), "Mouse");
        Assert.assertEquals(item.getAge(), 88);
        Assert.assertEquals(item.getStatus(), TestObject.Status.DEAD);

        String json = writer.writeValueAsString(pubItem);
        System.out.println(json);
    }
}