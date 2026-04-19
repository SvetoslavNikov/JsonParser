# JSON Parser Project Walkthrough

## 1. What This Project Is

This project is a small, clean Java implementation of a JSON parser pipeline:

1. Read raw text from a file or standard input.
2. Tokenize the text (lexing).
3. Parse tokens into an Abstract Syntax Tree (AST).
4. Print:
   - a structural AST view, and optionally
   - pretty-formatted JSON.

It is built with Maven, targets Java 17, and includes JUnit 5 tests.

The design follows a classic compiler-style separation:

- `Lexer` turns characters into `Token`s.
- `Parser` turns tokens into typed AST nodes implementing `JsonValue`.
- `AstPrinter` and `PrettyPrinter` provide output views.

This separation keeps each part simple and makes it easy to extend.

---

## 2. Project Structure

Main source code:

- `src/main/java/com/jsonparser/Main.java`
- `src/main/java/com/jsonparser/JsonProcessor.java`
- `src/main/java/com/jsonparser/Lexer.java`
- `src/main/java/com/jsonparser/Parser.java`
- `src/main/java/com/jsonparser/ParseException.java`
- `src/main/java/com/jsonparser/TokenType.java`
- `src/main/java/com/jsonparser/Token.java`
- AST model:
  - `JsonValue`
  - `JsonObject`
  - `JsonArray`
  - `JsonString`
  - `JsonNumber`
  - `JsonBoolean`
  - `JsonNull`
- Output helpers:
  - `AstPrinter`
  - `PrettyPrinter`

Tests and fixtures:

- `src/test/java/com/jsonparser/JsonProcessorTest.java`
- `src/test/resources/valid-simple.json`
- `src/test/resources/valid-complex.json`
- `src/test/resources/invalid-missing-comma.json`

Build config:

- `pom.xml`

Planning doc:

- `PROJECT_PLAN.md`

---

## 3. Runtime Flow: End-to-End

When you run the app, the high-level path is:

1. `Main.main(String[] args)` parses CLI flags:
   - `--file <path>` to read JSON from a file.
   - `--pretty` to print pretty JSON in addition to AST.
2. Input is read from file or STDIN.
3. `JsonProcessor.parse(input)` is called.
4. `JsonProcessor` creates:
   - `Lexer` -> `tokenize()`
   - `Parser` -> `parse()`
5. If parsing succeeds:
   - print `Valid JSON`
   - print AST tree from `AstPrinter.print(value)`
   - optional pretty JSON from `PrettyPrinter.format(value)`
6. If parsing fails:
   - catch `ParseException`
   - print `Invalid JSON` and a precise location-aware error message
   - exit with status code `1`.

This gives both machine-style diagnostics and human-friendly output.

---

## 4. Lexical Analysis (`Lexer`)

### 4.1 Responsibility

`Lexer` scans the source text character by character and emits a list of tokens ending with `EOF`.

Each token captures:

- token type (`TokenType`)
- token lexeme (raw or decoded value)
- line
- column
- absolute character position

This location data is what powers accurate error reporting.

### 4.2 Token Types

Defined in `TokenType`:

- punctuation: `{`, `}`, `[`, `]`, `:`, `,`
- values: `STRING`, `NUMBER`, `TRUE`, `FALSE`, `NULL`
- sentinel: `EOF`

### 4.3 Whitespace Handling

`skipWhitespace()` consumes spaces, tabs, carriage returns, and newlines.
Newlines update internal `line` and `column` through `advance()`.

### 4.4 String Handling

`string(...)`:

- consumes until closing `"` appears
- supports escapes: `\" \\ \/ \b \f \n \r \t \uXXXX`
- decodes unicode escapes by reading exactly 4 hex digits
- rejects invalid escapes
- rejects unterminated strings (including newline inside string)

Stored token lexeme for strings is the decoded Java string value.

### 4.5 Number Handling

`number(...)` supports:

- optional leading `-`
- integer part
- optional fractional part (`.` + digits)
- optional exponent (`e`/`E`, optional sign, digits)

Validation rules include:

- no leading zeros like `01`
- at least one digit after decimal point
- exponent must include digits

### 4.6 Literals

Alphabetic sequences are parsed by `identifier(...)` and accepted only as:

- `true`
- `false`
- `null`

Any other identifier becomes a parse error.

### 4.7 Error Strategy

`Lexer` throws `ParseException` immediately on malformed input with exact position info.
That means invalid syntax is reported as early as possible.

---

## 5. Parsing (`Parser`)

### 5.1 Responsibility

`Parser` is a recursive-descent parser over the token list.
It builds strongly typed AST nodes and enforces grammar rules.

Core grammar:

```txt
value    -> object | array | string | number | true | false | null
object   -> '{' members? '}'
members  -> pair (',' pair)*
pair     -> string ':' value
array    -> '[' elements? ']'
elements -> value (',' value)*
```

### 5.2 Entry Point

`parse()`:

1. parse one value via `parseValue()`
2. require `EOF` after it

This prevents extra garbage after valid JSON (for example, `{"a":1} xyz`).

### 5.3 Object Parsing

`parseObject()`:

