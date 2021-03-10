package com.jia54321.utils.netty4.types;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.jia54321.utils.NumberUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author G
 */
public class NettyUtil {

	/**
	 * 获取连接方ip
	 * @param ctx 上下文
	 * @return ip
	 */
	public static String getRemoteIp(ChannelHandlerContext ctx) {
		return getRemoteIp(ctx.channel());
	}

	/**
	 * 获取连接方ip
	 * @param c 通道
	 * @return ip
	 */
	public static String getRemoteIp(Channel c) {
		return getRemoteAddress(c).split(":")[0];
	}

	public static String getRemoteAddress(Channel c) {
		String remoteAddress = "unknown:0";
		// c.remoteAddress() notEmpty
		if (null != c.remoteAddress() && !"".equals(c.remoteAddress())) {
			//  see InetSocketAddress.toString()
			remoteAddress = c.remoteAddress().toString();
		}
		return remoteAddress;
	}

	public static boolean isWritable(Channel channel) {
		return (channel != null) && (channel.isWritable()) && (channel.isOpen()) && (channel.isActive());
	}
}
