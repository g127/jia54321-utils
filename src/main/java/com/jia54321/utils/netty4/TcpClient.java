package com.jia54321.utils.netty4;

import java.net.InetSocketAddress;

import com.jia54321.utils.netty4.policy.RetryPolicy;
import com.jia54321.utils.netty4.policy.impl.ExponentialBackOffRetry;
import com.jia54321.utils.netty4.types.JsonObjectDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * Tcp client
 * @author G
 */
//@SuppressWarnings("rawtypes")
public final class TcpClient {
	/** 日志 */
	private static Logger log = LoggerFactory.getLogger(TcpClient.class);

	/** 地址 */
	private final String host;

	/** 端口 */
	private final int port;


	/** 事件循环组 */
	private volatile EventLoopGroup workerGroup;

	/** 启动器 */
	private volatile Bootstrap bootstrap;

	/** 是否关闭 */
	private volatile boolean closed = false;

	/** 重连策略 */
	private RetryPolicy retryPolicy;

	/** 通道 */
	private Channel channel;

	/** ChannelFuture */
	private ChannelFuture future;

	/** 客户端初始化类 */
	private TcpClientChannelInitializer channelInitializer;

	public TcpClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void close() {
		closed = true;
		workerGroup.shutdownGracefully();
		if (log.isInfoEnabled()) {
			log.info("Stopped Tcp Client: " + getInfo());
		}
	}

	public void setChannelInitializer(TcpClientChannelInitializer channelInitializer) {
		this.channelInitializer = channelInitializer;
	}

	public void init() {
        closed = false;
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.group(workerGroup).channel(NioSocketChannel.class);

        // 重试策略
		this.retryPolicy = new ExponentialBackOffRetry(2000, Integer.MAX_VALUE, 60 * 1000);

    	// 默认初始化器
		if(channelInitializer == null) {
			channelInitializer = new TcpClientChannelInitializer(TcpClient.this);
		}
		bootstrap.handler(channelInitializer);
    }

	public boolean channelActive() {
		return channelInitializer.channelActive();
	}

	/** */
    public void connect() {
    	final TcpClient client = this;
        if (closed) {
            return;
        }

		synchronized (bootstrap) {
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
			future.addListener(getConnectionListener());
			this.channel = future.channel();
		}
//
//        this.future = bootstrap.connect(new InetSocketAddress(host, port));
//		this.future.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture f) throws Exception {
//                if (f.isSuccess()) {
//                	client.future = f;
//                	if (log.isInfoEnabled()) {
//                		log.info("Tcp Client Connected: " + getInfo());
//                	}
//                } else {
//					if (!future.isSuccess()) {
//						future.channel().pipeline().fireChannelInactive();
//					}
//
//					if (log.isInfoEnabled()) {
//                		 log.info("Tcp Client Failed: " + getInfo());
//                	}
//                    f.channel().eventLoop().schedule(new Runnable() {
//						@Override
//                        public void run() {
//							client.doConnect();
//
//						}
//					}, 1, TimeUnit.SECONDS);
//                }
//            }
//        });
        
    }

	private ChannelFutureListener getConnectionListener() {
		return new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					future.channel().pipeline().fireChannelInactive();
				}
			}
		};
	}

//	public ChannelFuture getFuture() {
//		return future;
//	}

	public Channel getChannel() {
		return channel;
	}

	public ChannelFuture getFuture() {
		return future;
	}

	private String getInfo() {
        return String.format("RemoteHost=%s RemotePort=%d", host, port);
    }

//
//    public <T> T sendAndGetFirst(T in, long timeout, TimeUnit unit) {
//    	List<T> ms = send(in, timeout, unit);
//    	if(null==ms || ms.size()<=0 ){
//    		return null;
//    	}
//    	return ms.get(0);
//    }
//
//    public <T> T sendAndGetLaset(T in, long timeout, TimeUnit unit) {
//    	List<T> ms = send(in, timeout, unit);
//    	if(null==ms || ms.size()<=0 ){
//    		return null;
//    	}
//		return ms.get(ms.size() - 1);
//    }
//
//    /**
//     * 发送消息
//     * @param in
//     * @param timeout
//     * @param unit
//     * @return
//     */
//	@SuppressWarnings("unchecked")
//	public <T> List<T> send(T in, long timeout, TimeUnit unit) {
//		List<T> ms = new ArrayList<T>();
//		try {
//			// Read commands from the stdin.
//			final ChannelFuture lastWriteFuture = this.future;
//			final SocketChannel channel = (SocketChannel)lastWriteFuture.channel();
//			if (null != channel && channel.isWritable()) {
//				//
//				this.clientHandler.setReceiveMsgBuffer(ms);
//
//				// Sends the received line to the server.
//				channel.writeAndFlush(in);
//
//				// 等待代码
//				lastWriteFuture.channel().closeFuture().await(timeout, unit);
//
//			} else {
//				if(logger.isInfoEnabled()) {
//					logger.info( String.format("Tcp Client Channel unWritable. %s" , channelDescText(channel)));
//				}
//			}
//		} catch (InterruptedException e) {
//			if(logger.isInfoEnabled()) {
//				logger.info(e.getMessage());
//			}
//		}
//		return ms;
//	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	/**
	 * channelDescText
	 * @param c
	 * @return
	 */
	public static String channelDescText(SocketChannel c){
		if(null == c) {
            return null;
        }
		String ip =  c.remoteAddress().getAddress().getHostAddress();
		int port = c.remoteAddress().getPort();
    	return String.format("[%s:%d IP位置反查：%s ", ip, port, "");
    }
}
