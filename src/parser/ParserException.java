package parser;

public class ParserException extends Exception {
    public ParserException(String message, int ind) {
        super("Exception until parsing at position " + ind + ": " + message);
    }
}
