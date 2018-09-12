package com.cnautosoft.silver.client;

public class Sample {
	public static void main(String[] args) {
		PropertiesManager pm = PropertiesManager.getInstance();
		pm.init("localhost",9091,"datagroup","datakey");
		
		Thread t = new Thread() {
			public void run() {
				while(true) {
					System.out.println(pm.getProperty("config1"));
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		t.start();
		
		
		
		
	}
}
