/*
 * Copyright (c) 2019, Huang Ketian
 */

/**
 * 快照功能是非常重要的核心功能，验证数据、新增指标甚至修复数据都需要快照信息.
 *
 * 目前的获取快照实现非常不灵活，只能满足从获取快照进入Kafka的功能。
 * 还不足以担当起笔者流式计算体系中的快照实现。
 *
 * TODO: 需要重构，并添加相关信息。
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;