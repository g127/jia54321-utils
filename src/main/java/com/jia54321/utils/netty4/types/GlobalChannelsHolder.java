package com.jia54321.utils.netty4.types;

import com.jia54321.utils.NumberUtils;
import com.jia54321.utils.lock.MapWithLock;
import com.jia54321.utils.lock.SetWithLock;
import com.jia54321.utils.lock.WriteLockHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用的通讯缓存Holder
 *
 * @author G
 */
@SuppressWarnings("unchecked")
public class GlobalChannelsHolder {
    /** 日志  */
    static               Logger                                    logger                    = LoggerFactory.getLogger(GlobalChannelsHolder.class);
    /** 增加通道日志前缀  */
    static final         String                                    LOG_ADD                   = "+";
    /** 移除通道日志前缀  */
    static final         String                                    LOG_DEL                   = "-";
    static final         String                                    REGEX_VERTICAL_BAR        = "\\|";
    /**  格式化的消息模板 channelDescText [ID:系统端:第三方端:客户端] L8_ACT_CID_CSIZE_TSIZE_IP_PORT_LOC  */
    static final         String                                    L8_LOC                    = "[%s%s:%s:%s:%s]%s:%d%s";
    private final static AtomicInteger                             COUNT_SYS                 = new AtomicInteger(0);
    private final static AtomicInteger                             COUNT_USER                = new AtomicInteger(0);
    private final static AtomicInteger                             COUNT_THIRD               = new AtomicInteger(0);
    /** 锁. */
    private static final Object                                    lock                      = new Object();
    /**  G_CHANNEL_CTX. */
    public static final  AttributeKey<ChannelCtx>                  G_CHANNEL_CTX             = AttributeKey.valueOf("G_CHANNEL_CTX");
    /** scheduled asynchronous operation */
    @SuppressWarnings("rawtypes")
    public static final  AttributeKey<ScheduledFuture>             G_CLIENT_SCHEDULED_FUTURE = AttributeKey.valueOf("G_CLIENT_SCHEDULED_FUTURE");
    /**  组 */
    private final static MapWithLock<String, SetWithLock<String>>  GROUP                     = new MapWithLock<>(new HashMap<String, SetWithLock<String>>(100));
    /** 客户端列表*/
    private final static MapWithLock<String, SetWithLock<Channel>> CLIENT                    = new MapWithLock<>(new HashMap<String, SetWithLock<Channel>>(100));

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

        ClientType(int intVal, String strVal) {
            this.strVal = strVal;
        }

        /**
         * 获取 正式名字  = 客户端类型 + '_' + 客户端名称
         * @param clientType 客户端类型
         * @param name 客户端名称
         * @return
         */
        public static String originName(ClientType clientType, String name) {
            String originName = name;
            if (null == name || name.length() <= 0) {
                return name;
            }
            if (name.indexOf(SYS.strVal) == 0 || name.indexOf(THIRD.strVal) == 0 || name.indexOf(USER.strVal) == 0) {
                // warning
            }
            return clientType.toString() + name;
        }

        /**
         * 获取 客户端名称
         * @param originName 正式名字  = 客户端类型 + '_' + 客户端名称
         * @return 客户端名称
         */
        public static String name(String originName) {
            String name = originName;
            if (null == name || name.length() <= 0) {
                return name;
            }
            if (name.indexOf(SYS.strVal) == 0 || name.indexOf(THIRD.strVal) == 0 || name.indexOf(USER.strVal) == 0) {
                name = name.substring(2);
            }
            return name;
        }

        /**
         * 获取 客户端类型
         * @param originName 正式名字  = 客户端类型 + '_' + 客户端名称
         * @return 客户端类型
         */
        public static ClientType fromOriginName(String originName) {
            ClientType returnVal = null;
            if (originName.indexOf(SYS.strVal) == 0) {
                returnVal = SYS;
            }
            if (originName.indexOf(THIRD.strVal) == 0) {
                returnVal = THIRD;
            }
            if (originName.indexOf(USER.strVal) == 0) {
                returnVal = USER;
            }
            return returnVal;
        }