- consumes `{`
- supports empty object `{}` fast path
- loops over key-value pairs:
  - key must be `STRING`
  - `:` required
  - value parsed recursively
- separators are commas
- requires closing `}`

Data is stored in `LinkedHashMap`, preserving insertion order.

### 5.4 Array Parsing

`parseArray()`:

- consumes `[`
- supports empty array `[]`
- parses comma-separated values recursively
- requires closing `]`

### 5.5 Number Conversion

Lexer emits number lexeme as text; parser converts it using `Double.parseDouble`.
If conversion fails, parser raises a location-aware `ParseException`.

### 5.6 Error Strategy

`expect(...)` and `error(...)` centralize syntax checks and diagnostics.
When the expected token is missing, the parser reports the current token location.

---

## 6. AST Model

### 6.1 Interface + Node Types

All nodes implement marker interface `JsonValue`.

Concrete node classes:

- `JsonObject` -> `LinkedHashMap<String, JsonValue>`
- `JsonArray` -> `List<JsonValue>`
- `JsonString` -> `String`
- `JsonNumber` -> `double`
- `JsonBoolean` -> `boolean`
- `JsonNull` -> singleton instance `JsonNull.INSTANCE`

### 6.2 Design Notes

- `JsonNull` singleton avoids repeated allocations for null values.
- `JsonObject` uses insertion-order map, so output order matches input order.
- `JsonNumber` currently stores all numbers as `double`, which is practical for course scope but can lose precision for very large integers.

---

## 7. Output Components

### 7.1 `AstPrinter`

Purpose: developer-facing structural visualization of parsed JSON.

Output style examples:

- `OBJECT`
- `key: STRING("value")`
- `ARRAY`
- `NUMBER(5.75)`

Implementation details:

- recursive traversal
- 2-space indentation by depth
- includes object keys inline with child values
- trims trailing whitespace at end

This is not JSON output; it is a tree inspection view.

### 7.2 `PrettyPrinter`

Purpose: valid JSON serialization with consistent formatting.

Behavior:

- 2-space indentation
- multi-line formatting for non-empty arrays/objects
- proper comma placement (no trailing comma)
- escaping for string values and keys:
  - backslash, quotes, control characters
- numbers:
  - whole doubles print as integer text (`1.0` -> `1`)
  - fractional values preserve decimal form

This gives readable JSON while preserving AST semantics.

---

## 8. CLI Contract (`Main`)

Supported arguments:

- `--file <path>`: read file content
- `--pretty`: include pretty JSON output

Rules:

- unknown flag -> error + exit code 1
- `--file` without path -> error + exit code 1
- file I/O failure -> error + exit code 1
- parse failure -> `Invalid JSON` + detailed message + exit code 1
- parse success -> `Valid JSON` + AST (+ optional pretty block)

Input modes:

- file-based
- STDIN-based (scanner reads full stream)

---

## 9. Error Handling Model

`ParseException` stores:

- message
- line
- column
- absolute position

`getMessage()` appends location info in format:

`<message> (line X, column Y, position Z)`

This is useful in both tests and CLI output, especially for malformed documents where quick pinpointing matters.

---

## 10. Testing Walkthrough

`JsonProcessorTest` covers representative success and failure paths:

Success cases:

- simple object
- nested object/array + booleans/null
- empty object and empty array
- escaped strings with unicode
- pretty-printer formatting characteristics

Failure cases:

- missing comma
- missing closing brace
- invalid literal (`tru`)
- invalid number (`01`)

Coverage emphasis:

- parser correctness on core grammar
- lexer validation rules
- error message quality (contains meaningful diagnostics)

Test resource files provide realistic fixtures for valid/invalid payloads.

---

## 11. Build and Run

Requirements:

- Java 17+
- Maven 3+

Commands:

```bash
mvn test
```

```bash
mvn -q exec:java -Dexec.mainClass=com.jsonparser.Main -Dexec.args='--file src/test/resources/valid-complex.json --pretty'
```

Or pipe JSON through stdin:

```bash
echo '{"x":[1,2,3]}' | mvn -q exec:java -Dexec.mainClass=com.jsonparser.Main
```

If you do not use `exec-maven-plugin`, compile and run via your IDE or direct `java` command using compiled classes.

---

## 12. Known Limits and Improvement Ideas

Current constraints:

- numbers stored as `double` (precision tradeoff)
- no recovery after first parse error (fail-fast)
- no schema or semantic validation (syntax-only parser)
- no comments/trailing commas (strict JSON, by design)

Practical next upgrades:

1. Number precision mode (`BigDecimal` / `BigInteger`).
2. Richer diagnostics (token previews, caret snippets).
3. Serializer options (minified mode, custom indent width).
4. Visitor interface for AST processing.
5. Benchmarks for large inputs.
6. Optional streaming parser mode.

---

## 13. Why This Project Is Well-Structured for Learning

This codebase is a good educational parser project because:

- responsibilities are separated cleanly (lexing, parsing, AST, printing, CLI)
- the grammar is explicit and implemented directly in recursive descent
- diagnostics include precise source locations
- tests cover both happy-path and malformed input behavior
- extension points are obvious and low-risk

It is small enough to fully understand, but realistic enough to teach core language-processing concepts.
