package generators;

/**
 * Арифметические выражения. Поскольку здесь генерировать нечего,
 * {@link #nextObject(Variables)} возвращает тоже самое, что и {@link #cached(Variables)}
 *
 * Для простоты будем считать, что арифметические операции возможны только над
 * целочисленными типами.
 */
public interface GExpression extends GType, GBound<Integer> {
    Object evaluate(Variables vars);

    default int intEvaluate(Variables vars) {
        return ((Number)evaluate(vars)).intValue();
    }
}
