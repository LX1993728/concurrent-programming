package com.liuxun.disruptor.generate2;

import com.liuxun.disruptor.generate1.Trade;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TradePublisher implements Runnable {
    Disruptor<Trade> disruptor;
    private CountDownLatch latch;

    private static int LOOP = 10; // 模拟百万次交易的发生

    public TradePublisher(Disruptor<Trade> disruptor, CountDownLatch latch) {
        this.disruptor = disruptor;
        this.latch = latch;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeTranslator = new TradeEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            disruptor.publishEvent(tradeTranslator);
        }
        latch.countDown();
    }
}

class TradeEventTranslator implements EventTranslator<Trade> {

    private Random random = new Random();

    @Override
    public void translateTo(Trade event, long l) {
        this.generateTrade(event);
    }

    private Trade generateTrade(Trade trade) {
        trade.setPrice(random.nextDouble() * 9999);
        return trade;
    }
}