# Project Plan: JSON Validator + Parser + AST (Java)

## Goal
Build a 2-day course project that:
- reads JSON from file or console
- validates syntax
- parses into AST
- prints structure (and optionally pretty-prints JSON)
- reports precise errors (line/column/position)

## Scope
Supported JSON elements:
- object
- array
- string
- number
- boolean
- null

Out of scope:
- JSON schema validation
- Java object mapping/reflection
- code generation

## Architecture
- `TokenType`
- `Token`
- `Lexer`
- `Parser`
- `ParseException`
- AST:
  - `JsonValue`
  - `JsonObject`
  - `JsonArray`
  - `JsonString`
  - `JsonNumber`
  - `JsonBoolean`
  - `JsonNull`
- `AstPrinter`
- `PrettyPrinter`
- `Main`

## Grammar
```txt
value    -> object | array | string | number | 'true' | 'false' | 'null'
object   -> '{' members? '}'
members  -> pair (',' pair)*
pair     -> string ':' value
array    -> '[' elements? ']'
elements -> value (',' value)*
```

## Implementation Steps
1. Implement token model (`TokenType`, `Token`) with source location.
2. Implement lexer with:
   - punctuation tokens: `{ } [ ] : ,`
   - literals: strings, numbers, true/false/null
   - whitespace skipping
   - line/column/position tracking
3. Implement recursive descent parser:
   - `parseValue()`
   - `parseObject()`
   - `parseArray()`
4. Build AST nodes and return parsed tree.
5. Add clear diagnostics (`ParseException`) with exact location.
6. Add structure output (`AstPrinter`) and JSON formatting (`PrettyPrinter`).
7. Add CLI (`Main`) supporting file input or stdin.
8. Add tests for valid and invalid JSON.

## Acceptance Criteria
- Valid JSON: prints `Valid JSON` and AST.
- Invalid JSON: prints meaningful error with line/column.
- Nested arrays/objects parse correctly.
- Pretty print outputs valid formatted JSON.

## Test Cases
- Valid simple object.
- Valid nested object/array.
- Empty object/array.
- Missing comma between fields.
- Missing closing bracket/brace.
- Invalid literals.
- Invalid number formats.
- String escape handling.

## Defaults
- `LinkedHashMap` in `JsonObject` to preserve insertion order.
- `double` for numbers (sufficient for course scope).
- Trailing commas rejected.
- Pretty print uses 2-space indentation.
