package generators;

public class GInt extends GPrimitive<Integer> {
    // [left, right)
    public GInt(GBound<Integer> left, GBound<Integer> right) {
        super(left, right, random::nextInt);
    }
}
