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
 * Util関数をここにまとめる。
 *
 */
public class SugorokuonUtils {

	/**
	 * radiko.jpから振ってくるOn Air timeのフォーマットをCalendarインスタンスにする。
	 * 例） <prog ft="20121022050000" to="20121022080000" ftl="0500" tol="0800" dur="10800">
	 * これのftとto。
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
	 * 画面が横かどうかを取得。
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
	 * PixelからDPを計算。
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static float calculateDpfromPx(Context context, float px) {
		return px / context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * 実行環境がGingerBreadよりも新しいかどうかを調べる。
	 * 
	 * @return
	 */
	public static boolean isHigherThanGingerBread() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;
	}
	
	/**
	 * 実行環境がHoneyCombよりも新しいかどうかを調べる。
	 * 
	 * @return
	 */
	public static boolean isHigherThanHoneyComb() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2;
	}

}
