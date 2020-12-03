package com.jia54321.utils.netty4.types;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author G
 */
public class NettyUtil {
	
	public static String getRemoteIp(ChannelHandlerContext ctx) {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		InetAddress inetaddress = socketAddress.getAddress();
		String ipAddress = inetaddress.getHostAddress();
		return ipAddress;
	}

	public static boolean isWritable(Channel channel) {
		return (channel != null) && (channel.isWritable()) && (channel.isOpen()) && (channel.isActive());
	}
}
