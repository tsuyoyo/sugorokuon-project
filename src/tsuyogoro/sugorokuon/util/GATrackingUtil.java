/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import android.content.Context;
import android.os.Build;

/**
 * MobileGoogleAnalyticsのためのUtilityクラス。
 * https://developers.google.com/analytics/devguides/collection/android/v2/
 * 
 * @author Tsuyoyo
 */
public class GATrackingUtil {
	
	/**
	 * {@link Build#MODEL}と{@link Build#PRODUCT}で取れる値を返す。
	 * それぞれnullの場合がありうるので、その場合は空文字に置き換え。
	 * 
	 * @return
	 */
	static public String getModelAndProductName() {
		String model = Build.MODEL;
		String product = Build.PRODUCT;
		
		String res = "";
		res += (null != model) ? model : "";
		res += " (";
		res += (null != product) ? product : "";
		res += ")";
		
		return res;
	}
	
	/**
	 * 現在時間を、02月15日（水） 14:47  といったフォーマットで取得。
	 * @param context
	 * @return
	 */
	static public String getCurrentTime(Context context) {
		String strTo = context.getString(R.string.date_mmddeeehhmm);
		SimpleDateFormat sdfTo = new SimpleDateFormat(strTo, Locale.JAPANESE);
		Calendar now = Calendar.getInstance(Locale.JAPAN);
		
		return sdfTo.format(new Date(now.getTimeInMillis()));
	}
}
