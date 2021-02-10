package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.file.AccessDeniedException;

public class ConfFileAccessDeniedException extends ConfFileFileSystemException
    implements JacksonObject
{
    public ConfFileAccessDeniedException(String name, String path, AccessDeniedException ex)
    {
        super(name, path, CODE, ex);
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.set("message", TextNode.valueOf(this.getMessage()));
        ArrayNode reasonsNode = node.putArray("reasons");
        node.set("errcode", TextNode.valueOf(""));
        ArrayNode advisesNode = node.putArray("advises");
        advisesNode.add("$RUOSHUI_HOME/var/auto/ access denied");
        node.set("exclass", TextNode.valueOf(ConfFileAccessDeniedException.class.getName()));
        return node;
    }
}
