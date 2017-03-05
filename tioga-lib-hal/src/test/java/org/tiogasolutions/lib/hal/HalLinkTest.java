package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

@Test
public class HalLinkTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    public void testForceHttpsOff() throws Exception {
        HalLink link = HalLink.create("http://username:secret@www.example.com:80/whatever?offset=32");
        Assert.assertEquals(link.getHref().toString(), "http://username:secret@www.example.com:80/whatever?offset=32");

        link = HalLink.create("https://username:secret@www.example.com:80/whatever?offset=32");
        Assert.assertEquals(link.getHref().toString(), "https://username:secret@www.example.com:80/whatever?offset=32");

        link = HalLink.create("http://www.example.com:80/whatever?offset=32");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com:80/whatever?offset=32");

        link = HalLink.create("http://www.example.com:80/whatever");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com:80/whatever");

        link = HalLink.create("http://www.example.com:80/");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com:80/");

        link = HalLink.create("http://www.example.com:80");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com:80");

        link = HalLink.create("http://username:secret@www.example.com/whatever?offset=32");
        Assert.assertEquals(link.getHref().toString(), "http://username:secret@www.example.com/whatever?offset=32");

        link = HalLink.create("https://username:secret@www.example.com/whatever?offset=32");
        Assert.assertEquals(link.getHref().toString(), "https://username:secret@www.example.com/whatever?offset=32");

        link = HalLink.create("http://www.example.com/whatever?offset=32");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com/whatever?offset=32");

        link = HalLink.create("http://www.example.com/whatever");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com/whatever");

        link = HalLink.create("http://www.example.com/");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com/");

        link = HalLink.create("http://www.example.com");
        Assert.assertEquals(link.getHref().toString(), "http://www.example.com");
    }

    public void testForceHttpsOn() throws Exception {

        HalLink link;
        HalLink.forceHttps = true;

        try {

            link = HalLink.create("http://username:secret@www.example.com:80/whatever?offset=32");
            Assert.assertEquals(link.getHref().toString(), "https://username:secret@www.example.com:443/whatever?offset=32");

            link = HalLink.create("https://username:secret@www.example.com:80/whatever?offset=32");
            Assert.assertEquals(link.getHref().toString(), "https://username:secret@www.example.com:80/whatever?offset=32");

            link = HalLink.create("http://www.example.com:80/whatever?offset=32");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443/whatever?offset=32");

            link = HalLink.create("http://www.example.com:80/whatever");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443/whatever");

            link = HalLink.create("http://www.example.com:80/");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443/");

            link = HalLink.create("http://www.example.com:80");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443");

            link = HalLink.create("http://username:secret@www.example.com/whatever?offset=32");
            Assert.assertEquals(link.getHref().toString(), "https://username:secret@www.example.com:443/whatever?offset=32");

            link = HalLink.create("https://username:secret@www.example.com/whatever?offset=32");
            Assert.assertEquals(link.getHref().toString(), "https://username:secret@www.example.com/whatever?offset=32");

            link = HalLink.create("http://www.example.com/whatever?offset=32");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443/whatever?offset=32");

            link = HalLink.create("http://www.example.com/whatever");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443/whatever");

            link = HalLink.create("http://www.example.com/");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443/");

            link = HalLink.create("http://www.example.com");
            Assert.assertEquals(link.getHref().toString(), "https://www.example.com:443");

        } finally {
            HalLink.forceHttps = false;
        }
    }

    public void testCreateWithHref() throws Exception {

        HalLink oldLink = HalLink.create("http://www.example.com/whatever?offset=32");

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), null);
        Assert.assertEquals(newLink.isTemplated(), false);
        Assert.assertEquals(newLink.getDeprecation(), null);

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n", json);
    }

    public void testCreateWithHrefTitle() throws Exception {

        HalLink oldLink = HalLink.create("http://www.example.com/whatever?offset=32", "Some Link");

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), "Some Link");
        Assert.assertEquals(newLink.isTemplated(), false);
        Assert.assertEquals(newLink.getDeprecation(), null);

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n", json);
    }

    public void testCreateWithUriHref() throws Exception {

        URI href = URI.create("http://www.example.com/whatever?offset=32");
        HalLink oldLink = HalLink.create(href);

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), null);
        Assert.assertEquals(newLink.isTemplated(), false);
        Assert.assertEquals(newLink.getDeprecation(), null);

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n", json);
    }

    public void testCreateWithUriHrefTitle() throws Exception {

        URI href = URI.create("http://www.example.com/whatever?offset=32");
        HalLink oldLink = HalLink.create(href, "Some Link");

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), "Some Link");
        Assert.assertEquals(newLink.isTemplated(), false);
        Assert.assertEquals(newLink.getDeprecation(), null);

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n", json);
    }

    public void testDeprecated() throws Exception {

        HalLink oldLink = HalLink.create("http://www.example.com/whatever?offset=32");
        oldLink = oldLink.deprecation("http://www.google.com");

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), null);
        Assert.assertEquals(newLink.isTemplated(), false);
        Assert.assertEquals(newLink.getDeprecation(), "http://www.google.com");

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n", json);
    }

    public void testTemplated() throws Exception {

        HalLink oldLink = HalLink.create("http://www.example.com/whatever?offset=32", "A Link");
        oldLink = oldLink.templated();

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), "A Link");
        Assert.assertEquals(newLink.isTemplated(), true);
        Assert.assertEquals(newLink.getDeprecation(), null);

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n\n", json);
    }

    public void testAll() throws Exception {

        HalLink oldLink = HalLink
                .create("http://www.example.com/whatever?offset=32", "Whatever")
                .templated()
                .deprecation("http://whatever.com");

        String json = writer.writeValueAsString(oldLink);
        HalLink newLink = mapper.readValue(json, HalLink.class);

        Assert.assertEquals(newLink.getHref().toString(), "http://www.example.com/whatever?offset=32");
        Assert.assertEquals(newLink.getTitle(), "Whatever");
        Assert.assertEquals(newLink.isTemplated(), true);
        Assert.assertEquals(newLink.getDeprecation(), "http://whatever.com");

        Assert.assertEquals(newLink.getCuries().size(), 0);
        Assert.assertEquals(newLink.getName(), null);
        Assert.assertEquals(newLink.getType(), null);
        Assert.assertEquals(newLink.getProfile(), null);
        Assert.assertEquals(newLink.getHreflang(), null);

        System.out.printf("\n%s\n\n", json);
    }
}