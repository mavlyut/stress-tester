package generators;

public class GChar implements GType, GBound<Character> {
    private final GInt gint;

    public GChar(GBound<Integer> left, GBound<Integer> right) {
        gint = new GInt(left, right);
    }

    @Override
    public Character cached() {
        return (char)gint.cached();
    }

    @Override
    public Character nextObject() {
        return (char)gint.nextObject();
    }
}
