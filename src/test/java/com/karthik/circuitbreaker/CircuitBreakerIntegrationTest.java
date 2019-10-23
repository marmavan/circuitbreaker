package com.karthik.circuitbreaker;

import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class CircuitBreakerIntegrationTest {
	
	
	@Test
	public void canExecuteWhenAllSuccessful() throws CircuitBreakerOpenException {
		CircuitBreaker<String, String> cb = new CircuitBreaker<>(5, 10);
		for (int i = 0; i < 6; i++) {
			cb.execute((str) -> {
				return str;
			}, "hello");
		}
	}
	
	@Test
	public void canExecuteWhenFailureWithinThreshold() throws CircuitBreakerOpenException {
		CircuitBreaker<String, String> cb = new CircuitBreaker<>(5, 10);
		for (int i = 0; i < 2; i++) {
			cb.execute((str) -> {
				return str;
			}, "hello");
		}
		for (int i = 0; i < 4; i++) {
			cb.execute((str) -> {
				throw new RuntimeException();
			}, "hello");
		}
		for (int i = 0; i < 2; i++) {
			cb.execute((str) -> {
				return str;
			}, "hello");
		}
	}
	
	@Test(expectedExceptions = CircuitBreakerOpenException.class)
	public void canThrowExceptionWhenFailureBeyondThreshold() throws CircuitBreakerOpenException {
		CircuitBreaker<String, String> cb = new CircuitBreaker<>(5, 10);
		for (int i = 0; i < 6; i++) {
			cb.execute((str) -> {
				throw new RuntimeException();
			}, "hello");
		}
	}
	
	@Test
	public void canExecuteAfterTimeout() throws CircuitBreakerOpenException, InterruptedException {
		CircuitBreaker<String, String> cb = new CircuitBreaker<>(5, 10);
		for (int i = 0; i < 5; i++) {
			cb.execute((str) -> {
				throw new RuntimeException();
			}, "hello");
		}
		Thread.sleep(10000);
		cb.execute((str) -> {
			return str;
		}, "hello");
	}
	
	@Test
	public void canExecuteAfterTimeout2() throws CircuitBreakerOpenException, InterruptedException {
		CircuitBreaker<String, String> cb = new CircuitBreaker<>(5, 10);
		try {
			for (int i = 0; i < 6; i++) {
				cb.execute((str) -> {
					throw new RuntimeException();
				}, "hello");
			}
		} catch (Exception e) {
			System.out.println("hi");
		}
		Thread.sleep(10000);
		cb.execute((str) -> {
			return str;
		}, "hello");
	}
}
