package generators;

public class GChar extends GPrimitive<Character> {
    public GChar(char left, char right) {
        super(left, (char)(right + 1), (l, r) -> (char)random.nextInt(l, r));
    }
}
