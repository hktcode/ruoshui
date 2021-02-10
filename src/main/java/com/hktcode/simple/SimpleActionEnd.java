package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleActionEnd<E> extends SimpleAction<E>
{
    public static <E> SimpleActionEnd<E> of(SimpleHolder<E> holder)
    {
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new SimpleActionEnd<>(holder);
    }

    protected SimpleActionEnd(SimpleHolder<E> holder)
    {
        super(holder);
    }
}
