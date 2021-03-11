package com.jia54321.utils.event;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventManagerTest {

    static final Logger log = LoggerFactory.getLogger(EventManagerTest.class);

    @Before
    public void setUp() throws Exception {
        // 异步订阅
        EventManager.asyncSubscriberEvent(PrintEvent.EVENT_ID, new IEventSubscriber<PrintEvent>(){
            @Override
            public void action(PrintEvent paramT) {
                if(paramT.getSource() instanceof String) {
                    System.out.println("异步订阅:" + paramT.getSource());
                }
            }
        });

        // 同步订阅
        EventManager.subscriberEvent(PrintEvent.EVENT_ID, new IEventSubscriber<PrintEvent>(){
            @Override
            public void action(PrintEvent paramT) {
                if(paramT.getSource() instanceof String) {
                    System.out.println("同步订阅:" + paramT.getSource());
                }
            }
        });

    }

    @Test
    public void test() {
        System.out.println("开始");
        PrintEvent printEvent = new PrintEvent("消息");
        //事件分发:验证通过事件
        EventManager.publishEvent(printEvent);
        System.out.println("结束");
    }
    @Test
    public void publishEvent() {
    }

    @Test
    public void subscriberEvent() {
    }

    @Test
    public void testSubscriberEvent() {
    }

    @Test
    public void testSubscriberEvent1() {
    }

    @Test
    public void asyncSubscriberEvent() {
    }

    @Test
    public void cancelSubscriberEvent() {
    }

    @Test
    public void subscribersEvent() {
    }

    @Test
    public void asyncSubscribersEvent() {
    }

    @Test
    public void testSubscribersEvent() {
    }
}