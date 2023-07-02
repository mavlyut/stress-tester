package generators;

public class GIntConst extends AbstractGExpression {
    private final int value;

    public GIntConst(int value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Variables vars) {
        return value;
    }
}
