package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class HalLinks extends LinkedHashMap<String, HalLink> {

    @JsonCreator
    public HalLinks(Map<String, HalLink> links) {
        putAll(links);
    }

    @JsonIgnore
    public List<HalLink> getLinks() {
        return new ArrayList<>(super.values());
    }



    public HalLink getLink(String rel) {
        return super.get(rel);
    }

    @Deprecated
    public HalLink get(String rel) {
        return super.get(rel);
    }



    public boolean hasLink(String rel) {
        return super.containsKey(rel);
    }

    @Deprecated
    public boolean containsKey(String rel) {
        return super.containsKey(rel);
    }



    public static HalLinks empty() {
        return new HalLinks(Collections.emptyMap());
    }

    public static HalLinksBuilder builder() {
        return HalLinksBuilder.builder();
    }
}
