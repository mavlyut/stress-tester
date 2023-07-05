package generators;

import java.util.List;

public class GGroup extends AbstractGType {
    private final List<Object> group;

    public GGroup(List<Object> group) {
        this.group = group;
    }

    @Override
    protected Object generate(Variables vars) {
        return group.get(random.nextInt(0, group.size()));
    }
}
