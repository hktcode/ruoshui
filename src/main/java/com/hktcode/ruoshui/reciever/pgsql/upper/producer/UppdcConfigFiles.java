/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.Ruoshui;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public class UppdcConfigFiles extends UppdcConfig
{
    public static final long DEFAULT_MAX_SYNCTIME = 60000;

    public static final long DEFAULT_MAX_SYNCSIZE = 64 * 1024;

    public static final long DEFAULT_MAX_FILESIZE = 256 * 1024 * 1024;

    public static final long DEFAULT_MAX_FILETIME = 60 * 60 * 1000;

    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UppdcConfigFiles.class, "UppdcConfig.yml");

    public static UppdcConfigFiles ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        Path walDatapath = Paths.get(Ruoshui.HOME, "var", "data", "revievers.pgsql.upper");
        long maxSynctime = json.path("max_synctime").asLong(DEFAULT_MAX_SYNCTIME);
        long maxSyncsize = json.path("max_syncsize").asLong(DEFAULT_MAX_SYNCSIZE);
        long maxFilesize = json.path("max_filesize").asLong(DEFAULT_MAX_FILESIZE);
        long maxFiletime = json.path("max_filetime").asLong(DEFAULT_MAX_FILETIME);
        return new UppdcConfigFiles(walDatapath, maxSynctime, maxSyncsize, maxFilesize, maxFiletime);
    }

    public final Path walDatapath;

    public final long maxSynctime;

    public final long maxSyncsize;

    public final long maxFilesize;

    public final long maxFiletime;

    private UppdcConfigFiles //
        /* */( Path walDatapath
        /* */, long maxSynctime
        /* */, long maxSyncsize
        /* */, long maxFilesize
        /* */, long maxFiletime
        /* */)
    {
        super("files");
        this.walDatapath = walDatapath;
        this.maxSynctime = maxSynctime;
        this.maxSyncsize = maxSyncsize;
        this.maxFilesize = maxFilesize;
        this.maxFiletime = maxFiletime;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        super.toJsonObject(node);
        node.put("max_synctime", this.maxSynctime);
        node.put("max_syncsize", this.maxSyncsize);
        node.put("max_filesize", this.maxFilesize);
        node.put("max_filetime", this.maxFiletime);
        return node;
    }

    @Override
    public UppdcActionRunFiles action()
    {
        return UppdcActionRunFiles.of(this);
    }
}
