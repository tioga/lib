package org.tiogasolutions.lib.hal;

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
public final class HalItemWrapper<T> extends HalItem {

    @JsonUnwrapped
    private final T item;

    @JsonCreator
    protected HalItemWrapper(T item, @JsonProperty("_links") HalLinks _links) {
        super(_links);
        this.item = item;
    }

    public HalItemWrapper(T item, int code) {
        super(code);
        this.item = item;
    }

    public HalItemWrapper(T item, int code, HalLinks _links) {
        super(code, _links);
        this.item = item;
    }

    public HalItemWrapper(T item, HttpStatusCode statusCode) {
        super(statusCode);
        this.item = item;
    }

    public HalItemWrapper(T item, HttpStatusCode statusCode, HalLinks _links) {
        super(statusCode, _links);
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
