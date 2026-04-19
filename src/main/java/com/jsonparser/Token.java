package com.jsonparser;

public final class Token {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private final int position;

    public Token(TokenType type, String lexeme, int line, int column, int position) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.position = position;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return type + "('" + lexeme + "') at line " + line + ", column " + column;
    }
}
