package com.jia54321.utils.netty4.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <p>
 *  用于捕获{@link IdleState#WRITER_IDLE}事件（未在指定时间内向服务器发送数据），然后向<code>Server</code>端发送一个心跳包。
 * </p>
 */
@ChannelHandler.Sharable
public class WriteIdleStateTrigger extends ChannelInboundHandlerAdapter {

    /** 心跳包 */
    protected ByteBuf pkg = Unpooled.buffer();

    /** 获取心跳包 */
    public ByteBuf getPkg() {
        return pkg;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                ctx.writeAndFlush(getPkg());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}