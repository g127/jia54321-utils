package com.jia54321.utils.netty4.channel;

import com.jia54321.utils.netty4.TcpClient;
import com.jia54321.utils.netty4.policy.RetryPolicy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Reconnector
 */
@ChannelHandler.Sharable
public class Reconnector extends ChannelInboundHandlerAdapter {
    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(Reconnector.class);

    protected int retries = 0;
    protected RetryPolicy retryPolicy;

    protected TcpClient tcpClient;

    public Reconnector(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // System.out.println("Successfully established a connection to the server.");
        log.info("Successfully established a connection to the server.");
        retries = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (retries == 0) {
            // System.err.println("Lost the TCP connection with the server.");
            log.error("Lost the TCP connection with the server.");
            ctx.close();
        }

        boolean allowRetry = getRetryPolicy().allowRetry(retries);
        if (allowRetry) {

            long sleepTimeMs = getRetryPolicy().getSleepTimeMs(retries);

            // System.out.println(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs, ++retries));
            log.info(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs, ++retries));

            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> {
                // System.out.println("Reconnecting ...");
                log.info("Reconnecting ...");
                tcpClient.connect();
            }, sleepTimeMs, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelInactive();
    }

    protected RetryPolicy getRetryPolicy() {
        if (this.retryPolicy == null) {
            this.retryPolicy = tcpClient.getRetryPolicy();
        }
        return this.retryPolicy;
    }
}