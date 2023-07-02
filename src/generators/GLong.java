package generators;

public class GLong extends GPrimitive<Long> {
    public GLong(GBound<Long> left, GBound<Long> right) {
        super(left, right, random::nextLong);
    }

    @Override
    protected Long getExclusiveBound(Long x) {
        return x + 1L;
    }
}
