package com.jsonparser;

public class ParseException extends RuntimeException {
    private final int line;
    private final int column;
    private final int position;

    public ParseException(String message, int line, int column, int position) {
        super(message);
        this.line = line;
        this.column = column;
        this.position = position;
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
    public String getMessage() {
        return super.getMessage() + " (line " + line + ", column " + column + ", position " + position + ")";
    }
}
