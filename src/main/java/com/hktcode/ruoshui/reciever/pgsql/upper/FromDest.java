package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmRecver;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcSender;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class FromDest
{
    public static final class Schema
    {
        public static final ObjectNode SCHEMA;

        static
        {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
            ObjectNode typeNode = schema.put("type", "object");
            ObjectNode propertiesNode = schema.putObject("properties");
            propertiesNode.set("recver", UpcsmRecver.SCHEMA);
            propertiesNode.set("sender", UppdcSender.Schema.SCHEMA);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }
    }

    public static FromDest of(JsonNode jsonnode)
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        AtomicLong xidlsn = new AtomicLong(0L);
        UpcsmRecver recver = UpcsmRecver.of(jsonnode.path("recver"), xidlsn);
        UppdcSender sender = UppdcSender.of(jsonnode.path("sender"), xidlsn);
        return new FromDest(recver, sender);
    }

    private final UpcsmRecver recver;

    private final UppdcSender sender;

    private FromDest(UpcsmRecver recver, UppdcSender sender)
    {
        this.recver = recver;
        this.sender = sender;
    }

    public UpcsmRecver.Client recver() throws SQLException
    {
        return this.recver.client();
    }

    public UppdcSender.Client sender()
    {
        return this.sender.client();
    }

    public Result toJsonResult()
    {
        return new Result(this);
    }

    public static final class Result implements JacksonObject
    {
        public final UpcsmRecver.Result recver;

        public final UppdcSender.Result sender;

        private Result(FromDest origin)
        {
            this.recver = origin.recver.toJsonResult();
            this.sender = origin.sender.toJsonResult();
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            ObjectNode recverNode = node.putObject("recver");
            this.recver.toJsonObject(recverNode);
            ObjectNode senderNode = node.putObject("sender");
            this.sender.toJsonObject(senderNode);
            return node;
        }
    }
}
