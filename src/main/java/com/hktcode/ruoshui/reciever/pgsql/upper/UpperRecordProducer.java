/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;

public class UpperRecordProducer
{
    public static UpperRecordProducer of(PgsqlKey key, PgsqlVal val)
    {
        if (key == null) {
            throw new ArgumentNullException("key");
        }
        if (val == null) {
            throw new ArgumentNullException("val");
        }
        return new UpperRecordProducer(key, val);
    }

    public final PgsqlKey key;

    public final PgsqlVal val;

    private UpperRecordProducer(PgsqlKey key, PgsqlVal val)
    {
        this.key = key;
        this.val = val;
    }
}
