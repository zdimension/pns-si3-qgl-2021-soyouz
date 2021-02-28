package fr.unice.polytech.si3.qgl.soyouz.classes.utilities;

import java.util.stream.Stream;

public final class Util
{
    public static <T> Stream<T> filterType(Stream<?> str, Class<T> clazz)
    {
        return str
            .filter(clazz::isInstance)
            .map(clazz::cast);
    }
}
