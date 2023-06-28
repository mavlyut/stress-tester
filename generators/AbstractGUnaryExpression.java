package generators;

public sealed class AbstractGUnaryExpression extends AbstractGExpression {
    private final GExpression right;
    private final UnaryOperation op;

    protected AbstractGUnaryExpression(GExpression right, UnaryOperation op) {
        this.right = right;
        this.op = op;
    }

    @Override
    public Object evaluate(Variables vars) {
        return op.apply(right.intEvaluate(vars));
    }

    private interface UnaryOperation {
        int apply(int x);
    }

    public static final class GNegate extends AbstractGUnaryExpression {
        public GNegate(GExpression right) {
            super(right, x -> -x);
        }
    }

    public static final class GAbs extends AbstractGUnaryExpression {
        public GAbs(GExpression right) {
            super(right, Math::abs);
        }
    }
}
