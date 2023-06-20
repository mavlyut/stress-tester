package generators;

// TODO: documentation
/**
 * Константный псевдогенератор. Возвращает всегда одно и тоже значение {@code value}.
 * Лучше, чем {@code new GPrimitive(value, value, (_, _) -> value)} тем,
 * что не аллоцирует лишнюю память для хранения закешированного значения.
 *
 * @param <T> тип хранимого значения
 */
public class GConst<T extends GType> implements GType {
    private final T gen;

    /**
     * Создает новый константный генератор.
     *
     * @param gen возвращаемое значение
     */
    public GConst(T gen) {
        this.gen = gen;
    }

    @Override
    public Object cached() {
        return gen.cached();
    }

    @Override
    public Object nextObject() {
        return gen.nextObject();
    }
}
