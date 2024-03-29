/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PostgreSQL;
import org.postgresql.PGProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * the property for PgConnection.
 */
public class PgProps implements JacksonObject
{
    public static final Logger logger = LoggerFactory.getLogger(PgProps.class);

    public static PgProps of(ImmutableMap<String, String> propertyMap)
    {
        if (propertyMap == null) {
            throw new ArgumentNullException("propertyMap");
        }
        return new PgProps(propertyMap);
    }

    /**
     * Obtain a {@link PgProps} from a {@link JsonNode}.
     *
     * @param json the {@link JsonNode} from a JSON string.
     * @return a {@link PgProps} Object.
     * @throws ArgumentNullException if {@code json} is {@code null}.
     */
    public static PgProps ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        Map<String, String> map = createDefaultMap();
        Iterator<Map.Entry<String, JsonNode>> it = json.fields();
        while(it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            map.put(e.getKey(), e.getValue().asText());
        }
        // TODO: 检查srcProperty
        return new PgProps(ImmutableMap.copyOf(map));
    }

    /**
     * create the default properties.
     *
     * @return the default properties map.
     */
    private static Map<String, String> createDefaultMap()
    {
        Map<String, String> result = new HashMap<>();
        result.put(PGProperty.PG_DBNAME.getName(), "postgres");
        result.put(PGProperty.PG_HOST.getName(), "localhost");
        result.put(PGProperty.PG_PORT.getName(), "5432");
        result.put(PGProperty.USER.getName(), "postgres");
        result.put(PGProperty.ASSUME_MIN_SERVER_VERSION.getName(), "10");
        result.put(PGProperty.APPLICATION_NAME.getName(), "ruoshui");
        return result;
    }

    /**
     * the properties.
     */
    public final ImmutableMap<String, String> propertyMap;

    /**
     * constructor.
     *
     * @param propertyMap the properties map.
     */
    private PgProps(ImmutableMap<String, String> propertyMap)
    {
        this.propertyMap = propertyMap;
    }

    /**
     * get a replication connection to PostgreSQL server.
     *
     * @return a repliation connection to PostgreSQL server.
     *
     * @throws SQLException if connection to PostgreSQL server occur an error.
     */
    public Connection replicaConnection() throws SQLException
    {
        Properties props = new Properties();
        for (Map.Entry<String, String> e : this.propertyMap.entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }

        PGProperty.REPLICATION.set(props, "database");
        PGProperty.PREFER_QUERY_MODE.set(props, "simple");
        StringBuilder sb = toText(props);
        String url = PostgreSQL.JDBC_URL;
        logger.info("get replication connection: url={}{}", url, sb);
        return DriverManager.getConnection(url, props);
    }

    /**
     * convert the properties to a {@link StringBuilder}.
     *
     * @return a {@link StringBuilder} Object that contains the properties information.
     */
    private static StringBuilder toText(Properties props)
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            sb.append("\n    ").append(e.getKey()).append(" = ").append(e.getValue());
        }
        return sb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("{");
        String splitter = "";
        for (Map.Entry<String, String> e : this.propertyMap.entrySet()) {
            sb.append(splitter);
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
            splitter = ", ";
        }
        sb.append('}');
        return sb.toString();
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        for (Map.Entry<String, String> entry : this.propertyMap.entrySet()) {
            node.put(entry.getKey(), entry.getValue());
        }
        return node;
    }
}
