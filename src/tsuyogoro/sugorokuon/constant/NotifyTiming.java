/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.constant;

import java.util.Calendar;

import tsuyogoro.sugorokuon.R;

/**
 * 番組が始まるどのくらい前に通知を行うか？を定義したenumクラス。
 *
 * @author Tsuyoyo
 * 
 */
public enum NotifyTiming {
	/** 通知しない */
	NOT_SET (R.string.settings_remindtiming_not_set),
	/** 10分前 */
	BEFORE_10_MIN (R.string.settings_remindtiming_ten_min_before),
	/** 30分前　*/
	BEFORE_30_MIN (R.string.settings_remindtiming_thirty_min_before),
	/** 1時間前　*/
	BEFORE_1_HOUR (R.string.settings_remindtiming_one_hour_before),
	/** 2時間前　*/
	BEFORE_2_HOUR (R.string.settings_remindtiming_two_hour_before),
	/** 5時間前　*/
	BEFORE_5_HOUR (R.string.settings_remindtiming_five_hour_before),	
	;
	
	/**
	 * 各値に対して、画面に表示するためのString ID。
	 * 
	 */
	public int optionStrId;
	
	private NotifyTiming(int _optionStrId) {
		optionStrId = _optionStrId;
	}
	
	/**
	 * 通知すべき時間を計算してくれるmethod。
	 * 例えば、BEFORE_30_MINに対してこのmethodを呼ぶと、
	 * inputの30分前のCalendarインスタンスを作って返してくれる。
	 * 
	 * @param input 計算したい時刻。
	 * @return valueに応じて、notifyすべき時間を返却。
	 */
	public Calendar calculateNotifyTime(Calendar input) {
		Calendar res = null;
		switch(this) {
		case NOT_SET:
			input = null;
			break;
		case BEFORE_10_MIN:
			input.add(Calendar.MINUTE, -10);
			break;
		case BEFORE_30_MIN:
			input.add(Calendar.MINUTE, -30);
			break;
		case BEFORE_1_HOUR:
			input.add(Calendar.HOUR_OF_DAY, -1);
			break;
		case BEFORE_2_HOUR:
			input.add(Calendar.HOUR_OF_DAY, -2);
			break;
		case BEFORE_5_HOUR:
			input.add(Calendar.HOUR_OF_DAY, -5);
			break;
		}
		res = input;
		return res;
	}
}
