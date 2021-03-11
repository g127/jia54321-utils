package com.jia54321.utils.event;

/**
 * IEventSubscriber
 * @param <T>
 */
public abstract interface IEventSubscriber<T extends EventObject<?>> {
	
	/**
	 * 
	 * @param paramT
	 */
	public abstract void action(T paramT);
}
