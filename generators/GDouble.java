package generators;

public class GDouble extends GPrimitive<Double> {
    public GDouble(GBound<Double> left, GBound<Double> right) {
        super(left, right, random::nextDouble);
    }
}
