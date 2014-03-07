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
 * MobileGoogleAnalytics�̂��߂�Utility�N���X�B
 * https://developers.google.com/analytics/devguides/collection/android/v2/
 * 
 * @author Tsuyoyo
 */
public class GATrackingUtil {
	
	/**
	 * {@link Build#MODEL}��{@link Build#PRODUCT}�Ŏ���l��Ԃ��B
	 * ���ꂼ��null�̏ꍇ�����肤��̂ŁA���̏ꍇ�͋󕶎��ɒu�������B
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
	 * ���ݎ��Ԃ��A02��15���i���j 14:47  �Ƃ������t�H�[�}�b�g�Ŏ擾�B
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
