package generators;

public class GInt extends GPrimitive<Integer> {
    public GInt(int left, int right) {
        super(left, right + 1, random::nextInt);
    }
}
