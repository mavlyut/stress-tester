package generators;

public abstract class AbstractGExpression implements GExpression {
    protected AbstractGExpression() {
    }

    @Override
    public Object cached(Variables vars) {
        return evaluate(vars);
    }

    @Override
    public Object nextObject(Variables vars) {
        return cached(vars);
    }
}
