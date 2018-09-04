package com.liuxun.disruptor.multi;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class MultiMain {
    public static void main(String[] args) throws Exception {
        // 创建ringBuffer
        RingBuffer<Order> ringBuffer = RingBuffer.create(ProducerType.MULTI, new EventFactory<Order>() {
            @Override
            public Order newInstance() {
                return new Order();
            }
        }, 1024 * 1024, new YieldingWaitStrategy());

        SequenceBarrier barriers = ringBuffer.newBarrier();
        Consumer[] consumers = new Consumer[3];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("C" + i);
        }

        WorkerPool<Order> workerPool = new WorkerPool<>(ringBuffer, barriers, new IntEventExceptionHandler(), consumers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            final Producer producer = new Producer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 100; j++) {
                        producer.onData(UUID.randomUUID().toString());
                    }
                }
            }).start();
        }

        Thread.sleep(2000);
        System.out.println("------------ 开始生产 ----------------");
        latch.countDown();
        Thread.sleep(5000);
        System.out.println("总数: " + consumers[0].getCount());
    }

    static class IntEventExceptionHandler implements ExceptionHandler {

        @Override
        public void handleEventException(Throwable throwable, long sequence, Object object) {
        }

        @Override
        public void handleOnStartException(Throwable throwable) {
        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {
        }
    }
}
