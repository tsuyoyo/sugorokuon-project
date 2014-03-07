/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.datatype;

/**
 * 1ラジオ局のデータ
 *
 */
public class Station {
	
	public final String id;
	public final String name;
	public final String ascii_name;
	public final String siteUrl;
	public final String logoUrl;
	public final String bannerUrl;
	
	public String logoCachePath;
	
	private Station(String _id, String _name, String _ascii_name, 
			String _siteUrl, String _logoUrl, String _bannerUrl, String _logoCachePath) {
		id   	   = _id;
		name 	   = _name;
		ascii_name = _ascii_name;
		siteUrl    = _siteUrl;
		logoUrl    = _logoUrl;
		bannerUrl  = _bannerUrl;
		logoCachePath = _logoCachePath;
	}
	
	public static class Builder {
		public String id;
		public String name;
		public String ascii_name;
		public String siteUrl;
		public String logoUrl;
		public String bannerUrl;
		public String logoCache;
		public String logoCachePath;
		
		public Station create() {
			return new Station(id, name, ascii_name, siteUrl, 
					logoUrl, bannerUrl, logoCachePath);
		}
	}
	
}
