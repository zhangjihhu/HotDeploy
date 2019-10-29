package com.hhu;

public class Target {

	public static void main(String[] args) throws InterruptedException {
		while (true) {
			new Test().test();
			Thread.sleep(3000);
		}
	}

}
