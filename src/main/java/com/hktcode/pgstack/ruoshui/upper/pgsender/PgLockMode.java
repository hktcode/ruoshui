/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

import java.util.Objects;

public enum PgLockMode
{
    NULL_LOCK("null"),

    NO_LOCK(""),

    ACCESS_SHARE("ACCESS SHARE"),

    ROW_SHARE("ROW SHARE"),

    ROW_EXCLUSIVE("ROW EXCLUSIVE"),

    SHARE_UPDATE_EXCLUSIVE("SHARE UPDATE EXCLUSIVE"),

    SHARE("SHARE"),

    SHARE_ROW_EXCLUSIVE("SHARE ROW EXCLUSIVE"),

    EXCLUSIVE("EXCLUSIVE"),

    ACCESS_EXCLUSIVE("ACCESS EXCLUSIVE");

    public static PgLockMode of(String textFormat)
    {
        if (textFormat == null) {
            throw new ArgumentNullException("textFormat");
        }
        PgLockMode[] values = PgLockMode.values();
        for (int i = 0; i < values.length; ++i) {
            if (Objects.equals(values[i].textFormat, textFormat)) {
                return values[i];
            }
        }
        return NO_LOCK;
    }

    public final String textFormat;

    private PgLockMode(String textFormat)
    {
        this.textFormat = textFormat;
    }
}
