package generators;

import java.util.HashMap;

public class Variables extends HashMap<String, GType> {
    private int unusedInd = 0;

    public Variables() {
        super();
    }

    public String getUnusedName() {
        while (containsKey("unused" + unusedInd)) {
            unusedInd++;
        }
        return "unused" + (unusedInd++);
    }

    @Override
    public GType put(String key, GType value) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key can't be empty");
        }
        if (containsKey(key)) {
            throw new IllegalArgumentException("Used key: " + key);
        }
        return super.put(key, value);
    }

    @Override
    public GType get(Object key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Variable with name " + key + " is not found");
        }
        return super.get(key);
    }
}
