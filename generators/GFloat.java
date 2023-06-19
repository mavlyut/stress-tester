package generators;

public class GFloat extends GPrimitive<Float> {
    public GFloat(float left, float right) {
        super(left, right + EPS, random::nextFloat);
    }
}
