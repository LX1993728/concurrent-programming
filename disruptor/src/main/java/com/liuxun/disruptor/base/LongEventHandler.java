package com.liuxun.disruptor.base;

import com.lmax.disruptor.EventHandler;

// 需要一个事件消费者，也就是一个事件处理器，在此只是做简单的打印
public class LongEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        // 第一个参数表示正在执行的事件(数据)
        // 第二个参数是sequence表示的是RingBuffer的下标
        // 第三个参数表示当前事件是否是RingBuffer的最后一个事件
        System.out.println(longEvent.getValue());
    }
}
