package com.jia54321.utils.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventBus
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventBus {
	private Map<String, List<EventSubscriberDesc>> subscriberCache = null;

	private static final Object LOCK = new Object();

	public static final EventBus INSTANCE = new EventBus();

	private Map<String, List<EventSubscriberDesc>> getSubscriberCache() {
		if (this.subscriberCache == null) {
			synchronized (LOCK) {
				this.subscriberCache = new HashMap<String, List<EventSubscriberDesc>>();
			}
		}
		return this.subscriberCache;
	}

	public void addSubscriber(String eventId, EventSubscriberDesc subscriber) {
		List list = (List) getSubscriberCache().get(eventId);
		if (list == null) {
			list = new ArrayList();
		}
		list.add(subscriber);
		getSubscriberCache().put(eventId, list);
	}

	public List<EventSubscriberDesc> getSubscribers(String eventId) {
		List list = (List) getSubscriberCache().get(eventId);
		return list;
	}

	public boolean removeSubscriber(String eventId,
			EventSubscriberDesc subscriber) {
		List list = (List) getSubscriberCache().get(eventId);
		if (list != null) {
			return list.remove(subscriber);
		}
		return false;
	}

	public void clearAllSubscriber() {
		getSubscriberCache().clear();
	}

	public void clearSubscriber(String eventId) {
		List list = (List) getSubscriberCache().get(eventId);
		if (list != null) {
            list.clear();
        }
	}
}
