package com.hktcode.lang;

/**
 * 忽略了{@link InterruptedException}的{@link Runnable}
 */
public interface RunnableWithInterrupted extends Runnable
{
    default void run()
    {
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 真正的运行内容.
     *
     * @throws InterruptedException 线程被中断时抛出.
     */
    void runWithInterrupted() throws InterruptedException;
}
