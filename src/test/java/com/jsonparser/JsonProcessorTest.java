package com.jsonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JsonProcessorTest {

    @Test
    void parsesSimpleValidObject() {
        JsonValue value = JsonProcessor.parse("{\"name\":\"Ivan\",\"age\":22}");
        JsonObject obj = assertInstanceOf(JsonObject.class, value);
        assertEquals(2, obj.getValues().size());
        assertInstanceOf(JsonString.class, obj.getValues().get("name"));
        assertInstanceOf(JsonNumber.class, obj.getValues().get("age"));
    }

    @Test
    void parsesNestedValidJson() {
        String json = """
            {
              \"user\": {\"name\": \"Ivan\", \"active\": true},
              \"skills\": [\"Java\", \"SQL\"],
              \"score\": 5.75,
              \"pet\": null
            }
            """;

        JsonValue value = JsonProcessor.parse(json);
        JsonObject obj = assertInstanceOf(JsonObject.class, value);
        assertInstanceOf(JsonObject.class, obj.getValues().get("user"));
        assertInstanceOf(JsonArray.class, obj.getValues().get("skills"));
        assertInstanceOf(JsonNumber.class, obj.getValues().get("score"));
        assertInstanceOf(JsonNull.class, obj.getValues().get("pet"));
    }

    @Test
    void parsesEmptyStructures() {
        assertInstanceOf(JsonObject.class, JsonProcessor.parse("{}"));
        assertInstanceOf(JsonArray.class, JsonProcessor.parse("[]"));
    }

    @Test
    void failsOnMissingComma() {
        ParseException ex = assertThrows(ParseException.class,
            () -> JsonProcessor.parse("{\"name\":\"Ivan\" \"age\":22}"));
        assertTrue(ex.getMessage().contains("Expected '}' after object members"));
    }

    @Test
    void failsOnMissingClosingBrace() {
        ParseException ex = assertThrows(ParseException.class,
            () -> JsonProcessor.parse("{\"name\":\"Ivan\""));
        assertTrue(ex.getMessage().contains("Expected '}' after object members"));
    }

    @Test
    void failsOnInvalidLiteral() {
        ParseException ex = assertThrows(ParseException.class,
            () -> JsonProcessor.parse("{\"ok\": tru}"));
        assertTrue(ex.getMessage().contains("Unexpected identifier"));
    }

    @Test
    void failsOnInvalidNumber() {
        ParseException ex = assertThrows(ParseException.class,
            () -> JsonProcessor.parse("{\"n\": 01}"));
        assertTrue(ex.getMessage().contains("Leading zeros"));
    }

    @Test
    void handlesEscapedStrings() {
        JsonObject obj = assertInstanceOf(JsonObject.class,
            JsonProcessor.parse("{\"text\":\"Hello\\nWorld \\\"x\\\"\\u0021\"}"));
        JsonString text = assertInstanceOf(JsonString.class, obj.getValues().get("text"));
        assertEquals("Hello\nWorld \"x\"!", text.getValue());
    }

    @Test
    void prettyPrinterFormatsJson() {
        JsonValue value = JsonProcessor.parse("{\"a\":[1,true,null],\"b\":\"x\"}");
        String pretty = PrettyPrinter.format(value);
        assertTrue(pretty.contains("\n"));
        assertTrue(pretty.contains("  \"a\": ["));
        assertTrue(pretty.contains("\"b\": \"x\""));
    }
}
