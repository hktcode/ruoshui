/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.pgsender.PgResult;

public class FetchThreadThrowsErrorException extends RuntimeException
{
    public final UpcsmReportSender sender;

    public FetchThreadThrowsErrorException(UpcsmReportSender sender)
    {
        this.sender = sender;
    }
}
