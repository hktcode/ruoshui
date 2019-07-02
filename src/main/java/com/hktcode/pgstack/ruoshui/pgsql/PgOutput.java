/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.hktcode.lang.exception.ArgumentNullException;

import java.util.HashMap;
import java.util.Map;

/**
 * the utils of Pgoutput Logical Decoding Plugin.
 */
public class PgOutput
{
    /**
     * create the default pgoutput logical repliation stream options.
     *
     * @param publicationNames Comma separated list of publication names for
     *                         which to subscribe (receive changes).
     *                         The individual publication names are treated
     *                         as standard objects names and can be quoted
     *                         the same as needed.
     * @return the key/value map of the default pgoutput options.
     * @throws ArgumentNullException if {@code publicationNames} is {@code null}.
     */
    public static Map<String, String> createStreamOptions(String publicationNames)
    {
        if (publicationNames == null) {
            throw new ArgumentNullException("publicationNames");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("proto_version", "1");
        result.put("publication_names", publicationNames);
        return result;
    }

    /**
     * Constructor.
     *
     * just for disable SonarLint:squid:S1118: Add a private constructor to
     * hide the implicit public one.
     */
    private PgOutput()
    {
    }
}
