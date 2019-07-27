/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class MainlineStepNormal extends MainlineStep
{
    MainlineStepNormal(MainlineSender sender)
    {
        super(sender);
    }

    abstract MainlineStep next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;
}
