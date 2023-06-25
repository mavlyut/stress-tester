package generators;

// TODO: documentation
/**
 * Константный псевдогенератор. Возвращает всегда одно и тоже значение {@code value}.
 * Лучше, чем {@code new GPrimitive(value, value, (_, _) -> value)} тем,
 * что не аллоцирует лишнюю память для хранения закешированного значения.
 *
 * @param <T> тип хранимого значения
 */
public class GConst<T> implements GType, GBound<T> {
    private final T value;

    /**
     * Создает новый константный генератор.
     *
     * @param value возвращаемое значение
     */
    public GConst(T value) {
        this.value = value;
    }

    @Override
    public T cached() {
        return value;
    }

    @Override
    public T nextObject() {
        return value;
    }
}
