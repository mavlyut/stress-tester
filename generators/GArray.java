package generators;

public class GArray<T extends GType> extends AbstractGType {
    private final GType lenType;
    private final T innerType;

    public GArray(GType lenType, T innerType) {
        this.lenType = lenType;
        this.innerType = innerType;
    }

    private int getSize() {
        int ans = ((Number)lenType.cached()).intValue();
        if (ans < 0) {
            throw new IllegalArgumentException("LenType generate negative size: " + ans);
        }
        return ans;
    }

    @Override
    protected Object[] generate() {
        Object[] ans = new Object[getSize()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = innerType.nextObject();
        }
        return ans;
    }

    @Override
    public String nextToString() {
        StringBuilder ans = new StringBuilder();
        // fixme)
        char delimiter = (innerType instanceof GArray) ? '\n' : ' ';
        for (int i = 0; i < getSize(); i++) {
            ans.append(i == 0 ? "" : delimiter).append(innerType.nextToString());
        }
        return ans.toString();
    }
}
