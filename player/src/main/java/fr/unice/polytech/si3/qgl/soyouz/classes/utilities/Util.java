package fr.unice.polytech.si3.qgl.soyouz.classes.utilities;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Stream;

public final class Util
{
    private Util()
    {
    }

    /**
     * Cast all element of a stream to a specific class.
     *
     * @param str   The stream.
     * @param clazz The wanted class.
     * @param <T>   The type of the class.
     * @return a stream of object of a specific casted type.
     */
    public static <T> Stream<T> filterType(Stream<?> str, Class<T> clazz)
    {
        return str
            .filter(clazz::isInstance)
            .map(clazz::cast);
    }

    public static <T extends OnboardEntity> Stream<T> sortByX(Stream<T> str)
    {
        return str.sorted(Comparator.comparing(OnboardEntity::getX));
    }

    /**
     * Update the log level.
     */
    public static void updateLogLevel(Level logLevel)
    {
        var root = LogManager.getLogManager().getLogger("");
        root.setLevel(logLevel);
        Arrays.stream(root.getHandlers()).forEach(h -> h.setLevel(logLevel));
    }
}
