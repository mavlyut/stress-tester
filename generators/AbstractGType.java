package generators;

/**
 * НЕ ПЕРЕОПРЕДЕЛЯТЬ {@link #cached()}, {@link #nextObject()}!!!
 */
public abstract class AbstractGType implements GType {
    /**
     * "Закешированное" значение -- последний результат вызова метода {@link #nextObject()}.
     */
    private Object cached = null;

    @Override
    public Object cached() {
        return cached;
    }

    @Override
    public Object nextObject() {
        cached = generate();
        return cached;
    }

    protected abstract Object generate();
}
