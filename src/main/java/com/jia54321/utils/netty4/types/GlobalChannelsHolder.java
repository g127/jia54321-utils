package com.jia54321.utils.netty4.types;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 通用的通讯缓存Holder
 * 
 * @author G
 */
public class GlobalChannelsHolder {
	
	/** 日志 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalChannelsHolder.class);

	/** 格式化的消息模板 channelDescText [ID:系统端:第三方端:客户端] */
	private static final String L8_ACT_CID_CSIZE_TSIZE_IP_PORT_LOC = "[%s%s:%s:%s:%s]%s:%d%s";
	
	private static final String LOG_ADD = "+";
	private static final String LOG_DEL = "-";

	/** 锁. */
	private static final Object lock = new Object();
	
	/** G_CLIENT_ID. */
	public static final AttributeKey<String> G_CLIENT_ID = AttributeKey.valueOf("G_CLIENT_ID");
	/** scheduled asynchronous operation */
	@SuppressWarnings("rawtypes")
	public static final AttributeKey<ScheduledFuture> G_CLIENT_SCHEDULED_FUTURE = AttributeKey.valueOf("G_CLIENT_SCHEDULED_FUTURE");
	
	/** 客户端列表  */
//	private static ConcurrentMap<String, Set<Channel>> CLIENT = Maps.newConcurrentMap();
	private static Cache<String, Set<Channel>> CLIENT = CacheBuilder
			.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
//			.refreshAfterWrite(10, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
//			.expireAfterAccess(1, TimeUnit.HOURS)// 缓存过期时间和redis缓存时长一样
			.maximumSize(1000)// 设置缓存个数
			.removalListener(new RemovalListener<String, Set<Channel>>() {
				@Override
				public void onRemoval(RemovalNotification<String, Set<Channel>> notification) {
					Set<Channel> channels = notification.getValue();
					for (Channel c : channels) {
						c.closeFuture().removeListener(remover);
					}
				}
			}).
			build();
//	private static ConcurrentMap<Channel, String>  REVERSE_USER_CHANNELS = Maps.newConcurrentMap();
	private static Cache<Channel, String> REVERSE_USER_CHANNELS = CacheBuilder
			.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
//			.refreshAfterWrite(10, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
//			.expireAfterAccess(1, TimeUnit.HOURS)// 缓存过期时间和redis缓存时长一样
			.maximumSize(1000)// 设置缓存个数
			.removalListener(new RemovalListener<Channel, String>() {
				@Override
				public void onRemoval(RemovalNotification<Channel, String> notification) {
					Channel c = notification.getKey();
					c.closeFuture().removeListener(remover);
				}
			}).
			build();
	
	/** group */
	private static Map<String, Set<String>> GROUP = new ConcurrentHashMap<String, Set<String>>();

    /**
     * 用户类型
     * ClientType.
     */
    public static enum ClientType {
    	/**
    	 * {@code "SYS"} 系统类
    	 */
    	SYS(0, "S_"), 
       	/**
    	 * {@code "THIRD"} 第三方类
    	 */
    	THIRD(1, "T_"), 
       	/**
    	 * {@code "USER"} 内部用户类
    	 */
    	USER(2, "U_");
    	private String strVal;
    	ClientType(int intVal, String strVal){
    		this.strVal = strVal;
    	}
    	/**
    	 * 是否为某类型值
    	 * @param type  ClientType
    	 * @param val   待判定的字符
    	 * @return 真或假
    	 */
    	public boolean isHeadOf(String name){
    		if(null==name || name.length()<=0){
    			return false;
    		}
    		return name.indexOf(this.toString()) == 0;
    	}
    	public String head(String name){
    		if(null==name || name.length()<=0){
    			return name;
    		}
    		return addHead(this, name);
    	}
    	public static String delHead(String name){
    		if(null==name || name.length()<=0){
    			return name;
    		}
			if(name.indexOf(SYS.strVal) == 0 || name.indexOf(THIRD.strVal) == 0 || name.indexOf(USER.strVal) == 0) {
				name = name.substring(2);
			};
			return name;
    	}
    	private static String addHead(ClientType ct, String name){
			if (ct.isHeadOf(name)) {
				return name;
			} else {
	    		if(null==name || name.length()<=0){
	    			return name;
	    		}
				return ct.toString() + delHead(name);
			}
    	}
    	@Override
    	public String toString() {
    		return this.strVal;
    	}
    }

    /**
     * ChannelType.
     */
    public static final class ChannelType {
        /**
         * {@code "G"} 组
         */
        public static final String GROUP_ = "G_";
        /**
         * {@code "U"} 用户
         */
        public static final String USER_ = "U_";

    }
	
