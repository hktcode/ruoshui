/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlKey;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlVal;

public class UpperProducerRecord
{
    public static UpperProducerRecord of(PgsqlKey key, PgsqlVal val)
    {
        if (key == null) {
            throw new ArgumentNullException("key");
        }
        if (val == null) {
            throw new ArgumentNullException("val");
        }
        return new UpperProducerRecord(key, val);
    }

    public final PgsqlKey key;

    public final PgsqlVal val;

    private UpperProducerRecord(PgsqlKey key, PgsqlVal val)
    {
        this.key = key;
        this.val = val;
    }
}