        @Override
        public String toString() {
            return this.strVal;
        }
    }

    /**
     * 通道上下文
     */
    public static class ChannelCtx {
        public String          id;         // 唯一id
        public String          clientOriginName; // 客户端正式名字
        public ClientType      clientType; // 客户端类型
        public String          clientName;     // 用户id，代笔一个用户userId。
        public String          groupId;    // 组Id。
        //
        public ScheduledFuture sf;

        public ChannelCtx(String id, String clientOriginName, String groupId) {
            this.id = id;
            this.clientOriginName = clientOriginName;
            this.clientType = ClientType.fromOriginName(clientOriginName);
            this.clientName = ClientType.name(clientOriginName);
            this.groupId = groupId;
        }

        public ChannelCtx(String id, ClientType clientType, String clientName, String groupId) {
            this.id = id;
            this.clientOriginName = ClientType.originName(clientType, clientName);
            this.clientType = clientType;
            this.clientName = clientName;
            this.groupId = groupId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChannelCtx that = (ChannelCtx) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(clientOriginName, that.clientOriginName) &&
                    clientType == that.clientType &&
                    Objects.equals(clientName, that.clientName) &&
                    Objects.equals(groupId, that.groupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, clientOriginName, clientType, clientName, groupId);
        }
    }


    /**
     * A {@link ChannelFutureListener} that closes the {@link Channel} which is
     * associated with the specified {@link ChannelFuture}.
     */
    public static final ChannelFutureListener CLOSE = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            Channel c = (Channel) future.channel();
            @SuppressWarnings("rawtypes")
            ScheduledFuture scheduledFuture = c.attr(G_CLIENT_SCHEDULED_FUTURE).get();
            if (null != scheduledFuture) {
                try {
                    scheduledFuture.cancel(false);
                } catch (Exception e) {
                }
                scheduledFuture = null;
            }
            remove(c);
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
     *
     * @return int
     */
    public static int size() {
        int[] innerSize = innerSize();
        int   sysSize   = innerSize[0], thirdSize = innerSize[1], userSize = innerSize[2];
        return sysSize + thirdSize + userSize;
    }


    /**
     * 返回连接数
     *
     * @return
     */
    public static int[] innerSize() {
        int sysSize = 0, thirdSize = 0, userSize = 0;

        if (null != CLIENT.copy()) {
            for (Map.Entry<String, SetWithLock<Channel>> entry : CLIENT.copy().entrySet()) {
                //判断各种用户前缀
                ClientType clientType = ClientType.fromOriginName(entry.getKey());

                if (ClientType.SYS == clientType) {
                    sysSize += entry.getValue().size();
                } else if (ClientType.THIRD == clientType) {
                    thirdSize += entry.getValue().size();
                } else if (ClientType.USER == clientType) {
                    userSize += entry.getValue().size();
                }
            }
        }
        return new int[]{sysSize, thirdSize, userSize};
    }

    /**
     * @param clientId
     * @return
     */
    private static SetWithLock<Channel> client(String clientId) {
        SetWithLock<Channel> userChannels = CLIENT.get(clientId);
        if (null == userChannels) {
            userChannels = new SetWithLock<Channel>(new TreeSet<Channel>());
            CLIENT.putIfAbsent(clientId, userChannels);
        }
        return userChannels;
    }

    /**
     * @param groupKey
     * @return
     */
    private static SetWithLock<String> group(String groupKey) {
        SetWithLock<String> users = GROUP.get(groupKey);
        if (null == users) {
            users = new SetWithLock(new TreeSet<String>());
            GROUP.put(groupKey, users);
        }
        return users;
    }

    /**
     * 保持单连接
     *
     * @param clientId
     * @param userNewChannel
     */
    @SuppressWarnings("unused")
    private static boolean clientOnlyKeepOne(String clientId, Channel userNewChannel) {
        if (null == clientId) {
            return false;
        }
        SetWithLock<Channel> userChannels = client(clientId);

        for (Channel oldChannel : userChannels.getObj()) {
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
     *
     * @param clientId
     * @param userNewChannel
     */
    private static boolean clientOnlyKeepMany(String clientId, Channel userNewChannel) {
        if (null == clientId) {
            return false;
        }
        SetWithLock<Channel> userChannels    = client(clientId);

        userChannels.handle(new WriteLockHandler<Set<Channel>>() {
            @Override
            public void handler(Set<Channel> channels) {
                // 添加通道
                channels.add(userNewChannel);
                // 清理已经失效的通道
                clearNotOpenChannels(channels);
            }
        });
        return true;
    }

    /**
     * 清理已经失效的通道
     * @param channels 通道
     */
    private static boolean clearNotOpenChannels(final Set<Channel> channels) {
        // notOpenChannels
        Set<Channel> notOpenChannels = new TreeSet<Channel>();
        // channels
        for (Channel channel : channels) {
            if (null != channel && ( !channel.isOpen() ) ) {
                notOpenChannels.add(channel);
            }
        }
        // channels
        return channels.removeAll(notOpenChannels);
    }

    /**
     * @param ct             用户类型
     * @param user           用户
     * @param encodingGroups 用户组， 将用户放入这些组中
     * @param channel        通道
     */
    public static void put(ClientType ct, String user, String encodingGroups, Channel channel) {
        put(ClientType.originName(ct, user), encodingGroups, channel);
    }

    /**
     * 用户 ，用户组   userChannel
     *
     * @param encodingUser   用户
     * @param encodingGroups 用户组， 将用户放入这些组中
     * @param channel        通道
     */
    private static void put(String encodingUser, String encodingGroups, Channel channel) {
        String user = encodingUser;
        if (null != user && !"".equals(user)) {
            //boolean added = clientOnlyKeepOne(user, userChannel);
            boolean added = clientOnlyKeepMany(user, channel);
            if (added) {
                channel.closeFuture().addListener(remover);
            }

            //
            ChannelCtx channelCtx = new ChannelCtx(channel.id().asLongText(), user, encodingGroups);
            channel.attr(G_CHANNEL_CTX).set( channelCtx );

            if (logger.isInfoEnabled()) {
                logger.info(channelDescText(LOG_ADD, channel));
            }
        }

        if (null != encodingGroups && !"".equals(encodingGroups)) {
            String[] groups = encodingGroups.split("|");

            if (groups.length > 0) {
                for (int i = 0; i < groups.length; i++) {
                    SetWithLock<String> groupUsers = group(groups[i]);
                    groupUsers.add(user);
                }
            }
        }

    }

    /**
     * 获取用户通道
     */
    public static SetWithLock<Channel> get(String encodingUser, String encodingGroups) {
        SetWithLock<Channel> holder = new SetWithLock(new TreeSet<Channel>());
        Set<Channel> holderObj = holder.getObj();
        // 获取encodingUser的所有客户端
        clientHolder(encodingUser, holderObj);
        // 获取encodingGroups的所有客户端
        groupHolder(encodingGroups, holderObj);
        // holder
        return holder;
    }

//    /**
//     * reverseChannel
//     *
//     * @param channel
//     * @return userId
//     */
//    public static String reverseChannel(Channel channel) {
//        return REVERSE_USER_CHANNELS.get(channel);
//    }

    /**
     * 删除
     *
     * @param channel
     */
    public static void remove(Channel channel) {
        ChannelCtx ctx      = channel.attr(G_CHANNEL_CTX).get();
        String     clientId = ctx.clientOriginName;
        if (null != clientId) {
            if (logger.isInfoEnabled()) {
                logger.info(channelDescText(LOG_DEL, channel));
            }
            client(clientId).remove(channel);

            channel.closeFuture().removeListener(remover);
        }
    }

    /**
     * @param ct
     * @param user
     */
    public static void removeByKey(ClientType ct, String user) {
        String       clientId =  ClientType.originName(ct, user);
        SetWithLock<Channel> s        = client(clientId);
        for (Channel channel : s.getObj()) {
            remove(channel);
        }
    }

    /**
     * channelDescText
     *
     * @param c
     * @return
     */
    public static String channelDescText(Channel c) {
        return channelDescText(null, c);
    }

    /**
     * channelDescText
     *
     * @param c
     * @return
     */
    public static String channelDescText(String action, Channel c) {
        if (null == c) {
            return "";
        }
        // 如果remoteAddress为空，则默认为 "UNKNOWN:"
        String remoteAddress = getRemoteAddress(c);
        String[]   remote  = remoteAddress.split(":");
        //
        String     actName = (null == action) ? "" : action;
        ChannelCtx ctx     = c.attr(G_CHANNEL_CTX).get();
        String     ip      = remote[0];
        int        port    = NumberUtils.toInt(remote[1], -1);
        int[]      sizes   = innerSize();

        return String.format(L8_LOC, //
                actName, ctx.clientOriginName, sizes[0], sizes[1], sizes[2], ip, port, "");
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

    // ================================================================================
    /**
     *
     * @param groups
     * @param holder
     * @return
     */
    private static Set<Channel> groupHolder(String[] groups, Set<Channel> holder) {
        if (null != groups && groups.length > 0) {
            for (String group : groups) {
                groupHolder(group, holder);
            }
        }
        return holder;
    }


    /**
     *
     * @param groups
     * @param holder
     * @return
     */
    private static Set<Channel> groupHolder(Set<String> groups, Set<Channel> holder) {
        if (null != groups && groups.size() > 0) {
            for (String group : groups) {
                groupHolder(group, holder);
            }
        }
        return holder;
    }

    /**
     *
     * @param group
     * @param holder
     * @return
     */
    private static Set<Channel> groupHolder(String group, Set<Channel> holder) {
        if (null != group && group.length() > 0) {
            //
            if (group.indexOf('|') > 0) {
                String[] groups = group.split(REGEX_VERTICAL_BAR);
                return groupHolder(groups, holder);
            } else {
                //
                SetWithLock<String> groupClients = group(group);
                Set<String>         clients      = groupClients.getObj();
                clientHolder(clients, holder);
            }
        }
        return holder;
    }

    /**
     *
     * @param clients
     * @param holder
     * @return
     */
    private static Set<Channel> clientHolder(Set<String> clients, Set<Channel> holder) {
        if (null != clients && clients.size() > 0) {
            for (String client : clients) {
                clientHolder(client, holder);
            }
        }
        return holder;
    }

    /**
     *
     * @param client
     * @param holder
     * @return
     */
    private static Set<Channel> clientHolder(String client, Set<Channel> holder) {
        if (null != client && !"".equals(client)) {
            // clientId
            SetWithLock<Channel> clientChannels = client(client);
            //
            if (null != clientChannels && null != clientChannels.getObj()) {
                clientChannels.handle(new WriteLockHandler<Set<Channel>>() {
                    @Override
                    public void handler(Set<Channel> channels) {
                        for (Channel c : channels) {
                            holder.add(c);
                        }
                    }
                });
            }
        }
        return holder;
    }
    //====================================================================================================================//

    /**
     * IP相同
     *
     * @param c
     * @return
     */
    public static boolean isSameHostAddress(Channel c) {
        if (c == null) {
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


}
