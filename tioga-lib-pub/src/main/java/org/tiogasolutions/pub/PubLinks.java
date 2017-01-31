package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class PubLinks extends LinkedHashMap<String, PubLink> {

    @JsonCreator
    private PubLinks(Map<String, PubLink> links) {
        for (PubLink link : links.values()) {
            put(link.getRel(), link);
        }
    }

    @JsonIgnore
    public List<PubLink> getItems() {
        return new ArrayList<>(values());
    }

    public PubLink getLink(String rel) {
        return get(rel);
    }

    public boolean hasLink(String rel) {
        return getLink(rel) != null;
    }

    public PubLink add(String rel, String href) {
        return add(PubLink.create(rel, href));
    }

    public PubLink add(PubLink link) {
        if (link != null) {
            // This is really just a convenience to allow
            // us to add null and have no effect.
            return put(link.getRel(), link);
        }
        return null;
    }

    public void addAll(Collection<PubLink> links) {
        links.forEach(this::add);
    }

    public void addFPNL(PubLink firstLink, PubLink prevLink, PubLink nextLink, PubLink lastLink) {
        add(firstLink.clone("first"));
        add(prevLink.clone("prev"));
        add(nextLink.clone("next"));
        add(lastLink.clone("last"));
    }

    public static PubLinks empty() {
        return new PubLinks(Collections.emptyMap());
    }

    public static PubLinks self(PubLink... items) {
        return self(Arrays.asList(items));
    }

    public static PubLinks self(List<PubLink> items) {
        PubLinks links = new PubLinks(Collections.emptyMap());
        for (PubLink link : items) {
            if (link.getRel().endsWith("-links")) {
                links.add(link.clone("self-links"));

            } else if (link.getRel().endsWith("-items")) {
                links.add(link.clone("self-items"));

            } else {
                links.add(link.clone("self"));
            }
        }
        return links;
    }
}
