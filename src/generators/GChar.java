package generators;

public class GChar implements GType, GBound<Character> {
    private final GInt gint;

    public GChar(GBound<Integer> left, GBound<Integer> right) {
        gint = new GInt(left, right);
    }

    @Override
    public Character cached(Variables vars) {
        return (char) gint.cached(vars);
    }

    @Override
    public Character nextObject(Variables vars) {
        return (char)((Number)gint.nextObject(vars)).intValue();
    }
}
