package tsuyogoro.sugorokuon.service;

import java.util.Calendar;
import java.util.TimeZone;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

public class USugorokuServiceLogicTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Radiko�̔ԑg�[�������t�H�[�}�b�g(yyyymmddhhmmss)����
	 * ������Calendar�����ˁH�̊m�F�B
	 * 
	 */
	@LargeTest
	public void test_CalendarLogicTes_01() {
		Calendar startData = translateDateFormat("20130525230000");
		
		Calendar currentTime = Calendar.getInstance(TimeZone.getDefault());
		assertTrue(startData.getTimeInMillis() > currentTime.getTimeInMillis());
	}
	
	/**
	 * Radiko�̔ԑg�[�������t�H�[�}�b�g(yyyymmddhhmmss)�ŁA
	 * "26��"�݂����Ȑݒ�����Ă��A������Calendar�����ˁH�̊m�F�B
	 * 
	 */
	@LargeTest
	public void test_CalendarLogicTes_02() {
		// �u2013/1/1 2:00�v �� �u2012/12/31 26:00�v �͓����͂��B
		Calendar testData = translateDateFormat("20130101020000");
		Calendar refData  = translateDateFormat("20121231260000");
		
		// ������ƌ덷���o��݂����Ȃ̂ŁA���̔�r�B
		long threshold = testData.getTimeInMillis() - refData.getTimeInMillis();
		assertTrue(1000 > threshold);
	}
	
	private Calendar translateDateFormat(String input) {
		int year  = Integer.valueOf(input.substring( 0, 4));
		int month = Integer.valueOf(input.substring( 4, 6));
		int day   = Integer.valueOf(input.substring( 6, 8));
		int hour  = Integer.valueOf(input.substring( 8, 10));
		int min   = Integer.valueOf(input.substring(10, 12));
		
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, min, 0);
		return c;
	}
	
}
