/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * an relation for replication message context.
 */
public class PgReplRelation
{
    /**
     * Obtain a {@link PgReplRelation} from a metadata and attrlist.
     *
     * @param metadata the metadata of relation.
     * @param attrlist attributes.
     * @throws ArgumentNullException if {@code metadata} or {@code attrlist} is {@code null}.
     */
    public static PgReplRelation of//
        /* */( PgReplRelationMetadata metadata
        /* */, ImmutableList<PgReplAttribute> attrlist //
        /* */)
    {
        if (metadata == null) {
            throw new ArgumentNullException("metadata");
        }
        if (attrlist == null) {
            throw new ArgumentNullException("attrlist");
        }
        return new PgReplRelation //
            /* */( metadata.relident //
            /* */, metadata.dbschema //
            /* */, metadata.relation //
            /* */, metadata.replchar //
            /* */, attrlist //
            /* */);
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
     * attributes.
     */
    public final ImmutableList<PgReplAttribute> attrlist;

    /**
     * 构造函数.
     *
     * @param relident ID of the relation.
     * @param dbschema Namespace(empty string for pg_catalog).
     * @param relation Relation Name
     * @param replchar Replica identity setting for the relation(same as relreplident in pg_class).
     * @param attrlist the attributes.
     */
    private PgReplRelation //
        /* */( long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgReplAttribute> attrlist //
        /* */)
    {
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
        this.attrlist = attrlist;
    }

    /**
     * 将关系的元数据附加到指定的{@code StringBuilder}后面.
     *
     * 一般用于生成{@link #toString()}人类可读信息.
     *
     * @param builder 指定的{@code StringBuilder}对象.
     */
    protected void appendMetadataTo(StringBuilder builder)
    {
        builder.append(this.dbschema);
        builder.append('|');
        builder.append(this.relation);
        builder.append('|');
        builder.append(this.relident);
        builder.append('|');
        builder.append((char)this.replchar);
    }

    /**
     * 将关系的属性数据附加到指定的{@code StringBuilder}后面.
     *
     * 一般用于生成{@link #toString()}人类可读信息.
     *
     * @param builder 指定的{@code StringBuilder}对象.
     */
    public void appendAttrlistTo(StringBuilder builder)
    {
        for(PgReplAttribute attr: attrlist) {
            builder.append("\n    ");
            attr.appendTo(builder);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        this.appendMetadataTo(builder);
        this.appendAttrlistTo(builder);
        return builder.toString();
    }
}
