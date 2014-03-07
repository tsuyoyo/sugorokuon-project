/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;

class SugorokuonActivityEventTracker {
	
	/** 
	 * Main screen�Ŕ��������C�x���g��GA�ɑ���B
	 * �C�x���g��category�͋��ʂ��āA
	 * <string name="ga_event_category_main_screen_operation">Main screen operation</string>
	 * ���g���B
	 * 
	 * @param categoryStrId strings_for_ga_tracking.xml�ɒ�`����category��string ID�B
	 * @param context
	 * @param label ����΁B�Ȃ����null�ŗǂ��B
	 */
	static public void submitGAEvent(int categoryStrId, Context context, String label) {
		EasyTracker.getTracker().trackEvent(
				context.getText(R.string.ga_event_category_main_screen_operation).toString(),
				context.getText(categoryStrId).toString(), 
				label, null);
	}
	
}
