package com.hktcode.simple;

public class SimpleAction<E>
{
    protected final SimpleHolder<E> holder;

    protected SimpleAction(SimpleHolder<E> holder)
    {
        this.holder = holder;
    }
}
