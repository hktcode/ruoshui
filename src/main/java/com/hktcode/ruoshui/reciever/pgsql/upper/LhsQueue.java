package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.queue.XQueue;

public class LhsQueue extends XQueue<LhsQueue.Record>
{
    public static LhsQueue of(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        LhsQueue result = new LhsQueue();
        result.pst(node);
        return result;
    }

    private LhsQueue()
    {
    }

    public static class Record
    {
        public static Record of(long lsn, LogicalMsg msg)
        {
            if (msg == null) {
                throw new ArgumentNullException("msg");
            }
            return new Record(lsn, msg);
        }

        public final long lsn;

        public final LogicalMsg msg;

        private Record(long lsn, LogicalMsg msg)
        {
            this.lsn = lsn;
            this.msg = msg;
        }
    }
}
