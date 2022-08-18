package com.example.demo.metrics;

public enum TagsEnum {

    EXCEPTION("exception"),
    METHOD("method"),
    OUTCOME("outcome"),
    STATUS("status"),
    URI("uri");

    private final String value;

    TagsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
