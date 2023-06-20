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

public class ConfigureParser {
    private static final String USAGE = "\nConfigureParser <config> <count-of-tests> <test-dir> [<tests-prefix>]";
    private static int unusedInd = 0;
    private static final List<GType> EMPTYLIST = List.of();

    private ConfigureParser() {
    }

    private static String getUnusedName() {
        return "unused" + (unusedInd++);
    }

    private static List<List<GType>> parse(List<String> lines) throws ParserException {
        List<List<GType>> ans = new ArrayList<>();
        Map<String, GType> vars = new HashMap<>();
        for (String line : lines) {
            List<GType> parsed = parseLine(line, vars);
            if (!parsed.isEmpty()) {
                ans.add(parsed);
            }
        }
        return ans;
    }

    private interface NumberGetter<T extends Number> {
        T get() throws ParserException;
    }

    private static <T extends Number, R extends GType>
    R parseRange(CharIterator it, NumberGetter<T> getter, BiFunction<T, T, R> toGenerator) throws ParserException {
        it.expect("[");
        T l = getter.get();
        it.expect(";");
        T r = getter.get();
        R ans = toGenerator.apply(l, r);
        it.expect("]");
        return ans;
    }

    private static GChar parseCharRange(CharIterator it) throws ParserException {
        return parseRange(it, () -> {
            int ans;
            if (it.take('\'')) {
                ans = it.take();
                it.expect('\'');
            } else {
                ans = (char) it.nextIInt();
            }
            return ans;
        }, (l, r) -> new GChar((char) (l.intValue()), (char) (r.intValue())));
    }

    private static GType parseType(CharIterator it, Map<String, GType> vars) throws ParserException {
        String type = it.nextWord();
        return switch (type) {
            case "char" -> {
                it.expect("in");
                yield parseCharRange(it);
            }
            case "int" -> {
                it.expect("in");
                yield parseRange(it, it::nextIInt, GInt::new);
            }
            case "long" -> {
                it.expect("in");
                yield parseRange(it, it::nextLInt, GLong::new);
            }
            case "float" -> {
                it.expect("in");
                yield parseRange(it, it::nextFInt, GFloat::new);
            }
            case "double" -> {
                it.expect("in");
                yield parseRange(it, it::nextDInt, GDouble::new);
            }
            case "array", "string" -> {
                it.expect("len");
                GType len;
                if (it.take("in")) {
                    len = parseRange(it, it::nextIInt, GInt::new);
                } else if (it.take("is")) {
                    len = new GConst<>(vars.get(it.nextWord()));
                } else {
                    throw it.exception("Expected \"in\" or \"is\" in array declaration");
                }
                it.expect("of");
                if (type.equals("array")) {
                    it.expect("{");
                    GType innerType = parseType(it, vars);
                    it.expect("}");
                    yield new GArray<>(len, innerType);
                }
                GChar innerType = parseCharRange(it);
                yield new GString(len, innerType.getLeftBound(), innerType.getRightBound());
            }
            default -> throw it.exception("Unexpected type: " + type);
        };
    }

    private static List<GType> parseLine(String line, Map<String, GType> vars) throws ParserException {
        if (line.isBlank()) {
            return EMPTYLIST;
        }
        CharIterator it = new CharIterator(line);
        if (it.take("next")) {
            return null;
        }
        if (it.take("//")) {
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
            GType type = parseType(it, vars);
            ans.add(type);
            vars.put(name, type);
        }
        it.expect(CharIterator.END);
        return ans;
    }

    private static void nextTest(List<List<GType>> config, Path path) {
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
            List<List<GType>> config = parse(lines);
            for (int i = 1; i <= n; i++) {
                nextTest(config, testDir.resolve(prefix + i));
            }
        } catch (IOException e) {
            System.err.println("Can't read from file");
        } catch (ParserException e) {
            System.err.println(e.getMessage());
        }
    }
}
