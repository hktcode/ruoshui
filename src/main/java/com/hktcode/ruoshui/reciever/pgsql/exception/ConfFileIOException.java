package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hktcode.jackson.JacksonObjectException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class ConfFileIOException extends JacksonObjectException
{
    public final static HttpStatus CODE = HttpStatus.VARIANT_ALSO_NEGOTIATES;

    public final String name;

    public final String path;

    public ConfFileIOException(String name, String path, HttpStatus code, IOException ex)
    {
        super(code, ex.getMessage(), ex);
        this.name = name;
        this.path = path;
    }

    protected ConfFileIOException(String name, String path, HttpStatus code, String msg, IOException ex)
    {
        super(code, msg, ex);
        this.name = name;
        this.path = path;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.set("message", TextNode.valueOf(this.getMessage()));
        ArrayNode reasonsNode = node.putArray("reasons");
        node.set("errcode", TextNode.valueOf(""));
        ArrayNode advisesNode = node.putArray("advises");
        node.set("exclass", TextNode.valueOf(ConfFileIOException.class.getName()));
        ObjectNode errdata = node.putObject("errdata");
        return node;
    }
}
