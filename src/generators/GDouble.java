package generators;

public class GDouble extends GPrimitive<Double> {
    public GDouble(GBound<Double> left, GBound<Double> right) {
        super(left, right, random::nextDouble);
    }

    @Override
    protected Double getExclusiveBound(Double x) {
        return x + EPS;
    }
}
