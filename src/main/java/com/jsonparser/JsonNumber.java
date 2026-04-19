package com.jsonparser;

public final class JsonNumber implements JsonValue {
    private final double value;

    public JsonNumber(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
