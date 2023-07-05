package parser;

import java.util.Iterator;

public class CharIterator implements Iterator<Character> {
    private final char[] chars;
    private int ind;
    public final static char END = '\0';

    public ParserException exception(String message) {
        return new ParserException(message, ind);
    }

    public CharIterator(String x) {
        chars = x.toCharArray();
        ind = 0;
    }

    private void skipWS() {
        while (ind < chars.length && Character.isWhitespace(chars[ind])) {
            ind++;
        }
    }

    @Override
    public boolean hasNext() {
        return ind < chars.length;
    }

    @Override
    public void remove() {
        if (ind-- == 0) {
            Iterator.super.remove();
        }
    }

    @Override
    public Character next() {
        ++ind;
        return peek();
    }

    private char peek() {
        return hasNext() ? chars[ind] : END;
    }

    public char take() {
        char ans = peek();
        ind++;
        return ans;
    }

    public boolean take(char x) {
        if (peek() == x) {
            ind++;
            return true;
        }
        return false;
    }

    /**
     * задумайтесь перед использование ({@link #expect(String)} скипает WS)
     */
    public void expect(char x) throws ParserException {
        if (!take(x)) {
            throw exception("expected " + x + ", found " + peek());
        }
    }

    public void expect(String expected) throws ParserException {
        skipWS();
        for (char c : expected.toCharArray()) {
            expect(c);
        }
        skipWS();
    }

    public boolean take(String test) {
        skipWS();
        int y = ind;
        for (char c : test.toCharArray()) {
            if (!take(c)) {
                ind = y;
                return false;
            }
        }
        skipWS();
        return true;
    }

    private boolean isAlpha(boolean first) {
        if (Character.isAlphabetic(peek())) {
            return true;
        }
        return Character.isDigit(peek()) && !first;
    }

    public String nextWord() {
        skipWS();
        StringBuilder sb = new StringBuilder();
        while (isAlpha(sb.isEmpty())) {
            sb.append(take());
        }
        skipWS();
        return sb.toString();
    }

    public int nextIInt() {
        skipWS();
        boolean negate = take('-');
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek())) {
            sb.append(take());
        }
        skipWS();
        return (negate ? -1 : 1) * Integer.parseInt(sb.toString());
    }

    public long nextLInt() {
        skipWS();
        boolean negate = take('-');
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek())) {
            sb.append(take());
        }
        skipWS();
        return (negate ? -1 : 1) * Long.parseLong(sb.toString());
    }

    public float nextFInt() {
        skipWS();
        boolean negate = take('-');
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek()) || peek() == '.') {
            sb.append(take());
        }
        skipWS();
        return (negate ? -1 : 1) * Float.parseFloat(sb.toString());
    }

    public double nextDInt() {
        skipWS();
        boolean negate = take('-');
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek()) || peek() == '.') {
            sb.append(take());
        }
        skipWS();
        return (negate ? -1 : 1) * Double.parseDouble(sb.toString());
    }

    public boolean between(char l, char r) {
        return peek() != END && l <= peek() && peek() <= r;
    }

    public Object parseConstValue() throws ParserException {
        if (take("\"")) {
            String ans = nextWord();
            expect("\"");
            return ans;
        }
        return nextDInt();
    }
}
