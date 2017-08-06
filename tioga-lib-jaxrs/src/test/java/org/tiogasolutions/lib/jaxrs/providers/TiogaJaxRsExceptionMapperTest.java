package org.tiogasolutions.lib.jaxrs.providers;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

@Test
public class TiogaJaxRsExceptionMapperTest {

    public void testCleanUrl() {
        String result = TiogaJaxRsExceptionMapper.cleanUrl(null);
        Assert.assertNull(result);

        result = TiogaJaxRsExceptionMapper.cleanUrl(URI.create("http://www.google.com"));
        Assert.assertEquals(result, "www.google.com");

        result = TiogaJaxRsExceptionMapper.cleanUrl(URI.create("https://www.google.com"));
        Assert.assertEquals(result, "www.google.com");

        result = TiogaJaxRsExceptionMapper.cleanUrl(URI.create("file://www.google.com"));
        Assert.assertEquals(result, "file://www.google.com");
    }
}