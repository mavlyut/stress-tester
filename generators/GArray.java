package generators;

public class GArray extends AbstractGType {
    private final String lenName, innerName;

    public GArray(String lenName, String innerName) {
        this.lenName = lenName;
        this.innerName = innerName;
    }

    private int getSize(Variables vars) {
        int ans = ((Number) vars.get(lenName).cached(vars)).intValue();
        if (ans < 0) {
            throw new IllegalArgumentException("LenType generate negative size: " + ans);
        }
        return ans;
    }

    @Override
    protected Object[] generate(Variables vars) {
        Object[] ans = new Object[getSize(vars)];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = vars.get(innerName).nextObject(vars);
        }
        return ans;
    }

    @Override
    public String nextToString(Variables vars) {
        StringBuilder ans = new StringBuilder();
        GType innerType = vars.get(innerName);
        char delimiter = (innerType instanceof GArray) ? '\n' : ' ';
        for (int i = 0; i < getSize(vars); i++) {
            ans.append(i == 0 ? "" : delimiter).append(innerType.nextToString(vars));
        }
        return ans.toString();
    }
}
