package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.XQueue;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;

public class RhsQueue extends XQueue<RhsQueue.Record>
{
    public static RhsQueue of(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        RhsQueue result = new RhsQueue();
        result.pst(node);
        return result;
    }

    private RhsQueue()
    {
    }

    public static class Record
    {
        public static Record of(PgsqlKey key, PgsqlVal val)
        {
            if (key == null) {
                throw new ArgumentNullException("key");
            }
            if (val == null) {
                throw new ArgumentNullException("val");
            }
            return new Record(key, val);
        }

        public final PgsqlKey key;

        public final PgsqlVal val;

        private Record(PgsqlKey key, PgsqlVal val)
        {
            this.key = key;
            this.val = val;
        }
    }
}
