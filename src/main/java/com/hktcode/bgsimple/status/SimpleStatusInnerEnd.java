/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusInnerEnd implements SimpleStatusInner
{
    public static SimpleStatusInnerEnd of(ImmutableList<SimpleMethodAllResultEnd> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusInnerEnd(result);
    }

    public final ImmutableList<SimpleMethodAllResultEnd> result;

    private SimpleStatusInnerEnd(ImmutableList<SimpleMethodAllResultEnd> result)
    {
        this.result = result;
    }

    @Override
    public SimpleStatus get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[result.size()];
        result.toArray(method);
        return SimpleStatusOuterGet.of(method, new Phaser(0));
    }

    @Override
    public SimpleStatus pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[result.size()];
        result.toArray(method);
        return SimpleStatusOuterPst.of(method, new Phaser(0));
    }

    @Override
    public SimpleStatus del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del中的Phaser是否已经终止
        SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[result.size()];
        result.toArray(method);
        return SimpleStatusOuterDel.of(method, new Phaser(0));
    }
}
