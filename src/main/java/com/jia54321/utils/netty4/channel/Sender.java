package com.jia54321.utils.netty4.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * <p>客户端连接到服务器端后，会循环执行一个任务：监测是否有数据，然后Sender一下Server端，即发送一个数据包。</p>
 */
@SuppressWarnings("unchecked")
@ChannelHandler.Sharable
public class Sender extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext ctx;
    protected ChannelPromise promise;

    protected Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当Channel已经断开的情况下, 仍然发送数据, 会抛异常, 该方法会被调用.
        cause.printStackTrace();
        ctx.close();
    }

    public synchronized ChannelPromise sendMessage(Object message) {
        while (null != channel && !channel.isActive()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
                //logger.error("等待ChannelHandlerContext实例化");
            } catch (InterruptedException e) {
                //logger.error("等待ChannelHandlerContext实例化过程中出错",e);
            }
        }
        // promise = ctx.newPromise();
        channel.writeAndFlush(message);
        // return promise;
        return null;
    }

}