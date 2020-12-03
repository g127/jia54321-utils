package com.jia54321.utils.netty4.types;

/**
 * @author G
 */
public class SequenceUtil {
	
	private static int SEQ_MAX_VALUE = 19999;

	private static volatile int sequenceId = 0;
	private static volatile int sequenceId255 = 0;

	public static synchronized int getSequence() {
		sequenceId += 1;

		if (sequenceId >= SEQ_MAX_VALUE) {
			sequenceId = 0;
		}
		return sequenceId;
	}

	public static synchronized int getSequence255() {
		sequenceId255 += 1;
		if (sequenceId255 >= 255) {
			sequenceId255 = 0;
		}
		return sequenceId255;
	}
}
