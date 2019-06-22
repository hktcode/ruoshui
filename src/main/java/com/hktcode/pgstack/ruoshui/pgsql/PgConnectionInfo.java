/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;

/**
 * PostgreSQL server connection information.
 */
public class PgConnectionInfo
{
    /**
     * Obtain a {@link PgConnectionInfo} from {@link PgConnection}.
     *
     * @param pgc the connection to PostgreSQL server.
     *
     * @return a {@link PgConnectionInfo} object.
     * @throws ArgumentNullException if {@code pgc} is {@code null}.
     */
    public static PgConnectionInfo of(PgConnection pgc)
    {
        if (pgc == null) {
            throw new ArgumentNullException("pgc");
        }
        long backendPid = pgc.getBackendPID();
        long serverMajorVersion = pgc.getServerMajorVersion();
        long serverMinorVersion = pgc.getServerMinorVersion();
        return new PgConnectionInfo //
            /* */( backendPid //
            /* */, serverMajorVersion //
            /* */, serverMinorVersion //
            /* */);
    }

    /**
     * the process ID (PID) of the backend server process handling this connection.
     */
    public final long backendPid;

    /**
     * server major version.
     */
    public final long serverMajorVersion;

    /**
     * server minor version.
     */
    public final long serverMinorVersion;

    /**
     * Constructor.
     *
     * @param backendPid PID of backend server process.
     * @param serverMajorVersion server major version.
     * @param serverMinorVersion server minor version.
     */
    private PgConnectionInfo //
        /* */( long backendPid //
        /* */, long serverMajorVersion //
        /* */, long serverMinorVersion //
        /* */) //
    {
        this.backendPid = backendPid;
        this.serverMajorVersion = serverMajorVersion;
        this.serverMinorVersion = serverMinorVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("{backend_pid=");
        sb.append(backendPid);
        sb.append(", serverMajorVersion=");
        sb.append(serverMajorVersion);
        sb.append(", serverMinorVersion=");
        sb.append(this.serverMinorVersion);
        sb.append("}");
        return sb.toString();
    }
}
