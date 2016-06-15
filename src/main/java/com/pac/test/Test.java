package com.pac.test;

import java.time.Clock;


public class Test implements Runnable {
	long period = Clock.systemUTC().millis();//计时用
	
	
	public static void main(String[] args) throws Exception {
		
		new Thread(new Test()).start();
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			test();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void test() {

		long periodTemp = System.currentTimeMillis();
		System.out.println(periodTemp-period);
		if (periodTemp-period<1000) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		period = periodTemp;
	
	}
}
