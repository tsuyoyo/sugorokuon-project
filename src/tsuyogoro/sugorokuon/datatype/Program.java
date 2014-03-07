/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.datatype;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 1番組分のデータ。
 * @author Tsuyoyo
 *
 */
public class Program {

	public final String stationId;
	public final String start;
	public final String end;
	public final String title;
	public final String subtitle;
	public final String personalities;
	public final String description;
	public final String info;
	public final String url;
	
	public List<String> recommendKeyword;
	
	private Program(String _stationId, String _start, String _end, 
			String _title, String _subtitle, String _personalities, 
			String _desc, String _info, String _url) {
		stationId 		= _stationId;
		start 			= _start;
		end   			= _end;
		title 			= _title;
		subtitle 		= _subtitle;
		personalities 	= _personalities;
		description 	= _desc;
		info 			= _info;
		url 			= _url;
		
		recommendKeyword = new ArrayList<String>();
	}
	
	public static class Builder {
		private Program mInstance;

		public String stationId;
		public String start;
		public String end;
		public String title;
		public String subtitle;
		public String personalities;
		public String description;
		public String info;
		public String url;
		
		public Builder() {
		}
		
		public Program create() {
			mInstance = new Program(stationId, start, end, title, 
					subtitle, personalities, description, info, url);
			return mInstance;
		}
		
	}

	/**
	 * 時間順にソートする際に使う。 Collections.sort()に渡す。
	 *
	 */
	public static class ProgramComparator implements Comparator<Program> {
		public int compare(Program s, Program t) {
			long sStart = Long.valueOf(s.start);
			long tStart = Long.valueOf(t.start);

			if(sStart > tStart) {
				return 1;
			}
			else if(sStart == tStart) {
				return 0;
			}
			else {
				return -1;
			}
		}
	}
}
