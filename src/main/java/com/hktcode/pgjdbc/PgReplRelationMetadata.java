/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;

/**
 * the metadata of a relation.
 */
public class PgReplRelationMetadata
{
    /**
     * Obtain a PgReplRelationMetadata from a relident, dbschema, relation and replchar.
     *
     * @param relident ID of the relation.
     * @param dbschema Namespace(empty string for pg_catalog).
     * @param relation Relation Name
     * @param replchar Replica identity setting for the relation(same as relreplident in pg_class).
     * @return a PgReplRelationMetadata Object.
     * @throws ArgumentNullException if {@code dbschema} or {@code relation} is {@code null}.
     */
    public static PgReplRelationMetadata //
    of(long relident, String dbschema, String relation, long replchar)
    {
        if (dbschema == null) {
            throw new ArgumentNullException("dbschema");
        }
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return new PgReplRelationMetadata(relident, dbschema, relation, replchar);
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
     * contructor function.
     *
     * @param relident ID of the relation.
     * @param dbschema Namespace(empty string for pg_catalog).
     * @param relation Relation Name
     * @param replchar Replica identity setting for the relation(same as relreplident in pg_class).
     */
    private PgReplRelationMetadata //
        /* */(long relident, String dbschema, String relation, long replchar)
    {
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
    }
}
