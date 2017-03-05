package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestObject {

    private final String firstName;
    private final String lastName;
    private final int age;
    private final TestItemStatus status;

    public TestObject(@JsonProperty("firstName") String firstName,
                      @JsonProperty("lastName") String lastName,
                      @JsonProperty("age") int age,
                      @JsonProperty("status") TestItemStatus status) {

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
