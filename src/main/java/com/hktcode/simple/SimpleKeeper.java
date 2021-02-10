package com.hktcode.simple;

@FunctionalInterface
public interface SimpleKeeper<E, R>
{
    R apply(E entity, long deletets);
}
