package com.jsonparser;

import java.util.List;

public final class JsonArray implements JsonValue {
    private final List<JsonValue> values;

    public JsonArray(List<JsonValue> values) {
        this.values = values;
    }

    public List<JsonValue> getValues() {
        return values;
    }
}
