/*
 * Copyright (c) 2019, Huang Ketian
 */

/**
 * 动态修改、获取后台线程信息的相关工具.
 *
 * <p>
 * bgsimple、bgsingle、bgdouble和bgtriple中包含了笔者的一些研究成果。
 * 本成果包含四个包：
 * <dl>
 *     <dt>bgsimple</dt><dd>基础组件，抽象线程外部操作为{@code bgmethod}</dd>
 *     <dt>bgsingle</dt><dd>单线程后台工作者模式</dd>
 *     <dt>bgdouble</dt><dd>消费者-生产者模式</dd>
 *     <dt>bgtriple</dt><dd>消费者-计算器-生产者模式</dd>
 * </dl>
 * </p>
 * 因为本项目中只用到了这两个包，所以只包含了bgsimple和bgtriple两个包。
 *
 * 这些成果仅仅靠程序中的注释无法说明清楚原理，笔者打算编写几篇文章介绍，再向程序中添加代码注释。
 * 而且在本项目发布时，还发现了可改进空间，未来可能还有较大改动。
 *
 * 因此目前当前代码中并没有注释信息。
 */
package com.hktcode.bgtriple;
