package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestObject {

    public enum Status{ALIVE, DEAD}

    private final String firstName;
    private final String lastName;
    private final int age;
    private final Status status;

    public TestObject(@JsonProperty("firstName") String firstName,
                      @JsonProperty("lastName") String lastName,
                      @JsonProperty("age") int age,
                      @JsonProperty("status") Status status) {

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

    public Status getStatus() {
        return status;
    }
}
