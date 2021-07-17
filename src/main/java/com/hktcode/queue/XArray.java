package com.hktcode.queue;

import java.util.Arrays;
import java.util.Iterator;

public class XArray<E>
{
    public static <E> XArray<E> of(int maxCapacity)
    {
        return new XArray<>(maxCapacity);
    }

    private Object[] list;

    private int size = 0;

    private XArray(int maxCapacity)
    {
        this.list = new Object[maxCapacity];
    }

    public boolean add(E item)
    {
        if (size >= this.list.length) {
            return false;
        }
        this.list[this.size++] = item;
        return true;
    }

    public void clear(boolean setNullable)
    {
        if (setNullable) {
            // 赋值为null，减少内存泄露。
            Arrays.fill(this.list, null);
        }
        this.size = 0;
    }

    public Iterator<E> iterator()
    {
        return new XIterator<>(this);
    }

    public int getCapacity()
    {
        return this.list.length;
    }

    public int getSize()
    {
        return this.size;
    }

    public void setCapacity(int maxCapacity)
    {
        if (maxCapacity < this.size) {
            return;
        }
        Object[] l = new Object[maxCapacity];
        System.arraycopy(this.list, 0, l, 0, this.size);
        this.list = l;
    }

    public static class XIterator<E> implements Iterator<E>
    {
        private XIterator(XArray<E> xarray)
        {
            this.xarray = xarray;
        }

        private final XArray<E> xarray;

        private int index = 0;

        @Override
        public boolean hasNext()
        {
            return index < xarray.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next()
        {
            return (E)xarray.list[index++];
        }
    }
}
