package com.jsonparser;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int current = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column, current));
        return tokens;
    }

    private void scanToken() {
        skipWhitespace();
        if (isAtEnd()) {
            return;
        }

        int tokenLine = line;
        int tokenColumn = column;
        int tokenPosition = current;
        char c = advance();

        switch (c) {
            case '{' -> add(TokenType.LEFT_BRACE, "{", tokenLine, tokenColumn, tokenPosition);
            case '}' -> add(TokenType.RIGHT_BRACE, "}", tokenLine, tokenColumn, tokenPosition);
            case '[' -> add(TokenType.LEFT_BRACKET, "[", tokenLine, tokenColumn, tokenPosition);
            case ']' -> add(TokenType.RIGHT_BRACKET, "]", tokenLine, tokenColumn, tokenPosition);
            case ':' -> add(TokenType.COLON, ":", tokenLine, tokenColumn, tokenPosition);
            case ',' -> add(TokenType.COMMA, ",", tokenLine, tokenColumn, tokenPosition);
            case '"' -> string(tokenLine, tokenColumn, tokenPosition); // goes through the whole string and add it as token without the ""
            case '-' -> number(tokenLine, tokenColumn, tokenPosition, c); // goes through the whole number and add it as token
            default -> {
                if (isDigit(c)) {
                    number(tokenLine, tokenColumn, tokenPosition, c);
                } else if (isLetter(c)) {
                    identifier(tokenLine, tokenColumn, tokenPosition, c); // true, false, nulll
                } else {
                    throw new ParseException("Unexpected character: '" + c + "'", tokenLine, tokenColumn, tokenPosition);
                }
            }
        }
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t') {
                advance();
            } else if (c == '\n') {
                advance();
            } else {
                break;
            }
        }
    }

    private void string(int tokenLine, int tokenColumn, int tokenPosition) {
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && peek() != '"') {
            char c = advance();
            if (c == '\\') {
                if (isAtEnd()) {
                    throw new ParseException("Unterminated escape sequence in string", line, column, current);
                }
                char escaped = advance();
                switch (escaped) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        String hex = readUnicodeHex();
                        sb.append((char) Integer.parseInt(hex, 16));
                    }
                    default -> throw new ParseException(
                        "Invalid escape character: '\\" + escaped + "'", line, column - 1, current - 1
                    );
                }
            } else {
                if (c == '\n') {
                    throw new ParseException("Unterminated string literal", tokenLine, tokenColumn, tokenPosition);
                }
                sb.append(c);
            }
        }

        if (isAtEnd()) {
            throw new ParseException("Unterminated string literal", tokenLine, tokenColumn, tokenPosition);
        }

        advance(); // closing quote is skipped
        add(TokenType.STRING, sb.toString(), tokenLine, tokenColumn, tokenPosition);
    }

    private String readUnicodeHex() {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (isAtEnd()) {
                throw new ParseException("Incomplete unicode escape sequence", line, column, current);
            }
            char h = advance();
            if (!isHexDigit(h)) {
                throw new ParseException("Invalid unicode escape sequence", line, column - 1, current - 1);
            }
            hex.append(h);
        }
        return hex.toString();
    }

    private void number(int tokenLine, int tokenColumn, int tokenPosition, char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        if (firstChar == '-') {
            if (isAtEnd() || !isDigit(peek())) {
                throw new ParseException("Invalid number format", tokenLine, tokenColumn, tokenPosition);
            }
            sb.append(advance());
        }

        if (sb.charAt(sb.length() - 1) == '0') {
            if (!isAtEnd() && isDigit(peek())) {
                throw new ParseException("Leading zeros are not allowed", line, column, current);
            }
        } else {
            while (!isAtEnd() && isDigit(peek())) {
                sb.append(advance());
            }
        }

        if (!isAtEnd() && peek() == '.') {
            sb.append(advance());
            if (isAtEnd() || !isDigit(peek())) {
                throw new ParseException("Invalid number format: missing digits after decimal point", line, column, current);
            }
            while (!isAtEnd() && isDigit(peek())) {
                sb.append(advance());
            }
        }

        if (!isAtEnd() && (peek() == 'e' || peek() == 'E')) {
            sb.append(advance());
            if (!isAtEnd() && (peek() == '+' || peek() == '-')) {
                sb.append(advance());
            }
            if (isAtEnd() || !isDigit(peek())) {
                throw new ParseException("Invalid number format: missing exponent digits", line, column, current);
            }
            while (!isAtEnd() && isDigit(peek())) {
                sb.append(advance());
            }
        }

        add(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn, tokenPosition);
    }

    private void identifier(int tokenLine, int tokenColumn, int tokenPosition, char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        while (!isAtEnd() && isLetter(peek())) {
            sb.append(advance());
        }

        String lexeme = sb.toString();
        switch (lexeme) {
            case "true" -> add(TokenType.TRUE, lexeme, tokenLine, tokenColumn, tokenPosition);
            case "false" -> add(TokenType.FALSE, lexeme, tokenLine, tokenColumn, tokenPosition);
            case "null" -> add(TokenType.NULL, lexeme, tokenLine, tokenColumn, tokenPosition);
            default -> throw new ParseException("Unexpected identifier: " + lexeme, tokenLine, tokenColumn, tokenPosition);
        }
    }

    private void add(TokenType type, String lexeme, int tokenLine, int tokenColumn, int tokenPosition) {
        tokens.add(new Token(type, lexeme, tokenLine, tokenColumn, tokenPosition));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        char c = source.charAt(current++);
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private char peek() {
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'f')
            || (c >= 'A' && c <= 'F');
    }
}
