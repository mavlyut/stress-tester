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
public abstract class GPrimitive<T extends Comparable<T>> extends AbstractGType implements GBound<T> {
    protected final GBound<T> left, right;
    private final BiFunction<T, T, T> generator;
    public final static float EPS = 1e-9f;

    /**
     * Создает новый генератор примитивного типа {@link T} в указанных границах.
     *
     * @param left      левая граница для генерации (включительно)
     * @param right     правая граница для генерации (невключительно)
     * @param generator метод для генерации случайного примитива из промежутка [a, b)
     */
     protected GPrimitive(GBound<T> left, GBound<T> right, BiFunction<T, T, T> generator) {
        this.left = left;
        this.right = right;
        this.generator = generator;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T generate() {
         try {
             return generator.apply((T) left.cached(), (T) right.cached());
         } catch (Throwable e) {
             System.out.println(left.cached() + " " + right.cached());
             throw e;
         }
    }
}
