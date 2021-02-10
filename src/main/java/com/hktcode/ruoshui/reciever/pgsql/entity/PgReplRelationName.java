/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.hktcode.lang.exception.ArgumentNullException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PostgreSQL relation full name which contains schema name and relation name.
 */
public class PgReplRelationName
{
    /**
     * the full name pattern.
     */
    public static final Pattern PATTERN = Pattern.compile("((?:[^.]|(?:\\.\\.))*)(?:\\.)((?:[^.]|(?:\\\\.\\\\.))+)");

    /**
     * Obtain a {@link PgReplRelationName} from text.
     *
     * @param text the full name string.
     *
     * @return a {@link PgReplRelationName} Object.
     * @throws ArgumentNullException if {@code text} is {@code null}.
     * @throws RuntimeException TODO:
     */
    public static PgReplRelationName ofTextString(String text)
    {
        if (text == null) {
            throw new ArgumentNullException("text");
        }
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new RuntimeException(); // TODO:
        }
        String dbschema = matcher.group(1);
        String relation = matcher.group(2);
        return new PgReplRelationName(dbschema, relation);
    }

    /**
     * Obtain a {@link PgReplRelationName} from schema name and relation name.
     *
     * @param dbschema the schema name.
     * @param relation the relation name.
     *
     * @return a {@link PgReplRelationName} Object.
     * @throws ArgumentNullException if {@code dbschema} or {@code relation} is {@code null}.
     */
    public static PgReplRelationName of(String dbschema, String relation)
    {
        if (dbschema == null) {
            throw new ArgumentNullException("dbschema");
        }
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return new PgReplRelationName(dbschema, relation);
    }

    /**
     * the schema name.
     */
    public final String dbschema;

    /**
     * the relation name.
     */
    public final String relation;

    /**
     * constructor.
     *
     * @param dbschema the schema name.
     * @param relation the relation name.
     */
    private PgReplRelationName(String dbschema, String relation)
    {
        this.dbschema = dbschema;
        this.relation = relation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(this.dbschema.replace(".", ".."));
        sb.append('.');
        sb.append(this.relation.replace(".", ".."));
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        if (!(o instanceof PgReplRelationName)) {
            return false;
        }
        PgReplRelationName n = (PgReplRelationName)o;
        return Objects.equals(this.dbschema, n.dbschema)
            && Objects.equals(this.relation, n.relation);
    }
}
