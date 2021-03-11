package com.jia54321.utils.event;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印日志事件
 */
public class PrintEvent extends EventObject<Object> {

	static final Logger log = LoggerFactory.getLogger(EventManagerTest.class);

	/** 序列化ID. */
	private static final long   serialVersionUID = 6836373264108086488L;

	/** 事件ID . */
	public static final String EVENT_ID = PrintEvent.class.getCanonicalName();

//	static {
//		// 异步订阅
//		EventManager.asyncSubscriberEvent(PrintEvent.EVENT_ID, new IEventSubscriber<PrintEvent>(){
//			@Override
//			public void action(PrintEvent paramT) {
//				if(paramT.getSource() instanceof String) {
//					System.out.println(paramT.getSource());
//				}
//			}
//		});
//
//		// 同步订阅
//		EventManager.subscriberEvent(PrintEvent.EVENT_ID, new IEventSubscriber<PrintEvent>(){
//			@Override
//			public void action(PrintEvent paramT) {
//				if(paramT.getSource() instanceof String) {
//					System.out.println(paramT.getSource());
//				}
//			}
//		});
//	}
	
	public PrintEvent(Object eventSource) {
		super(eventSource, EVENT_ID);
	}
}
