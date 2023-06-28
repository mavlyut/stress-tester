package generators;

public sealed class AbstractGBinaryExpression extends AbstractGExpression {
    private final GExpression left, right;
    private final BinaryOperation op;

    protected AbstractGBinaryExpression(GExpression left, GExpression right, BinaryOperation op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public Object evaluate(Variables vars) {
        return op.apply(left.intEvaluate(vars), right.intEvaluate(vars));
    }

    @FunctionalInterface
    protected interface BinaryOperation {
        int apply(int x, int y);
    }

    public static final class GAdd extends AbstractGBinaryExpression {
        public GAdd(GExpression left, GExpression right) {
            super(left, right, Integer::sum);
        }
    }

    public static final class GSubtract extends AbstractGBinaryExpression {
        public GSubtract(GExpression left, GExpression right) {
            super(left, right, (a, b) -> a - b);
        }
    }

    public static final class GMultiply extends AbstractGBinaryExpression {
        public GMultiply(GExpression left, GExpression right) {
            super(left, right, (a, b) -> a * b);
        }
    }

    public static final class GDivide extends AbstractGBinaryExpression {
        public GDivide(GExpression left, GExpression right) {
            super(left, right, (a, b) -> a / b);
        }
    }

    public static final class GMin extends AbstractGBinaryExpression {
        public GMin(GExpression left, GExpression right) {
            super(left, right, Integer::min);
        }
    }

    public static final class GMax extends AbstractGBinaryExpression {
        public GMax(GExpression left, GExpression right) {
            super(left, right, Integer::max);
        }
    }
}
