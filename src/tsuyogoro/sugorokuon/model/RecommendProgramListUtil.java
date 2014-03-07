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
 * Recommend�ԑgList�ɑ΂��Ďg����Utility�N���X�B
 * 
 */
public class RecommendProgramListUtil {

	/**
	 * TimeTable�̃��X�g����A���܂����L�[���[�h�̃��X�g�����ɁA�I�X�X���ԑg��filtering����B
	 * �߂�l�̓I�X�X��Program�̃��X�g�ŁA���ԏ��Ƀ\�[�g����A���A���ɏI����Ă���ԑg�͎�菜����Ă���B
	 * 
	 * @param input 
	 */
	public static List<Program> createRecommendProgramList(
			List<OnedayTimetable> weeklyTimeTable, List<String> keyWords) {
		List<Program> progs = new ArrayList<Program>();
		
		// �I�X�X���̔ԑg�ɂ��ڂ�
		for(OnedayTimetable timeTable : weeklyTimeTable) {
			progs.addAll(filterRecommendPrograms(timeTable, keyWords));
		}
		
		// ���ԏ��̃\�[�g�B
		RecommendProgramListUtil.sortProgramsTimeOrder(progs);
		
		// �I������ԑg�̏����B
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
	 * Recommend�ԑg���X�g���AonAir�̎��ԏ��Ƀ\�[�g����B
	 * 
	 * @param input 
	 */
	public static void sortProgramsTimeOrder(List<Program> input) {
		// ���ԏ��Ƀ\�[�g�B
		Collections.sort(input, new Program.ProgramComparator());		
	}
		

	/**
	 * ���ɕ������I���Ă��܂����ԑg��list��������B
	 * 
	 * @param input
	 */
	public static void removePastPrograms(List<Program> recommends) {
		long current = Calendar.getInstance(Locale.JAPAN).getTimeInMillis();
		for(int i=0; i < recommends.size(); i++) {
			Program p = recommends.get(i);
			Calendar start_c = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
			long start = start_c.getTimeInMillis();
			
			// ���ݎ��������O�̔ԑg�i�I����Ă��܂����ԑg�j�́ARecommend���X�g��������B
			if(current < start) {
				for(int j=0; j<i; j++) {
					// �擪������������̂�0�B
					recommends.remove(0);
				}
				return;
			}			
		}
		// �S����current > start��������A�S�Ă̔ԑg����菜��
		recommends.clear();
	}
	
}
