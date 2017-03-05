package org.tiogasolutions.lib.hal;

import java.net.URI;
import java.util.LinkedHashMap;

public class HalLinksBuilder {

    private final LinkedHashMap<String, HalLink> links = new LinkedHashMap<>();

    public HalLinks build() {
        return new HalLinks(links);
    }

    public HalLinksBuilder create(String rel, String href) {
        links.put(rel, HalLink.create(href));
        return this;
    }

    public HalLinksBuilder create(String rel, URI href) {
        links.put(rel, HalLink.create(href));
        return this;
    }

    public HalLinksBuilder create(String rel, String href, String title) {
        links.put(rel, HalLink.create(href, title));
        return this;
    }

    public HalLinksBuilder create(String rel, URI href, String title) {
        links.put(rel, HalLink.create(href, title));
        return this;
    }

    public HalLinksBuilder add(String rel, HalLink link) {
        links.put(rel, link);
        return this;
    }

    public HalLinksBuilder self(HalLink link) {
        links.put("self", link);
        return this;
    }

    public HalLinksBuilder first(HalLink link) {
        links.put("first", link);
        return this;
    }

    public HalLinksBuilder prev(HalLink link) {
        links.put("prev", link);
        return this;
    }

    public HalLinksBuilder next(HalLink link) {
        links.put("next", link);
        return this;
    }

    public HalLinksBuilder last(HalLink link) {
        links.put("last", link);
        return this;
    }

    public HalLinksBuilder addFPNL(HalLink firstLink, HalLink prevLink, HalLink nextLink, HalLink lastLink) {
        first(firstLink);
        prev(prevLink);
        next(nextLink);
        last(lastLink);
        return this;
    }

    public static HalLinksBuilder builder() {
        return new HalLinksBuilder();
    }
}
