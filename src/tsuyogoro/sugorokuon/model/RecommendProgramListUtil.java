/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.datatype.OnedayTimetable;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;

/**
 * Recommend番組Listに対して使えるUtilityクラス。
 * 
 */
public class RecommendProgramListUtil {

	/**
	 * TimeTableのリストから、おまかせキーワードのリストを元に、オススメ番組をfilteringする。
	 * 戻り値はオススメProgramのリストで、時間順にソートされ、かつ、既に終わっている番組は取り除かれている。
	 * 
	 * @param input 
	 */
	public static List<Program> createRecommendProgramList(
			List<OnedayTimetable> weeklyTimeTable, List<String> keyWords) {
		List<Program> progs = new ArrayList<Program>();
		
		// オススメの番組にしぼる
		for(OnedayTimetable timeTable : weeklyTimeTable) {
			progs.addAll(filterRecommendPrograms(timeTable, keyWords));
		}
		
		// 時間順のソート。
		RecommendProgramListUtil.sortProgramsTimeOrder(progs);
		
		// 終わった番組の除去。
		RecommendProgramListUtil.removePastPrograms(progs);
		
		return progs;
	}
	
	private static List<Program> filterRecommendPrograms(
			OnedayTimetable timeTable, List<String> keyWords) {
		List<Program> recommends = new ArrayList<Program>();
		RecommendProgramFilter filter = new RecommendProgramFilter(keyWords);
		for(Program p : timeTable.programs) {
			if(filter.isRecommend(p)) {
				recommends.add(p);
			}
		}
		return recommends;
	}
	
	/**
	 * Recommend番組リストを、onAirの時間順にソートする。
	 * 
	 * @param input 
	 */
	public static void sortProgramsTimeOrder(List<Program> input) {
		// 時間順にソート。
		Collections.sort(input, new Program.ProgramComparator());		
	}
		

	/**
	 * 既に放送し終えてしまった番組をlistから消す。
	 * 
	 * @param input
	 */
	public static void removePastPrograms(List<Program> recommends) {
		long current = Calendar.getInstance(Locale.JAPAN).getTimeInMillis();
		for(int i=0; i < recommends.size(); i++) {
			Program p = recommends.get(i);
			Calendar start_c = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
			long start = start_c.getTimeInMillis();
			
			// 現在時刻よりも前の番組（終わってしまった番組）は、Recommendリストから消す。
			if(current < start) {
				for(int j=0; j<i; j++) {
					// 先頭を消し続けるので0。
					recommends.remove(0);
				}
				return;
			}			
		}
		// 全件がcurrent > startだったら、全ての番組を取り除く
		recommends.clear();
	}
	
}
