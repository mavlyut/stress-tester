package generators;

public class GFloat extends GPrimitive<Float> {
    public GFloat(GBound<Float> left, GBound<Float> right) {
        super(left, right, random::nextFloat);
    }
}
