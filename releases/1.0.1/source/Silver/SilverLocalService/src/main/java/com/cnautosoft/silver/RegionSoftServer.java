package com.cnautosoft.silver;


import com.cnautosoft.h2o.standalone.StandaloneServer;

public class RegionSoftServer {
	public static void main(String[] args) throws Exception{
		StandaloneServer ss = new StandaloneServer("/Silver");
		ss.runHttp();
	}
}
