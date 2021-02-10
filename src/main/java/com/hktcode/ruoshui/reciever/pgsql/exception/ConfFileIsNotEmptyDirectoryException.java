package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.file.DirectoryNotEmptyException;

public class ConfFileIsNotEmptyDirectoryException extends ConfFileFileSystemException
{
    public ConfFileIsNotEmptyDirectoryException(String name, String path, DirectoryNotEmptyException ex)
    {
        super(name, path, CODE, ex);
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
        node.set("exclass", TextNode.valueOf(ConfFileIsNotEmptyDirectoryException.class.getName()));
        return node;
    }
}
