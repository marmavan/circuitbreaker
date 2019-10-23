package com.karthik.circuitbreaker;

import java.util.concurrent.TimeUnit;

public class TimeoutThread extends Thread {

	private long timeoutSeconds;
	private CircuitBreaker breaker;

	public TimeoutThread(long timeoutSeconds, CircuitBreaker breaker) {
		this.timeoutSeconds = timeoutSeconds;
		this.breaker = breaker;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(timeoutSeconds));
			breaker.getCurrentFailures().decrementAndGet();
			breaker.getCurrentState().set(com.karthik.circuitbreaker.State.HALF_OPEN);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
