package com.liuxun.disruptor.multi;

import com.lmax.disruptor.RingBuffer;

public class Producer {
    private final RingBuffer<Order> ringBuffer;

    public Producer(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData用来发布事件，每调用一次就发布一次事件
     * 它的参数会通过事件传递给消费者
     */
    public void onData(String data){
        // 可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
        long sequence = ringBuffer.next();
        try{
            // 用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
            Order order = ringBuffer.get(sequence);
            // 获取要通过事件传递的业务数据
            order.setId(data);
        }finally {
            // 发布事件
            // 注意，最后的ringBuffer.publish 方法必须包含在finally中以确保必须得到调用
            // 如果某个请求的sequence 未被提交，将阻塞后续的发布操作或者其它的producer
            ringBuffer.publish(sequence);
        }
    }
}
