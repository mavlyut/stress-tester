package parser;

import java.util.Iterator;

public class CharIterator implements Iterator<Character> {
    final char[] chars;
    int ind;
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

    public void expect(char x) throws ParserException {
        if (!take(x)) {
            throw exception("expected " + x + ", found " + peek());
        }
    }

    public void expect(boolean need, String x) throws ParserException {
        if (!need) {
            return;
        }
        skipWS();
        for (char c : x.toCharArray()) {
            expect(c);
        }
        skipWS();
    }

    public void expect(String x) throws ParserException {
        expect(true, x);
    }

    public boolean take(String x) {
        skipWS();
        int y = ind;
        for (char c : x.toCharArray()) {
            if (!take(c)) {
                ind = y;
                return false;
            }
        }
        skipWS();
        return true;
    }

    public String nextWord() {
        skipWS();
        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(peek())) {
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
}
