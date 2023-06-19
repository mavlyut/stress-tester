package generators;

import java.util.function.BiFunction;

/**
 * Стандартная реализация {@link GType} для генерации примитивных типов.
 * Рассчитывается на то, что на примитивах введено отношение "<", и что
 * их случайная равномерная генерация может быть достигнута вызовом какого-то
 * метода {@link java.util.Random} в некоторых пределах.
 *
 * @param <T> примитивный тип
 */
public abstract class GPrimitive<T extends Comparable<T>> extends AbstractGType {
    private final T left, right;
    private final BiFunction<T, T, T> generator;
    protected final static float EPS = 1e-9f;

    /**
     * Создает новый генератор примитивного типа {@link T} в указанных границах.
     * @param left левая граница для генерации (включительно)
     * @param right правая граница для генерации (включительно)
     * @param generator метод для генерации случайного примитива из промежутка [a, b)
     */
    protected GPrimitive(T left, T right, BiFunction<T, T, T> generator) {
        if (left.compareTo(right) > 0) {
            throw new IllegalArgumentException("Left bound must be less or equals to right, found: " + left + ".." + right);
        }
        this.left = left;
        this.right = right;
        this.generator = generator;
    }

    @Override
    protected Object generate() {
        return generator.apply(left, right);
    }

    public T getLeftBound() {
        return left;
    }

    public T getRightBound() {
        return right;
    }
}
