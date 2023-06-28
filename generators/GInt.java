package generators;

public class GInt extends GPrimitive<Integer> {
    public GInt(GBound<Integer> left, GBound<Integer> right) {
        super(left, right, random::nextInt);
    }

    @Override
    protected Integer getExclusiveBound(Integer x) {
        return x + 1;
    }
}
