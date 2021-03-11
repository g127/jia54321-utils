package com.jia54321.utils.aspect;

import com.jia54321.utils.annotation.DebugTrace;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DebugTraceAspectTest {

    @Before
    public void setUp() throws Exception {
//        //1、创建代理工厂
//        AspectJProxyFactory factory = new AspectJProxyFactory();
//        //2、创建目标对象
//        Waiter waiter = new NaiveWaiter();
//        //3、设置目标对象
//        factory.setTarget(waiter);
//        //4、添加切面类
//        factory.addAspect(PreGreetingAspect.class);

        //DebugTraceAspect debugTraceAspect = new DebugTraceAspect();
    }

    @Test
    public void test() {
        this.testAnnotatedMethod();
    }

    @DebugTrace
    public void testAnnotatedMethod() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}