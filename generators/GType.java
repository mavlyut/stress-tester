package generators;

import java.util.Arrays;
import java.util.Random;

/**
 * Реализации интерфейса {@link GType} -- генераторы различных типов.
 * Будем поддерживать инвариант, что после вызова {@link #nextObject()}
 * значение кешируется, и может быть возвращено методом {@link #cached()}.
 * <br><br>
 * Обратите внимание, что не стоит использовать один и тот же неанонимный генератор:
 * приведенный ниже код генерирует число <i>n, m</i>, и массив <i>a</i> длины <i>n</i>.
 * <br>
 * {@code var n = new GInt(0, 100);}<br>
 * {@code var m = new GInt(0, 100);}<br>
 * {@code var a = new GArray<>(n, new GInt(0, 100));}<br>
 * При этом код не может быть "соптимизирован" до <br>
 * {@code var n = new GInt(0, 100);}<br>
 * {@code var m = n;}<br>
 * {@code var a = new GArray<>(n, n);}<br>
 * поскольку вызов метода {@link #nextObject()} перетрет закешированное значение <i>n</i>,
 * и массив будет уже длины <i>m</i>, а после генерации массива в <i>n</i> лежит уже
 * даже и не <i>m</i>.
 */
public interface GType {
    Random random = new Random();

    /**
     * Возвращает "закешированное" значение -- последний результат вызова метода
     * {@link #nextObject()} (дефолтная реализация метода {@link #nextToString()}
     * внутри вызывает {@link #nextObject()}).
     *
     * @return закешированное значение
     */
    Object cached();

    /**
     * Генерирует случайный объект. Обновляет закешированное значение.
     *
     * @return следующий случайный объект
     */
    Object nextObject();

    /**
     * Возвращает строковое представление следующего случайного объекта.
     * Кешируемое значение обновляется. У всех зависимых имплементаций {@link GType}
     * также будет обновлено кешируемое значение, если метод не переопределен.
     *
     * @return строковое представление следующего объекта
     */
    default String nextToString() {
        Object ret = nextObject();
        if (ret instanceof Object[] arr) {
            return Arrays.asList(arr).toString();
        }
        return ret.toString();
    }

    default String getName() {
        return this.getClass().getTypeName();
    }
}
