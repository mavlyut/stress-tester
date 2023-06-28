package generators;

public class GVar extends AbstractGExpression {
    private final String name;

    public GVar(String name) {
        this.name = name;
    }

    @Override
    public Object evaluate(Variables vars) {
        return vars.get(name).cached(vars);
    }
}
