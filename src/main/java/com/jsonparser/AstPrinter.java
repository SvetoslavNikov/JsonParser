package com.jsonparser;

import java.util.Map;

public final class AstPrinter {
    private AstPrinter() {
    }

    public static String print(JsonValue value) {
        StringBuilder sb = new StringBuilder();
        print(value, sb, 0, null);
        return sb.toString().stripTrailing();
    }

    private static void print(JsonValue value, StringBuilder sb, int depth, String key) {
        String prefix = "  ".repeat(depth);
        if (key != null) {
            sb.append(prefix).append(key).append(": ");
        } else {
            sb.append(prefix);
        }

        if (value instanceof JsonObject obj) {
            sb.append("OBJECT\n");
            for (Map.Entry<String, JsonValue> entry : obj.getValues().entrySet()) {
                print(entry.getValue(), sb, depth + 1, entry.getKey());
            }
            return;
        }

        if (value instanceof JsonArray array) {
            sb.append("ARRAY\n");
            for (JsonValue item : array.getValues()) {
                print(item, sb, depth + 1, null);
            }
            return;
        }

        if (value instanceof JsonString str) {
            sb.append("STRING(\"").append(str.getValue()).append("\")\n");
            return;
        }

        if (value instanceof JsonNumber num) {
            sb.append("NUMBER(").append(num.getValue()).append(")\n");
            return;
        }

        if (value instanceof JsonBoolean bool) {
            sb.append("BOOLEAN(").append(bool.getValue()).append(")\n");
            return;
        }

        sb.append("NULL\n");
    }
}
