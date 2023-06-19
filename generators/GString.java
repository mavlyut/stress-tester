package generators;

public class GString extends GArray<GChar> {
    public GString(GType len, char l, char r) {
        super(len, new GChar(l, r));
    }
}
