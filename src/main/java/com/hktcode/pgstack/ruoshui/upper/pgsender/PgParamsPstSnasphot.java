/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgParamsPstSnasphot implements SimpleMethodPstParams<PgAction>
{
    public static PgParamsPstSnasphot of(PgConfigSnapshot config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new PgParamsPstSnasphot(config);
    }

    @Override
    public PgResult run(PgAction action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return action.pst(config);
    }

    private final PgConfigSnapshot config;

    private PgParamsPstSnasphot(PgConfigSnapshot config)
    {
        this.config = config;
    }
}
