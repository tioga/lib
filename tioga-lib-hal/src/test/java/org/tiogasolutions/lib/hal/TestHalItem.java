package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

public class TestHalItem extends HalItem {

    private final String firstName;
    private final String lastName;
    private final int age;
    private final TestItemStatus status;

    private TestHalItem(@JsonProperty("_links") HalLinks _links,
                        @JsonProperty("firstName") String firstName,
                        @JsonProperty("lastName") String lastName,
                        @JsonProperty("age") int age,
                        @JsonProperty("status") TestItemStatus status) {

        super(null, _links);

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.status = status;
    }

    public TestHalItem(HttpStatusCode statusCode,
                       HalLinks _links,
                       String firstName,
                       String lastName,
                       int age,
                       TestItemStatus status) {

        super(statusCode, _links);

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public TestItemStatus getStatus() {
        return status;
    }
}
