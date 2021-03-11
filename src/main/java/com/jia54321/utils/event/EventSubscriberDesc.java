package com.jia54321.utils.event;

/**
 * EventSubscriberDesc
 */
public class EventSubscriberDesc {
	private boolean async;
	private IEventSubscriber<?> subscriber;
	private int sort;

	public EventSubscriberDesc(IEventSubscriber<?> eventSubscriber,
			boolean newAsync, int newsort) {
		this.subscriber = eventSubscriber;
		this.async = newAsync;
		this.sort = newsort;
	}

	public EventSubscriberDesc(IEventSubscriber<?> eventSubscriber,
			boolean newAsync) {
		this.subscriber = eventSubscriber;
		this.async = newAsync;
	}

	public boolean isAsync() {
		return this.async;
	}

	public void setAsync(boolean newasync) {
		this.async = newasync;
	}

	public IEventSubscriber<?> getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(IEventSubscriber<?> newsubscriber) {
		this.subscriber = newsubscriber;
	}

	@Override
    public int hashCode() {
		return this.subscriber.hashCode();
	}

	@Override
    public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof EventSubscriberDesc))) {
			return false;
		}
		return this.subscriber.equals(((EventSubscriberDesc) obj).subscriber);
	}

	@Override
    public String toString() {
		return super.toString();
	}

	public int getSort() {
		return this.sort;
	}

	public void setSort(int newSort) {
		this.sort = newSort;
	}
}
