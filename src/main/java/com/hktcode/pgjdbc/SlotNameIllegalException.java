/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * illegal logical replication slot name exception.
 */
public class SlotNameIllegalException extends RuntimeException
{
    /**
     * the illegal slot name.
     */
    public final String slotName;

    /**
     * constructor.
     *
     * @param slotName the illegal slot name
     */
    public SlotNameIllegalException(String slotName)
    {
        super(buildDescription(slotName));
        if (slotName == null) {
            throw new ArgumentNullException("slotName");
        }
        this.slotName = slotName;
    }

    /**
     * build the message of {@link SlotNameIllegalException}.
     *
     * @param slotName the illegal slot name.
     *
     * @return the message of {@link SlotNameIllegalException}.
     */
    private static String buildDescription(String slotName)
    {
        slotName = (slotName == null ? "" : slotName);
        if ("".equals(slotName)) {
            return "empty slot name";
        }
        return String.format(
            "replication slot name contains invalid character: slotName=%s", slotName);
    }
}
