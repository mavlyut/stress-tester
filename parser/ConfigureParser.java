package parser;

import generators.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigureParser {
    private static final String USAGE = "\nConfigureParser <config> <count-of-tests> <test-dir> [<tests-prefix>]";
    private static final List<GType> EMPTYLIST = List.of();
    private final Map<String, GType> vars = new HashMap<>();
    private int unusedInd = 0;

    private ConfigureParser() {
    }

    private String getUnusedName() {
        while (vars.containsKey("unused" + unusedInd)) {
            unusedInd++;
        }
        return "unused" + (unusedInd++);
    }

    private List<List<GType>> parse(List<String> lines) throws ParserException {
        List<List<GType>> ans = new ArrayList<>();
        vars.clear();
        for (String line : lines) {
            List<GType> parsed = parseLine(line);
            assert parsed != null;
            if (!parsed.isEmpty()) {
                ans.add(parsed);
            }
        }
        return ans;
    }

    private interface NumberGetter<T extends Number> {
        T get() throws ParserException;
    }

    @SuppressWarnings("unchecked")
    private <T extends Number, R> GBound<R> parseRange(CharIterator it, NumberGetter<T> getter, Function<T, T> plus,
                                                    BiFunction<GBound<T>, GBound<T>, GBound<R>> toGenerator) throws ParserException {
        it.expect("[");
        String next = it.nextWord();
        GBound<T> l = next.isEmpty() ? new GConst<>(getter.get()) : (GBound<T>) vars.get(next);
        it.expect(";");
        next = it.nextWord();
        GBound<T> r = next.isEmpty() ? new GConst<>(plus.apply(getter.get())) : (GBound<T>) vars.get(next);
        GBound<R> ans = toGenerator.apply(l, r);
        it.expect("]");
        return ans;
    }

    private GChar parseCharRange(CharIterator it) throws ParserException {
        return (GChar)this.parseRange(it, () -> {
            int ans;
            if (it.take('\'')) {
                ans = it.take();
                it.expect('\'');
            } else {
                ans = it.nextIInt();
            }
            return ans;
        }, x -> x + 1, GChar::new);
    }

    private GType parseType(CharIterator it) throws ParserException {
        String type = it.nextWord();
        return switch (type) {
            case "char" -> {
                it.expect("in");
                yield parseCharRange(it);
            }
            case "int" -> {
                it.expect("in");
                yield parseRange(it, it::nextIInt, x -> x + 1, GInt::new);
            }
            case "long" -> {
                it.expect("in");
                yield parseRange(it, it::nextLInt, x -> x + 1, GLong::new);
            }
            case "float" -> {
                it.expect("in");
                yield parseRange(it, it::nextFInt, x -> x + GPrimitive.EPS, GFloat::new);
            }
            case "double" -> {
                it.expect("in");
                yield parseRange(it, it::nextDInt, x -> x + GPrimitive.EPS, GDouble::new);
            }
            case "array", "string" -> {
                it.expect("len");
                GType len;
                if (it.take("in")) {
                    len = parseRange(it, it::nextIInt, x -> x + 1, GInt::new);
                } else if (it.take("is")) {
                    len = vars.get(it.nextWord());
                } else {
                    throw it.exception("Expected \"in\" or \"is\" in array declaration");
                }
                it.expect("of");
                it.expect(type.equals("array"), "{");
                GType innerType = parseType(it);
                it.expect(type.equals("array"), "}");
                yield new GArray<>(len, innerType);
            }
            default -> throw it.exception("Unexpected type: " + type);
        };
    }

    private List<GType> parseLine(String line) throws ParserException {
        if (line.isBlank()) {
            return EMPTYLIST;
        }
        CharIterator it = new CharIterator(line);
        if (it.take("next")) {
            return null;
        }
        if (it.take("%")) {
            return EMPTYLIST;
        }
        List<GType> ans = new ArrayList<>();
        while (it.hasNext()) {
            if (!ans.isEmpty()) {
                it.expect(",");
            }
            String name = it.nextWord();
            if (name.isEmpty()) {
                name = getUnusedName();
            }
            it.expect(":");
            GType type = parseType(it);
            ans.add(type);
            vars.put(name, type);
        }
        it.expect(CharIterator.END);
        return ans;
    }

    private void nextTest(List<List<GType>> config, Path path) {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (List<GType> line : config) {
                for (GType type : line) {
                    bw.write(type.nextToString());
                    bw.write(' ');
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 3 && args.length != 4) {
            System.err.println(USAGE);
            return;
        }
        try {
            List<String> lines = Files.readAllLines(Path.of(args[0]));
            int n = Integer.parseInt(args[1]);
            Path testDir = Path.of(args[2]);
            if (Files.notExists(testDir)) {
                Files.createDirectories(testDir);
            }
            String prefix = (args.length == 3 ? "test" : args[3]);
            ConfigureParser parser = new ConfigureParser();
            List<List<GType>> config = parser.parse(lines);
            for (int i = 1; i <= n; i++) {
                parser.nextTest(config, testDir.resolve(prefix + i));
            }
        } catch (IOException e) {
            System.err.println("Can't read from file");
        } catch (ParserException e) {
            System.err.println(e.getMessage());
        }
    }
}
