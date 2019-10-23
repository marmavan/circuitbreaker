package com.karthik.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Hello world!
 * @param <I>
 *
 */
public class CircuitBreaker<I, O> {
	private AtomicInteger currentFailures = new AtomicInteger();
	private AtomicReference<State> currentState = new AtomicReference<>();
	private long timeoutSeconds;
	private int threshold;
	
	public CircuitBreaker(int threshold, long timeoutSeconds) {
		this.threshold = threshold;
		this.timeoutSeconds = timeoutSeconds;
		this.currentState.set(State.CLOSED);
	}
	
	public O execute(Function<I, O> function, I input) throws CircuitBreakerOpenException {
		if (currentState.get() == State.OPEN) {
			throw new CircuitBreakerOpenException();
		}

		O result = null;
		try {
			result = function.apply(input);
			if (currentState.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
				currentFailures.set(0);
			}
		} catch (Exception ex) {
			int value = currentFailures.incrementAndGet();
			if (value == threshold) {
				currentState.set(State.OPEN);
				TimeoutThread timeoutThread = new TimeoutThread(timeoutSeconds, this);
				timeoutThread.start();
			}
		}
		return result;
	}
	
	public AtomicInteger getCurrentFailures() {
		return currentFailures;
	}

	public void setCurrentFailures(AtomicInteger currentFailures) {
		this.currentFailures = currentFailures;
	}

	public AtomicReference<State> getCurrentState() {
		return currentState;
	}

	public void setCurrentState(AtomicReference<State> currentState) {
		this.currentState = currentState;
	}

}
