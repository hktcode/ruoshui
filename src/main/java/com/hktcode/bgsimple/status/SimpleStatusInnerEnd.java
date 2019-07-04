/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusInnerEnd<W extends SimpleWorker<W, M>, M> //
    extends SimpleStatusInner<W, M>
{
    public static <T extends SimpleWorker<T, M>, M> //
    SimpleStatusInnerEnd<T, M> of(ImmutableList<SimpleMethodAllResultEnd<T, M>> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusInnerEnd<>(result);
    }

    private final ImmutableList<SimpleMethodAllResultEnd<W, M>> result;

    private SimpleStatusInnerEnd(ImmutableList<SimpleMethodAllResultEnd<W, M>> result)
    {
        this.result = result;
    }

    @Override
    public SimpleStatus<W, M> get(SimpleStatusOuterGet<W, M> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        @SuppressWarnings("unchecked")
        SimpleMethodAllResultEnd<W, M>[] method = new SimpleMethodAllResultEnd[result.size()];
        result.toArray(method);
        return SimpleStatusOuterGet.of(method, new Phaser(0));
    }

    @Override
    public SimpleStatus<W, M> pst(SimpleStatusOuterPst<W, M> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        @SuppressWarnings("unchecked")
        SimpleMethodAllResultEnd<W, M>[] method = new SimpleMethodAllResultEnd[result.size()];
        result.toArray(method);
        return SimpleStatusOuterPst.of(method, new Phaser(0));
    }

    @Override
    public SimpleStatus<W, M> del(SimpleStatusOuterDel<W, M> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del中的Phaser是否已经终止
        @SuppressWarnings("unchecked")
        SimpleMethodAllResultEnd<W, M>[] method = new SimpleMethodAllResultEnd[result.size()];
        result.toArray(method);
        return SimpleStatusOuterDel.of(method, new Phaser(0));
    }
}
