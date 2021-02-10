package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;

import java.nio.file.FileSystemException;

public class ConfFileFileSystemException extends ConfFileIOException
{
    public ConfFileFileSystemException(String name, String path, HttpStatus code, FileSystemException ex)
    {
        super(name, path, code, ex);
    }

    protected ConfFileFileSystemException(String name, String path, HttpStatus code, String msg, FileSystemException ex)
    {
        super(name, path, code, msg, ex);
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
        node.set("exclass", TextNode.valueOf(ConfFileFileSystemException.class.getName()));
        // return new ResponseEntity<>(body, HttpStatus.VARIANT_ALSO_NEGOTIATES);
        return node;
    }
}
