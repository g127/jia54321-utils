package com.jia54321.utils.netty4;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * TcpServer
 * @author G
 *
 */
@SuppressWarnings("deprecation")
public final class TcpServer {
	/** 日志 */
	private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

	private volatile EventLoopGroup bossGroup;

	private volatile EventLoopGroup workerGroup;

	private volatile ServerBootstrap bootstrap;

	private volatile boolean closed = false;

	//private final String localAddress;
	private final int localPort;
	
	/** 工作线程数  */
	private int workerGroupThreads;
	
//	private final PortStringDecoder DECODER = new PortStringDecoder();
//	private final StringEncoder ENCODER = new StringEncoder();
//	
//	private TcpServerHandler serverHandler;
	
	private TcpServerChannelInitializer channelInitializer;
	
	public TcpServer(int localPort, TcpServerChannelInitializer channelInitializer) {
		this.localPort = localPort;
		this.channelInitializer = channelInitializer;
		this.workerGroupThreads = 8;
	}
	
	public TcpServer(int localPort, TcpServerChannelInitializer channelInitializer, int workerGroupThreads) {
		this.localPort = localPort;
		this.channelInitializer = channelInitializer;
		this.workerGroupThreads = workerGroupThreads;
	}
	

	public void init() {
		closed = false;

		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup(workerGroupThreads);
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.handler(new LoggingHandler(LogLevel.DEBUG));

		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(this.channelInitializer);
		bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		
		doBind();

	}
	
	public void close() {
		closed = true;

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();

		if(log.isInfoEnabled()) {
			log.info("Stopped Tcp Server: {} ",  localPort );
		}
	}



	protected void doBind() {
	
		final TcpServer server = this;
		if (closed) {
			return;
		}

		bootstrap.bind(localPort).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.isSuccess()) {
					if(log.isInfoEnabled()) {
						log.info("Started Tcp Server: {} ",  localPort );
					}
				} else {
					if(log.isInfoEnabled()) {
						log.info("Started Tcp Server Failed: {}",  localPort );
					}
					f.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							server.doBind();

						}
					}, 5, TimeUnit.SECONDS);
				}
			}
		});
	}
	
	/**
	 * 定时任务
	 * @param command
	 * @param initialDelay
	 * @param period
	 * @param unit
	 */
	public TcpServer setScheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit){
		bossGroup.scheduleAtFixedRate(command, initialDelay, period, unit);
		return this;
	}
}
