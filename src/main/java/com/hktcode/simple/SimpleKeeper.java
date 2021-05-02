package com.hktcode.simple;

@FunctionalInterface
public interface SimpleKeeper<R>
{
    R apply(SimplePhaserInner inner, SimplePhaserOuter outer);
}
