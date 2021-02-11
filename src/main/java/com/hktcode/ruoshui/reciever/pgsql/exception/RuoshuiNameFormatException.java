package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.HttpStatusJacksonObjectException;
import com.hktcode.jackson.JacksonObjectException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;

public class RuoshuiNameFormatException extends HttpStatusJacksonObjectException
{
    public static final HttpStatus CODE = HttpStatus.NOT_FOUND;

    public final String name;

    public RuoshuiNameFormatException(String name)
    {
        super(CODE, "For input name: name=" + name);
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        this.name = name;
    }

    public RuoshuiNameFormatException(HttpStatus code, String name)
    {
        super(code, "For input name: name=" + name);
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        this.name = name;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put(MESSAGE, this.getMessage());
        node.put(ERRCODE, "");
        node.put(EXCLASS, this.getClass().getName());
        node.putArray(REASONS);
        ArrayNode errdata = node.putArray(ERRDATA);
        errdata.addObject().put("name", this.name);
        node.putArray(ADVISES);
        return node;
    }
}
