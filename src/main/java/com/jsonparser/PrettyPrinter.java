package com.jsonparser;

import java.util.Iterator;
import java.util.Map;

public final class PrettyPrinter {
    private static final String INDENT = "  ";

    private PrettyPrinter() {
    }

    public static String format(JsonValue value) {
        return format(value, 0);
    }

    private static String format(JsonValue value, int depth) {
        if (value instanceof JsonObject obj) {
            if (obj.getValues().isEmpty()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            Iterator<Map.Entry<String, JsonValue>> iterator = obj.getValues().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonValue> entry = iterator.next();
                sb.append(indent(depth + 1))
                    .append('"').append(escape(entry.getKey())).append('"')
                    .append(": ")
                    .append(format(entry.getValue(), depth + 1));
                if (iterator.hasNext()) {
                    sb.append(',');
                }
                sb.append('\n');
            }
            sb.append(indent(depth)).append('}');
            return sb.toString();
        }

        if (value instanceof JsonArray array) {
            if (array.getValues().isEmpty()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < array.getValues().size(); i++) {
                sb.append(indent(depth + 1)).append(format(array.getValues().get(i), depth + 1));
                if (i < array.getValues().size() - 1) {
                    sb.append(',');
                }
                sb.append('\n');
            }
            sb.append(indent(depth)).append(']');
            return sb.toString();
        }

        if (value instanceof JsonString str) {
            return '"' + escape(str.getValue()) + '"';
        }

        if (value instanceof JsonNumber num) {
            double v = num.getValue();
            if (v == (long) v) {
                return Long.toString((long) v);
            }
            return Double.toString(v);
        }

        if (value instanceof JsonBoolean bool) {
            return Boolean.toString(bool.getValue());
        }

        return "null";
    }

    private static String indent(int depth) {
        return INDENT.repeat(depth);
    }

    private static String escape(String s) {
        return s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
