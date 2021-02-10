/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodGet;
import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusInnerEnd extends SimpleStatusInner
{
    public static SimpleStatusInnerEnd of(ImmutableList<SimpleMethodAllResultEnd<?>> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusInnerEnd(result);
    }

    private SimpleStatusInnerEnd(ImmutableList<SimpleMethodAllResultEnd<?>> result)
    {
        super(result);
    }

    @Override
    public SimpleStatusOuterGet get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        SimpleMethodGet<?>[] method = new SimpleMethodGet[result.size()];
        result.toArray(method);
        return SimpleStatusOuterGet.of(method, new Phaser(1));
    }

    @Override
    public SimpleStatusOuterPst pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        SimpleMethodPst<?>[] method = new SimpleMethodPst[result.size()];
        result.toArray(method);
        return SimpleStatusOuterPst.of(method, new Phaser(1));
    }

    @Override
    public SimpleStatusOuterDel del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del中的Phaser是否已经终止
        SimpleMethodDel<?>[] method = new SimpleMethodDel[result.size()];
        result.toArray(method);
        return SimpleStatusOuterDel.of(method, new Phaser(1));
    }
}
