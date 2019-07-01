/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;

/**
 * the 'Relation' Logical Replication Message.
 */
public class LogicalRelationInfMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalRelationInfMsg from a relident, dbschema, relation, replchar and attrlist.
     *
     * @param relident ID of the relation.
     * @param dbschema Namespace(empty string for pg_catalog).
     * @param relation relation name
     * @param replchar Replica identity setting for the relation(same as relreplident in pg_class).
     * @param attrlist attributes list.
     * @return a LogicalTxactBeginsMsg Object
     * @throws ArgumentNullException if {@code dbschema}, {@code relation} or {@code attrlist} is {@code null}.
     */
    public static LogicalRelationInfMsg of //
        /* */( long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<LogicalAttribute> attrlist //
        /* */)
    {
        if (dbschema == null) {
            throw new ArgumentNullException("dbschema");
        }
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        if (attrlist == null) {
            throw new ArgumentNullException("attrlist");
        }
        return new LogicalRelationInfMsg(relident, dbschema, relation, replchar, attrlist);
    }

    /**
     * Obtain a LogicalRelationInfMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalRelationInfMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalRelationInfMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long relident = content.getInt();
        String dbschema = LogicalMsg.readCStyleUtf8String(content);
        if ("".equals(dbschema)) {
            dbschema = "pg_catalog";
        }
        String relation = LogicalMsg.readCStyleUtf8String(content);
        long replchar = Byte.toUnsignedLong(content.get());
        int columnIndex = content.getShort();
        LogicalAttribute[] attrlist = new LogicalAttribute[columnIndex];
        for (int i = 0; i < columnIndex; ++i) {
            attrlist[i] = LogicalAttribute.ofLogicalWal(content);
        }
        return new LogicalRelationInfMsg(relident //
            , dbschema //
            , relation //
            , replchar //
            , ImmutableList.copyOf(attrlist) //
        );
    }

    /**
     * ID of the relation.
     */
    public final long relident;

    /**
     * Namespace(empty string for pg_catalog).
     */
    public final String dbschema;

    /**
     * Relation name.
     */
    public final String relation;

    /**
     * Replica identity setting for the relation(same as relreplident in pg_class).
     *
     * Columns used to form "replica identity" for rows:
     * <dl>
     *   <dt>d</dt><dd>default (primary key, if any),</dd>
     *   <dt>n</dt><dd>nothing.</dd>
     *   <dt>f</dt><dd>all columns.</dd>
     *   <dt>i</dt><dd>index with indisreplident set, or default</dd>
     * </dl>
     */
    public final long replchar;

    /**
     * Attributes list.
     */
    public final ImmutableList<LogicalAttribute> attrlist;

    /**
     * contructor function.
     *
     * @param relident ID of the relation.
     * @param dbschema Namespace(empty string for pg_catalog).
     * @param relation relation name
     * @param replchar Replica identity setting for the relation(same as relreplident in pg_class).
     * @param attrlist attributes list.
     */
    private LogicalRelationInfMsg //
        /* */( long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<LogicalAttribute> attrlist //
        /* */)
    {
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
        this.attrlist = attrlist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("    relation:");
        this.toString(sb);
        return sb.toString();
    }

    /**
     * put the string representation into a {@code StringBuilder}.
     *
     * @param builder the {@code StringBuilder} to append the string representation.
     * @throws ArgumentNullException if {@code builder} is {@code null}.
     */
    public void toString(StringBuilder builder)
    {
        if (builder == null) {
            throw new ArgumentNullException("builder");
        }
        builder.append(this.dbschema);
        builder.append('.');
        builder.append(this.relation);
        builder.append(':');
        builder.append(Long.toUnsignedString(this.relident));
        builder.append(':');
        builder.append(this.replchar);
        for(LogicalAttribute c : this.attrlist) {
            builder.append("\n            ");
            builder.append(c.toString());
        }
    }
}
