package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PubLink {
    private final String href;
    private final String rel;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, String> docs = new LinkedHashMap<>();

    @JsonCreator
    private PubLink(@JsonProperty("rel") String rel,
                    @JsonProperty("href") String href,
                    @JsonProperty("docs") Map<String, String> docs) {

        this.rel = rel;
        this.href = href;
        if (docs != null) this.docs.putAll(docs);
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public Map<String, String> getDocs() {
        return Collections.unmodifiableMap(docs);
    }

    public PubLink clone(String rel) {
        return new PubLink(rel, href, docs);
    }

    public PubLink toSelf() {
        return clone("self");
    }

    public PubLink toFirst() {
        return clone("first");
    }

    public PubLink toPrev() {
        return clone("prev");
    }

    public PubLink toNext() {
        return clone("next");
    }

    public PubLink toLast() {
        return clone("last");
    }

    public String toString() {
        return rel + ": " + href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PubLink pubLink = (PubLink) o;

        if (href != null ? !href.equals(pubLink.href) : pubLink.href != null) return false;
        return rel != null ? rel.equals(pubLink.rel) : pubLink.rel == null;

    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (rel != null ? rel.hashCode() : 0);
        return result;
    }

    public PubLink toLinks() {
        return clone(rel + "-links");
    }

    public PubLink toItems() {
        return clone(rel + "-items");
    }

    public static PubLink create(String rel, String href) {
        return create(rel, href, new String[0]);
    }

    public static PubLink create(String rel, String href, String... docItems) {
        PubLink link = new PubLink(rel, href, Collections.emptyMap());
        for (String doc : docItems) {
            int pos = doc.indexOf(":");
            if (pos < 0) throw new IllegalArgumentException("Invalid docs: " + doc);
            String key = doc.substring(0, pos).trim().toUpperCase();
            String val = doc.substring(pos + 1).trim();
            link.docs.put(key, val);
        }
        return link;
    }
}