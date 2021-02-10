package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObjectException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;

public class ConfFileLockFailureException extends JacksonObjectException
{
    public final String name;

    public final String path;

    public ConfFileLockFailureException(String name, String path)
    {
        super(HttpStatus.FORBIDDEN, "config file is locked by other process: name=" + name + ", path=" + path);
        this.name = name;
        this.path = path;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("message", this.getMessage());
        // ArrayNode reasonsNode = node.putArray("reasons");
        node.put("errcode", "");
        ArrayNode advisesNode = node.putArray("advises");
        node.put("exclass", ConfFileLockFailureException.class.getName());
        return node;
    }
}
