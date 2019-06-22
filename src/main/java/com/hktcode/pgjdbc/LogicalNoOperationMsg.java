/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

/**
 * the 'Noop' Logical replication message.
 *
 * this class mean that no message from the Logical replication.
 *
 * offer this class only for full complete.
 */
public class LogicalNoOperationMsg implements LogicalMsg
{
    /**
     * the only one instance.
     */
    public static LogicalNoOperationMsg INSTANCE = new LogicalNoOperationMsg();

    /**
     * Constructor function.
     */
    private LogicalNoOperationMsg()
    {
    }
}
