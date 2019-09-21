/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * the result of {@code CREATE_REPLICATION_SLOT slotname LOGICAL}.
 */
public class PgReplSlotTuple
{
    public static PgReplSlotTuple of //
        /* */( String slotName //
        /* */, long consistentPoint //
        /* */, String snapshotName //
        /* */, String outputPlugin //
        /* */) //
    {
        if (slotName == null) {
            throw new ArgumentNullException("slotName");
        }
        if (snapshotName == null) {
            throw new ArgumentNullException("snapshotName");
        }
        if (outputPlugin == null) {
            throw new ArgumentNullException("outputPlugin");
        }
        return new PgReplSlotTuple(slotName, consistentPoint, snapshotName, outputPlugin);
    }

    /**
     * Obtain a PgReplSlotTuple from a connection and sql.
     *
     * the sql must be a {@code CREATE_REPLICATION_SLOT LOGICAL} statement.
     *
     * There is not good for client code to ensure the {@code sql} parameter
     * is a {@code CREATE_REPLICATION_SLOT LOGICAL} statement
     *
     * TODO: maybe a {@code LogicalReplicationSlotCreator} is good.
     *
     * @param rs the resultSet from {@code CREATE_REPLICATION_SLOT LOGICAL} statement.
     *
     * @return a PgReplSlotTuple Object.
     *
     * @throws SQLException if execute the {@code sql} wrong.
     * @throws ArgumentNullException if {@code connection} or {@code sql} is {@code null}.
     */
    public static PgReplSlotTuple of(ResultSet rs) //
        throws SQLException
    {
        if (rs == null) {
            throw new ArgumentNullException("rs");
        }
        // NOTE:XXX: 此处需要处理null问题么？需要处理rs返回多条记录的问题么？
        Object slotNameObj = rs.getObject("slot_name");
        Object snapshotNameObj = rs.getObject("snapshot_name");
        Object outputPluginObj = rs.getObject("output_plugin");
        Object consistentPointObj = rs.getObject("consistent_point");

        String slotName = Objects.toString(slotNameObj, "");
        String snapshotName = Objects.toString(snapshotNameObj, "");
        String outputPlugin = Objects.toString(outputPluginObj, "");
        LogSequenceNumber consistentPoint = LogSequenceNumber.INVALID_LSN;
        if (consistentPointObj != null) {
            String consistentPointTxt = consistentPointObj.toString();
            consistentPoint = LogSequenceNumber.valueOf(consistentPointTxt);
        }
        return new PgReplSlotTuple //
            /* */( slotName //
            /* */, consistentPoint.asLong() //
            /* */, Objects.toString(snapshotName, "") //
            /* */, Objects.toString(outputPlugin, "") //
            /* */);
    }

    /**
     * The name of the newly-created replication slot.
     */
    public final String slotName;

    /**
     * The WAL location at which the slot became consistent.
     *
     * This is the earliest location from which streaming can start on this
     * replication slot.
     */
    public final long consistentPoint;

    /**
     * The identifier of the snapshot exported by the command.
     *
     * The snapshot is valid until a new command is executed on this connection
     * or the replication connection is closed.
     */
    public final String snapshotName;

    /**
     * The name of the output plugin used by the newly-created replication slot.
     */
    public final String outputPlugin;

    /**
     * Constructor.
     *
     * @param slotName The name of the newly-created replication slot.
     * @param consistentPoint The WAL location at which the slot became consistent.
     * @param snapshotName The identifier of the snapshot exported by the command.
     * @param outputPlugin The name of the output plugin used by the newly-created replication slot.
     */
    private PgReplSlotTuple //
    /* */( String slotName //
        /* */, long consistentPoint //
        /* */, String snapshotName //
        /* */, String outputPlugin //
        /* */) //
    {
        this.slotName = slotName;
        this.consistentPoint = consistentPoint;
        this.snapshotName = snapshotName;
        this.outputPlugin = outputPlugin;
    }

    public void toJsonObject(ObjectNode node)
    {
        node.put("slot_name", slotName);
        node.put("consistent_point", consistentPoint);
        node.put("snashot_name", snapshotName);
        node.put("output_plugin", outputPlugin);
    }
}
