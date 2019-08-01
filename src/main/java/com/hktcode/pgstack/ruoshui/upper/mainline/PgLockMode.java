/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public enum PgLockMode
{
    ACCESS_SHARE("ACCESS SHARE"),

    ROW_SHARE("ROW SHARE"),

    ROW_EXCLUSIVE("ROW EXCLUSIVE"),

    SHARE_UPDATE_EXCLUSIVE("SHARE UPDATE EXLUSIVE"),

    SHARE("SHARE"),

    SHARE_ROW_EXCLUSIVE("SHARE ROW EXCLUSIVE"),

    EXCLUSIVE("EXCLUSIVE"),

    ACCESS_EXCLUSIVE("ACCESS EXCLUSIVE");

    public final String textFormat;

    private PgLockMode(String textFormat)
    {
        this.textFormat = textFormat;
    }
}
