package com.jsonparser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public JsonValue parse() {
        JsonValue value = parseValue();
        expect(TokenType.EOF, "Expected end of input");
        return value;
    }

    private JsonValue parseValue() {
        Token token = peek();
        return switch (token.getType()) {
            case LEFT_BRACE -> parseObject(); // {
            case LEFT_BRACKET -> parseArray(); // [
            case STRING -> new JsonString(advance().getLexeme());
            case NUMBER -> new JsonNumber(parseNumber(advance()));
            case TRUE -> {
                advance();
                yield new JsonBoolean(true);
            }
            case FALSE -> {
                advance();
                yield new JsonBoolean(false);
            }
            case NULL -> {
                advance();
                yield JsonNull.INSTANCE;
            }
            default -> throw error(token, "Expected JSON value");
        };
    }

    private JsonObject parseObject() {
        expect(TokenType.LEFT_BRACE, "Expected '{'");

        LinkedHashMap<String, JsonValue> map = new LinkedHashMap<>();
        if (match(TokenType.RIGHT_BRACE)) {
            return new JsonObject(map);
        }

        do {
            Token keyToken = expect(TokenType.STRING, "Expected string key in object");
            expect(TokenType.COLON, "Expected ':' after object key");
            JsonValue value = parseValue();
            map.put(keyToken.getLexeme(), value);
        } while (match(TokenType.COMMA));

        expect(TokenType.RIGHT_BRACE, "Expected '}' after object members");
        return new JsonObject(map);
    }

    private JsonArray parseArray() {
        expect(TokenType.LEFT_BRACKET, "Expected '['");

        List<JsonValue> values = new ArrayList<>();
        if (match(TokenType.RIGHT_BRACKET)) {
            return new JsonArray(values);
        }

        do {
            values.add(parseValue());
        } while (match(TokenType.COMMA));

        expect(TokenType.RIGHT_BRACKET, "Expected ']' after array elements");
        return new JsonArray(values);
    }

    private double parseNumber(Token numberToken) {
        try {
            return Double.parseDouble(numberToken.getLexeme());
        } catch (NumberFormatException ex) {
            throw error(numberToken, "Invalid number: " + numberToken.getLexeme());
        }
    }

    private Token expect(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(TokenType type) {
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseException error(Token token, String message) {
        return new ParseException(message, token.getLine(), token.getColumn(), token.getPosition());
    }
}
