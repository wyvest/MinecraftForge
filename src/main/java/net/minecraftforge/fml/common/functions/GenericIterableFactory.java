package net.minecraftforge.fml.common.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.Iterator;

public class GenericIterableFactory {
    public static <T> Iterable<T> newCastingIterable(final Iterator<?> input, final Class<T> type) {
        return () -> Iterators.transform(input, new TypeCastFunction<>(type));
    }

    public static <T> Iterable<T> newCastingIterable(Iterable<?> input, Class<T> type) {
        return Iterables.transform(input, new TypeCastFunction<>(type));
    }
}
