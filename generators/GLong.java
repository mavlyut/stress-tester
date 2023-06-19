package generators;

public class GLong extends GPrimitive<Long> {
    public GLong(long left, long right) {
        super(left, right + 1, random::nextLong);
    }
}
