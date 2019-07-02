/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class MainlineActionNormal extends MainlineAction
{
    MainlineActionNormal(MainlineSender sender)
    {
        super(sender);
    }

    abstract MainlineAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;
}
