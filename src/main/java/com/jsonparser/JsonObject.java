package com.jsonparser;

import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonObject implements JsonValue {
    private final LinkedHashMap<String, JsonValue> values;

    public JsonObject(LinkedHashMap<String, JsonValue> values) {
        this.values = values;
    }

    public Map<String, JsonValue> getValues() {
        return values;
    }
}
