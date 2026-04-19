package com.jsonparser;

import java.util.List;

public final class JsonProcessor {
    private JsonProcessor() {
    }

    public static JsonValue parse(String input) {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
}
