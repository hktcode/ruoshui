/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleWorker;

public interface SimpleStatus
{
    SimpleStatus get(SimpleStatusOuterGet get);

    SimpleStatus pst(SimpleStatusOuterPst pst);

    SimpleStatus del(SimpleStatusOuterDel del);
}
