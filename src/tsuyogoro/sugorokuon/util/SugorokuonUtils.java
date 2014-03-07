/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.util;

import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

/**
 * Util�֐��������ɂ܂Ƃ߂�B
 *
 */
public class SugorokuonUtils {

	/**
	 * radiko.jp����U���Ă���On Air time�̃t�H�[�}�b�g��Calendar�C���X�^���X�ɂ���B
	 * ��j <prog ft="20121022050000" to="20121022080000" ftl="0500" tol="0800" dur="10800">
	 * �����ft��to�B
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar changeOnAirTimeToCalendar(String input) {
		int year  = Integer.valueOf(input.substring( 0, 4));
		int month = Integer.valueOf(input.substring( 4, 6));
		int day   = Integer.valueOf(input.substring( 6, 8));
		int hour  = Integer.valueOf(input.substring( 8, 10));
		int min   = Integer.valueOf(input.substring(10, 12));
		
		Calendar c = Calendar.getInstance(Locale.JAPAN);
		c.set(year, month-1, day, hour, min, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
	
	/**
	 * ��ʂ������ǂ������擾�B
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isLandscape(Context context) {
		Resources resources = context.getResources();
		return (Configuration.ORIENTATION_LANDSCAPE 
				== resources.getConfiguration().orientation);
	}

	/**
	 * Pixel����DP���v�Z�B
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static float calculateDpfromPx(Context context, float px) {
		return px / context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * ���s����GingerBread�����V�������ǂ����𒲂ׂ�B
	 * 
	 * @return
	 */
	public static boolean isHigherThanGingerBread() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;
	}
	
	/**
	 * ���s����HoneyComb�����V�������ǂ����𒲂ׂ�B
	 * 
	 * @return
	 */
	public static boolean isHigherThanHoneyComb() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2;
	}

}
