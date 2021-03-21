package fr.unice.polytech.si3.qgl.soyouz.classes.utilities;

import java.util.stream.Stream;

public final class Util
{
    private Util(){}

    /**
     * Cast all element of a stream to a specific class.
     *
     * @param str The stream.
     * @param clazz The wanted class.
     * @param <T> The type of the class.
     * @return a stream of object of a specific casted type.
     */
    public static <T> Stream<T> filterType(Stream<?> str, Class<T> clazz)
    {
        return str
            .filter(clazz::isInstance)
            .map(clazz::cast);
    }
}
