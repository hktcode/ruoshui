package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.file.FileSystemLoopException;

public class ConfFileFileSystemLoopException extends ConfFileFileSystemException
{
    public ConfFileFileSystemLoopException(String name, String path, FileSystemLoopException ex)
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
        node.set("exclass", TextNode.valueOf(ConfFileFileSystemLoopException.class.getName()));
        // return new ResponseEntity<>(body, HttpStatus.VARIANT_ALSO_NEGOTIATES);
        return node;
    }
}
