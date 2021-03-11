package com.jia54321.utils.event;

import java.io.Serializable;

/**
 * 事件对象
 *
 * @param <T>
 */
public class EventObject<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private T source;
	private String id;

	public EventObject(T eventSource, String eventId) {
		this.source = eventSource;
		this.id = eventId;
	}

	public EventObject(T eventSource) {
		this.source = eventSource;
	}

	public T getSource() {
		return this.source;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String newid) {
		this.id = newid;
	}

	public void setSource(T newsource) {
		this.source = newsource;
	}
}
