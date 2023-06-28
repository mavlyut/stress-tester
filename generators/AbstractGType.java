package generators;

/**
 * НЕ ПЕРЕОПРЕДЕЛЯТЬ {@link #cached(Variables)}, {@link #nextObject(Variables)}!!!
 */
public abstract class AbstractGType implements GType {
    /**
     * "Закешированное" значение -- последний результат вызова метода {@link #nextObject(Variables)}.
     */
    private Object cached = null;

    @Override
    public Object cached(Variables vars) {
        return cached;
    }

    @Override
    public Object nextObject(Variables vars) {
        cached = generate(vars);
        return cached;
    }

    protected abstract Object generate(Variables vars);
}
