/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PgConfig extends TqueueConfig
{
    public static ImmutableMap<PgReplRelationName, String> toTupleSelect(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        Map<PgReplRelationName, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            PgReplRelationName relationName = PgReplRelationName.ofTextString(e.getKey());
            map.put(relationName, e.getValue().asText());
        }
        return ImmutableMap.copyOf(map);
    }

    /**
     * 默认的{@link ResultSet#setFetchSize(int)}值.
     */
    static final int DEFAULT_RS_FETCHSIZE = 10240;

    static final String DEFAULT_RELATION_SQL;

    static final String DEFAULT_TYPELIST_SQL;

    static {
        DEFAULT_TYPELIST_SQL = getResourceFileAsString("/default_typelist.sql");
        DEFAULT_RELATION_SQL = getResourceFileAsString("/default_relalist.sql");
    }

    /**
     * Reads given resource file as a string.
     *
     * modify from
     * https://stackoverflow.com/questions/6068197/utils-to-read-resource-text-file-to-string-java
     *
     * @param fileName path to the resource file
     * @return the file's contents
     * @throws UncheckedIOException if read fails for any reason
     */
    private static String getResourceFileAsString(String fileName) //
    {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException();
            }
            StringBuilder builder = new StringBuilder();
            Charset utf8 = StandardCharsets.UTF_8;
            try (InputStreamReader isr = new InputStreamReader(is, utf8);
                 BufferedReader reader = new BufferedReader(isr)) {
                int length = 1024;
                char[] buffer = new char[length];
                int readlength;
                while ((readlength = reader.read(buffer)) != -1) {
                    builder.append(buffer, 0, readlength);
                }
                return builder.toString();
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public PreparedStatement queryTypelist(PgConnection pgdata)
        throws SQLException
    {
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        return preparedStatement(pgdata, typelistSql);
    }

    public PreparedStatement queryTupleval(PgConnection pgdata, PgReplRelation relation)
        throws SQLException
    {
        PgReplRelationName name = PgReplRelationName.of(relation.dbschema, relation.relation);
        String sql = this.tupleSelect.get(name);
        if (sql == null) {
            sql = buildSelect(pgdata, relation);
        }
        return preparedStatement(pgdata, sql);
    }

    private PreparedStatement preparedStatement(PgConnection pg, String sql) //
        throws SQLException
    {
        if (pg == null) {
            throw new ArgumentNullException("pg");
        }
        if (sql == null) {
            throw new ArgumentNullException("sql");
        }
        PreparedStatement ps = pg.prepareStatement //
            /* */( sql //
                /* */, ResultSet.TYPE_FORWARD_ONLY //
                /* */, ResultSet.CONCUR_READ_ONLY //
                /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
                /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.rsFetchsize);
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }

    private static String buildSelect(PgConnection c, PgReplRelation r) //
        throws SQLException
    {
        StringBuilder sb = new StringBuilder("\nSELECT ");
        String n = c.escapeIdentifier(r.attrlist.get(0).attrname);
        sb.append(n);
        sb.append("::text as ");
        sb.append(n);
        for(int i = 1; i < r.attrlist.size(); ++i) {
            sb.append("\n     , ");
            n = c.escapeIdentifier(r.attrlist.get(i).attrname);
            sb.append(n);
            sb.append("::text as ");
            sb.append(n);
        }
        sb.append("\nFROM ");
        sb.append(c.escapeIdentifier(r.dbschema));
        sb.append(".");
        sb.append(c.escapeIdentifier(r.relation));
        return sb.toString();
    }

    public PreparedStatement queryRelalist(PgConnection pgdata) //
        throws SQLException
    {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (String name : this.logicalRepl.publicationNames) {
            arrayNode.add(name);
        }
        PreparedStatement ps = this.preparedStatement(pgdata, relationSql);
        try {
            ps.setString(1, arrayNode.toString());
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }

    public boolean whereRelalist(PgReplRelation relation) throws ScriptException
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return whereScript.eval(relation);
    }

    public String lockStatement(PgReplRelation relation, PgConnection cnt)
        throws SQLException
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return "LOCK TABLE ONLY " //
            + cnt.escapeIdentifier(relation.dbschema) //
            + "." //
            + cnt.escapeIdentifier(relation.relation) //
            + " IN " //
            + lockingMode.textFormat //
            + " MODE" //
            ;
    }

    public final PgConnectionProperty srcProperty;

    public final ImmutableMap<PgReplRelationName, String> tupleSelect;

    public final String relationSql;

    public final PgFilter whereScript;

    public final PgLockMode lockingMode;

    public final LogicalReplConfig logicalRepl;

    public int rsFetchsize = DEFAULT_RS_FETCHSIZE;

    public final String typelistSql;

    public final boolean getSnapshot;

    protected PgConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */) //
    {
        this.srcProperty = srcProperty;
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.tupleSelect = tupleSelect;
        this.logicalRepl = logicalRepl;
        this.typelistSql = DEFAULT_TYPELIST_SQL;
        this.getSnapshot = true;
    }

    protected PgConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, String typelistSql //
        /* */, boolean getSnapshot //
        /* */) //
    {
        this.srcProperty = srcProperty;
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.tupleSelect = tupleSelect;
        this.logicalRepl = logicalRepl;
        this.typelistSql = typelistSql;
        this.getSnapshot = getSnapshot;
    }

    public abstract PgAction afterSnapshot(PgActionDataSsFinish action);

    public abstract PgDeputeCreateSlot newCreateSlot(Statement statement);

    public abstract PgAction createsAction(AtomicReference<SimpleStatus> status, TransferQueue<PgRecord> tqueue);
}
