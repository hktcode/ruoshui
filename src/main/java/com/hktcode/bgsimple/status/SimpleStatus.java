/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleWorker;

public interface SimpleStatus<W extends SimpleWorker<W, M>, M>
{
    SimpleStatus<W, M> get(SimpleStatusOuterGet<W, M> get);

    SimpleStatus<W, M> pst(SimpleStatusOuterPst<W, M> pst);

    SimpleStatus<W, M> del(SimpleStatusOuterDel<W, M> del);
}
