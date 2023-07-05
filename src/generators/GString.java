package generators;

public class GString extends GArray {
    public GString(String lenName, String innerName) {
        super(lenName, innerName);
    }

    @Override
    public String nextToString(Variables vars) {
        StringBuilder ans = new StringBuilder();
        GType innerType = vars.get(innerName);
        for (int i = 0; i < getSize(vars); i++) {
            ans.append(innerType.nextToString(vars));
        }
        return ans.toString();
    }
}
