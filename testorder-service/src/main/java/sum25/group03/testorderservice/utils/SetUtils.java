package sum25.group03.testorderservice.utils;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {

    // get only what's inside set A but not in set B
    public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
        Set<T> difference = new HashSet<>(setA);
        difference.removeAll(setB);
        return difference;
    }
}
