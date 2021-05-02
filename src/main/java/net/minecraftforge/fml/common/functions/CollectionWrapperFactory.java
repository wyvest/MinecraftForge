package net.minecraftforge.fml.common.functions;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class CollectionWrapperFactory {
    /**
     * Return a read only cast view of the supplied non-generic collection, based on the element type given
     *
     * @param coll        The collection to cast
     * @param elementType the supertype contained in the collection
     * @return a collection asserting that type relationship
     */
    public static <T> Collection<T> wrap(Collection<Object> coll, Class<T> elementType) {
        return Collections2.transform(coll, new TypeCastFunction<>(elementType));
    }

    /**
     * Return a read only cast view of the supplied non-generic list, based on the element type given
     *
     * @param list        The list to cast
     * @param elementType the supertype contained in the list
     * @return a list asserting that type relationship
     */
    public static <T> List<T> wrap(List<Object> list, Class<T> elementType) {
        return Lists.transform(list, new TypeCastFunction<>(elementType));
    }
}
