package com.jia54321.utils.event;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jia54321.utils.ExecutorServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * EventManager
 *
 */
@SuppressWarnings({"unchecked","rawtypes" })
public class EventManager {
	
	/** 日志 */
	private static Logger logger = LoggerFactory.getLogger(EventManager.class);
	
	private static final String EVENT_INFO = "发布事件,类：{0},ID:{1}";
	private static final String SUBSCRIBERS_INFO = "事件(ID:{0})的订阅者个数为:{1}";
//	private static final String SUBSCRIBERS_ASYNC_INFO = "异步事件(ID:{0})的订阅者个数为:{1}";
//	private static final String SUBSCRIBERS_SYNC_INFO = "同步事件(ID:{0})的订阅者个数为:{1}";
//	private static final String SYNCEVENT_ABORT_INFO = "同步事件(ID:{0})的订阅者:{1}属性abort值为:{2}; 属性cancelSubscriber值为:{3}";
//	private static final String METHOD = "action";

	/**. */
	private static final ScheduledExecutorService ASYNC_POOL = ExecutorServiceUtil.newScheduledExecutorService();
	
	//
	private static final ThreadLocal<Map<String, Object>> eventCache = new ThreadLocal();
	
	
	public static <T extends EventObject<?>> void publishEvent(final T event) {
		String eventId = event.getId();
	    try {
			if (eventCache.get() == null) {
				eventCache.set(new HashMap());
			}
			if (((Map) eventCache.get()).containsKey(eventId)) {
				return;
			}
			((Map) eventCache.get()).put(eventId, event);
			
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(EVENT_INFO, new Object[] { event.getClass().getName(), event.getId() }));
			}
			List<EventSubscriberDesc> list = EventBus.INSTANCE.getSubscribers(event.getId());
			if ((list != null) && (list.size() > 0)) {
		        if (logger.isDebugEnabled()) {
			          logger.debug(MessageFormat.format(SUBSCRIBERS_INFO, new Object[] { event.getId(), Integer.valueOf(list.size()) }));
			    }
				for (final EventSubscriberDesc subscriber : list) {
					final IEventSubscriber eventSubscriber = subscriber.getSubscriber();
					if(null == eventSubscriber) {
                        continue;
                    }
					if (!subscriber.isAsync()) {
						eventSubscriber.action(event);
					} else {
						ASYNC_POOL.schedule(new TimerTask() {
							@Override
							public void run() {
								eventSubscriber.action(event);
							}
						}, 1, TimeUnit.MILLISECONDS); 
					}
				}
			}
	    } finally {
			((Map) eventCache.get()).remove(eventId);
		}
		((Map) eventCache.get()).remove(eventId);
	}

	public static <T extends EventObject<?>> void subscriberEvent(
			String eventId, IEventSubscriber<T> subscriber, boolean async,
			int sort) {
		EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(
				subscriber, async, sort));
	}

	public static <T extends EventObject<?>> void subscriberEvent(
			String eventId, IEventSubscriber<T> subscriber, boolean async) {
		EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(
				subscriber, async));
	}
	
	public static <T extends EventObject<?>> void subscriberEvent(
			String eventId, IEventSubscriber<T> subscriber) {
		EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(
				subscriber, false));
	}
	
	public static <T extends EventObject<?>> void asyncSubscriberEvent(
			String eventId, IEventSubscriber<T> subscriber) {
		EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(
				subscriber, true));
	}
	
	public static <T extends EventObject<?>> void cancelSubscriberEvent(
			String eventId, IEventSubscriber<T> subscriber) {
		EventBus.INSTANCE.removeSubscriber(eventId, new EventSubscriberDesc(
				subscriber, false));
	}
	
	//===============================================================================================
	
	/**
	 * 同步
	 * @param eventId
	 * @param subscribers
	 */
	public static <T extends EventObject<?>> void subscribersEvent(
			String eventId, List<IEventSubscriber<T>> subscribers) {
		for (IEventSubscriber<T> subscriber : subscribers) {
			EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(subscriber, false));
		}
	}
	
	/**
	 * 异步
	 * @param eventId
	 * @param subscribers
	 */
	public static <T extends EventObject<?>> void asyncSubscribersEvent(
			String eventId, List<IEventSubscriber<T>> subscribers) {
		for (IEventSubscriber<T> subscriber : subscribers) {
			EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(subscriber, true));
		}
	}

	/**
	 * 
	 * @param eventId
	 * @param subscribers
	 * @param async
	 */
	public static <T extends EventObject<?>> void subscribersEvent(
			String eventId, List<IEventSubscriber<T>> subscribers, boolean async) {
		for (IEventSubscriber<T> subscriber : subscribers) {
			EventBus.INSTANCE.addSubscriber(eventId, new EventSubscriberDesc(subscriber, async));
		}
	}
}
