/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.time.ZonedDateTime;

public interface MainlineMetric
{
    void toJsonObject(ObjectNode node);
}
