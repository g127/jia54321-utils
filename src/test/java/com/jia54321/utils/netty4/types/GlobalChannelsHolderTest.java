package com.jia54321.utils.netty4.types;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jia54321.utils.netty4.types.GlobalChannelsHolder.ClientType.*;
import static org.junit.Assert.*;

public class GlobalChannelsHolderTest {

    /**
     * 日志
     */
    private static final Logger log                = LoggerFactory.getLogger(GlobalChannelsHolderTest.class);
    public static final  String REGEX_VERTICAL_BAR = "\\|";

    private static Channel channel1 = new EmbeddedChannel();

    private static Channel channel2 = new NioSocketChannel();

    private static Channel channel3 = new NioSocketChannel();

    static {

        for (int i = 0; i < 10; i++) {
            //
            Channel channel1 = new EmbeddedChannel();
            Channel channel2 = new NioSocketChannel();

            GlobalChannelsHolder.put(SYS, "e" + i + 1, "", channel1);
            GlobalChannelsHolder.put(SYS, "n" + i + 1, "", channel2);
        }

        for (int i = 0; i < 100; i++) {
            //
            Channel channel1 = new EmbeddedChannel();
            Channel channel2 = new NioSocketChannel();

            GlobalChannelsHolder.put(THIRD, "e" + i + 1, "", channel1);
            GlobalChannelsHolder.put(THIRD, "n" + i + 1, "", channel2);
        }

        for (int i = 0; i < 1000; i++) {
            //
            Channel channel1 = new EmbeddedChannel();
            Channel channel2 = new NioSocketChannel();

            GlobalChannelsHolder.put(USER, "e" + i + 1, "", channel1);
            GlobalChannelsHolder.put(USER, "n" + i + 1, "", channel2);
        }

        //


    }

    @Test
    public void size() {
        // size
        Assert.assertEquals("2220", 2220, GlobalChannelsHolder.size());
    }

    @Test
    public void innerSize() {
        // innerSize
        Assert.assertArrayEquals(" 20, 200, 2000", new int[]{20, 200, 2000}, GlobalChannelsHolder.innerSize());
    }

    @Test
    public void get() {
        GlobalChannelsHolder.get("", "");

        Object[] groups = "123123|12".split(REGEX_VERTICAL_BAR);
        System.out.println(String.format("======%s======%s", groups));
    }

    @Test
    public void reverseChannel() {
    }

    @Test
    public void remove() {
//        GlobalChannelsHolder.remove(channel1);
//        GlobalChannelsHolder.remove(channel2);
//        GlobalChannelsHolder.remove(channel3);
    }

    @Test
    public void removeByKey() {

    }

    @Test
    public void channelDescText() {
        GlobalChannelsHolder.channelDescText(null);
    }

    @Test
    public void isSameHostAddress() {
    }
}