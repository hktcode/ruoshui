/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public class PgMetricUnk implements PgMetricErr
{
    public static PgMetricUnk of()
    {
        return new PgMetricUnk();
    }

    public final String errormsg = "UNEXPECTED FAILURE";

    private PgMetricUnk()
    {
    }
}
