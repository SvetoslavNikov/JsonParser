# JSON Parser

Small Java project for parsing and validating JSON without external parsing libraries.

It supports:
- objects and arrays
- strings, numbers, booleans, and `null`
- AST printing
- pretty-printing with `--pretty`

## Requirements

- Java 17
- Maven

## Run

```bash
mvn compile
```

### Command options

- `--file <path>` reads JSON from a file
- `--pretty` prints formatted JSON after parsing

### Examples with the sample resources

Simple valid JSON:

```bash
java -cp target/classes com.jsonparser.Main --file src/test/resources/valid-simple.json
```

Simple valid JSON with pretty print:

```bash
java -cp target/classes com.jsonparser.Main --file src/test/resources/valid-simple.json --pretty
```

Complex valid JSON:

```bash
java -cp target/classes com.jsonparser.Main --file src/test/resources/valid-complex.json
```

Complex valid JSON with pretty print:

```bash
java -cp target/classes com.jsonparser.Main --file src/test/resources/valid-complex.json --pretty
```

Invalid JSON example:

```bash
java -cp target/classes com.jsonparser.Main --file src/test/resources/invalid-missing-comma.json
```

You can also pipe JSON through standard input:

```bash
echo '{"name":"Ivan","age":22}' | java -cp target/classes com.jsonparser.Main
```

And with pretty print:

```bash
echo '{"name":"Ivan","age":22}' | java -cp target/classes com.jsonparser.Main --pretty
```

## Test

```bash
mvn test
```
