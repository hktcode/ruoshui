package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.jackson.JacksonObject;

public interface SimpleArgval extends JacksonObject
{
    void pst(JsonNode json);
}
