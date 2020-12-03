package com.jia54321.utils.netty4;

import com.jia54321.utils.netty4.channel.Reconnector;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TcpClientChannelInitializer extends ChannelInitializer<SocketChannel>{

	/** 日志 */
	private static Logger logger = LoggerFactory.getLogger(TcpClientChannelInitializer.class);

	protected Reconnector reconnector;

	protected final TcpClient client;

	private transient boolean channelActive = false;

	public TcpClientChannelInitializer(final TcpClient client) {
		if (client == null) {
			throw new IllegalArgumentException("TcpClient can not be null.");
		}
		this.client = client;
		this.reconnector = new Reconnector(client);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(this.reconnector);
//		pipeline.addFirst(new ChannelInboundHandlerAdapter() {
//			@Override
//			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//				super.channelInactive(ctx);
//				channelActive = false;
//				ctx.channel().eventLoop().schedule(new Runnable() {
//					@Override
//					public void run() {
//						client.doConnect();
//
//					}
//				}, 5, TimeUnit.SECONDS);
//			}
//
//			@Override
//			public void channelActive(ChannelHandlerContext ctx)
//					throws Exception {
//				super.channelActive(ctx);
//				channelActive = true;
//			}
//		});
	}

	public boolean channelActive(){
		return channelActive;
	}

}
