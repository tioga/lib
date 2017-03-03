package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

/**
 * Creates a one-way conversion from a pub object to JSON/XML.
 * It's not intended to be deserializable and is provided only
 * as a temporary stop-gap for older apis.
 * @param <T> The item contained by this wrapper.
 */
public final class PubItemWrapper<T> extends PubItem {

    @JsonUnwrapped
    private final T item;

    /**
     * Creates an instance of the PubItemWrapper.
     * @param _status The HTTP status of the call for which this item was produced.
     * @param _links The HATEOS links for the corresponding resource.
     * @param item The item contained by this wrapper.
     */
    @JsonCreator
    public PubItemWrapper(@JsonProperty("_status") PubResponse _status,
                          @JsonProperty("_links") PubLinks _links,
                          @JsonProperty("item") T item) {

        super(_status, _links);
        this.item = item;
    }

    public PubItemWrapper(PubResponse _status, T item) {
        this(_status, null, item);
    }

    public PubItemWrapper(int code, T item) {
        this(code, null, item);
    }

    public PubItemWrapper(int code, PubLinks _links, T item) {
        this(toStatus(code), _links, item);
    }

    public PubItemWrapper(HttpStatusCode statusCode, T item) {
        this(statusCode, null, item);
    }

    public PubItemWrapper(HttpStatusCode statusCode, PubLinks _links, T item) {
        this(toStatus(statusCode), _links, item);
    }

    public T getItem() {
        return item;
    }
}
