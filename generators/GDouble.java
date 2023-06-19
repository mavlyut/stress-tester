package generators;

public class GDouble extends GPrimitive<Double> {
    public GDouble(double left, double right) {
        super(left, right + EPS, random::nextDouble);
    }
}