	/**
     * A {@link ChannelFutureListener} that closes the {@link Channel} which is
     * associated with the specified {@link ChannelFuture}.
     */
	public static final ChannelFutureListener CLOSE = new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				Channel c = (Channel)future.channel();
				@SuppressWarnings("rawtypes")
				ScheduledFuture scheduledFuture = c.attr(G_CLIENT_SCHEDULED_FUTURE).get();
				if(null != scheduledFuture){
					try {
						scheduledFuture.cancel(false);
					} catch (Exception e) {
					}
					scheduledFuture = null;
				}
				remove(c); 
				//logger.info(String.format("SYS_=%s, THIRD_=%s, USER_=%s, ", GlobalChannelMap.size()) + ". CLOSE ID=" + converter.attr(GlobalChannelMap.G_CLIENT_ID) +  " " + converter.toString());
				c.close();
			}
	};
	
	/**
     * A {@link ChannelFutureListener} that closes the {@link Channel} which is
     * associated with the specified {@link ChannelFuture}.
     */
    private static final ChannelFutureListener remover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            remove(future.channel());
        }
    };
	
    
	/**
	 * 返回连接数
	 * @return int
	 */
	public static int size() {
		int[] innerSize = innerSize();
		int sysSize = innerSize[0], thirdSize = innerSize[1], userSize = innerSize[2];
		return sysSize + thirdSize + userSize;
	}
    
    
	/**
	 * 返回连接数
	 * @return
	 */
	public static int[] innerSize() {
		int sysSize = 0, thirdSize = 0, userSize = 0;
		for (Map.Entry<String,Set<Channel>> entry : CLIENT.asMap().entrySet()) {
			//判断各种用户前缀
			if(ClientType.SYS.isHeadOf(entry.getKey())) {
				sysSize += entry.getValue().size();
			} else if(ClientType.THIRD.isHeadOf(entry.getKey())) {
				thirdSize += entry.getValue().size();
			} else if(ClientType.USER.isHeadOf(entry.getKey())) {
				userSize += entry.getValue().size();
			} 
		}
		return new int[]{sysSize, thirdSize, userSize};
	}
	
	/**
	 * 
	 * @param clientId
	 * @return
	 */
	private static Set<Channel> client(String clientId) {
		Set<Channel> userChannels = CLIENT.getIfPresent(clientId);
		if (null == userChannels) {
			synchronized (lock) {
				userChannels = new TreeSet<Channel>();
				CLIENT.put(clientId, userChannels);
			}
		}
		return userChannels;
	}

	/**
	 * 
	 * @param groupKey
	 * @return
	 */
	private static Set<String> group(String groupKey) {
		Set<String> users = GROUP.get(groupKey);
		if (null == users) {
			users = new TreeSet<String>();
			GROUP.put(groupKey, users);
		}
		return users;
	}
	
	/**
	 * 保持单连接
	 * @param clientId
	 * @param userNewChannel
	 */
	@SuppressWarnings("unused")
	private static boolean clientOnlyKeepOne(String clientId, Channel userNewChannel){
		if(null == clientId) {
            return false;
        }
		Set<Channel> userChannels = client(clientId);
		
		for (Channel oldChannel : userChannels) {
			if (null != oldChannel  
					&& oldChannel.remoteAddress() != userNewChannel.remoteAddress()
					&& oldChannel.isOpen()) {
				try {
					oldChannel.close();
				} catch (Exception e) {
					logger.error("关闭通道错误", e);
				}
			}
		}
		userChannels.clear();
		
		return userChannels.add(userNewChannel);
	}
	
	/**
	 * 保持多连接
	 * @param clientId
	 * @param userNewChannel
	 */
	private static boolean clientOnlyKeepMany(String clientId,  Channel userNewChannel){
		if(null == clientId) {
            return false;
        }
		Set<Channel> userChannels = client(clientId);
		Set<Channel> userNewChannels = new TreeSet<Channel>();
		for (Channel oldChannel : userChannels) {
			if (null != oldChannel  
					&& oldChannel.remoteAddress() != userNewChannel.remoteAddress()
					&& oldChannel.isOpen()) {
				userNewChannels.add(oldChannel);
			}
		}
		userChannels.clear();
		userChannels.addAll(userNewChannels);
		
		return userChannels.add(userNewChannel);
	}
	
	/**
	 * 
	 * @param ct               用户类型
	 * @param user             用户
	 * @param encodingGroups   用户组， 将用户放入这些组中
	 * @param channel          通道
	 */
	public static void put(ClientType ct, String user, String encodingGroups, Channel channel) {
		put(ct.head(user), encodingGroups, channel);
	}
	
	/**
	 * 用户 ，用户组   userChannel 
	 * @param encodingUser     用户
	 * @param encodingGroups   用户组， 将用户放入这些组中
	 * @param channel          通道
	 */
	public static void put(String encodingUser, String encodingGroups, Channel channel) {
		String user = encodingUser;
		if (null != user && !"".equals(user)) {
			//boolean added = clientOnlyKeepOne(user, userChannel);
			boolean added = clientOnlyKeepMany(user, channel);
			if(added){
				channel.closeFuture().addListener(remover);
			}
			
			REVERSE_USER_CHANNELS.put(channel, user);
			channel.attr(G_CLIENT_ID).set(user);
			
			if(logger.isInfoEnabled()) {
				logger.info(channelDescText(LOG_ADD, channel));
			}
		}
		
		if (null != encodingGroups && !"".equals(encodingGroups)) {
			String[] groups = encodingGroups.split("|");
			
			if (groups.length > 0) {
				for (int i = 0; i < groups.length; i++) {
					Set<String> groupUsers = group(groups[i]);
					groupUsers.add(user);
				}
			}
		}
		
	}
	
	/** 获取用户通道 */
	public static Set<Channel> get(String encodingUser, String encodingGroups){
		Set<Channel> channels = new TreeSet<Channel>();

		String user = encodingUser;
		if (null != user && !"".equals(user)) {
			channels.addAll(client(user));
		}
		
		if (null != encodingGroups && !"".equals(encodingGroups)) {
			String[] groups = encodingGroups.split("|");
			if (groups.length > 0) {
				for (int i = 0; i < groups.length; i++) {
					Set<String> groupUsers = group(groups[i]);
					for (String other : groupUsers) {
						channels.addAll(client(other));
					}
				}
			}
		}
		return channels;
	}
	
	/**
	 * reverseChannel
	 * @param channel
	 * @return userId
	 */
	public static String reverseChannel(Channel channel){
		return REVERSE_USER_CHANNELS.getIfPresent(channel);
	}
	
	/**
	 * 删除
	 * @param channel
	 */
	public static void remove(Channel channel){
		String clientId = reverseChannel(channel);
		if (null != clientId) {
			if(logger.isInfoEnabled()) {
				logger.info(channelDescText(LOG_DEL, channel));
			}
			REVERSE_USER_CHANNELS.invalidate(channel);
			//REVERSE_USER_CHANNELS.remove(channel);
			client(clientId).remove(channel);
			
			channel.closeFuture().removeListener(remover);
		}
	}
	
	/**
	 * 
	 * @param ct
	 * @param user
	 */
	public static void removeByKey(ClientType ct, String user) {
		String clientId = ClientType.addHead(ct, user);
		Set<Channel> s = client(clientId);
		for (Channel channel : s) {
			remove(channel);
		}
	}
	
	/**
	 * channelDescText
	 * @param c
	 * @return
	 */
	public static String channelDescText(Channel c){
		return channelDescText(null, c);
    }
	
	/**
	 * channelDescText
	 * @param c
	 * @return
	 */
	public static String channelDescText(String action, Channel c){
		if(null == c || !(c instanceof io.netty.channel.socket.SocketChannel) ) {
            return null;
        }
		String actName = null==action?"":action;
		io.netty.channel.socket.SocketChannel sc = (io.netty.channel.socket.SocketChannel) c;
		String ip =sc.remoteAddress().getAddress().getHostAddress();
		int port = sc.remoteAddress().getPort();
		int[] sizes = innerSize();
		return String.format(L8_ACT_CID_CSIZE_TSIZE_IP_PORT_LOC, actName, c.attr(G_CLIENT_ID), sizes[0],sizes[1],sizes[2] , ip, port, "");
    }
	//====================================================================================================================//
	
	/**
	 * IP相同
	 * @param c
	 * @return
	 */
	public static boolean isSameHostAddress(Channel c) {
		if(c == null ) {
            return false;
        }
		if (c instanceof io.netty.channel.socket.SocketChannel) {
			io.netty.channel.socket.SocketChannel socketChannel = (io.netty.channel.socket.SocketChannel) c;
			return socketChannel
					.localAddress()
					.getAddress()
					.getHostAddress()
					.equals(socketChannel.remoteAddress().getAddress().getHostAddress());
		}
		if (c instanceof java.nio.channels.SocketChannel) {
		}
		return false;
	}
	
//	public static void main(String[] args) {
//		System.out.println(GlobalChannelsHolder.isSameHostAddress(null));
//	}
}
