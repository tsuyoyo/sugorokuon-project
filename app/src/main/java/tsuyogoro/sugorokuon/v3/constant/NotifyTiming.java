/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.v3.constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.models.entities.Program;

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
	/** 30分前 */
	BEFORE_30_MIN (R.string.settings_remindtiming_thirty_min_before),
	/** 1時間前 */
	BEFORE_1_HOUR (R.string.settings_remindtiming_one_hour_before),
	/** 2時間前 */
	BEFORE_2_HOUR (R.string.settings_remindtiming_two_hour_before),
	/** 5時間前 */
	BEFORE_5_HOUR (R.string.settings_remindtiming_five_hour_before),	
	;
	
	/**
	 * 各値に対して、画面に表示するためのString ID
	 * 
	 */
	public int optionStrId;
	
	NotifyTiming(int _optionStrId) {
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
	private Calendar calculateNotifyTime(Calendar input) {
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

    /**
     *
     *
     * @param input
     * @return valueに応じて、notifyすべき時間を返却 (通知の必要がない場合はnull)
     */
    public Calendar calculateNotifyTime(List<Program> input) {

        List<Program> programs = new ArrayList<Program>(input);

        if (this.equals(NOT_SET)) {
            return null;
        }

        Collections.sort(input, new Comparator<Program>() {
            @Override
            public int compare(Program lhs, Program rhs) {
                return lhs.startTime.compareTo(rhs.startTime);
            }
        });

        // 今から通知時間後までに放送を開始しちゃう番組をフィルタ
        // (例えば、設定が30分で、今から30分以内に番組を開始しちゃう)
        Calendar justAfterSetTime = Calendar.getInstance(Locale.JAPAN);
        switch(this) {
            case BEFORE_10_MIN:
                justAfterSetTime.add(Calendar.MINUTE, 10);
                break;
            case BEFORE_30_MIN:
                justAfterSetTime.add(Calendar.MINUTE, 30);
                break;
            case BEFORE_1_HOUR:
                justAfterSetTime.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case BEFORE_2_HOUR:
                justAfterSetTime.add(Calendar.HOUR_OF_DAY, 2);
                break;
            case BEFORE_5_HOUR:
                justAfterSetTime.add(Calendar.HOUR_OF_DAY, 5);
                break;
        }
        while (!programs.isEmpty() &&
                0 > programs.get(0).startTime.compareTo(justAfterSetTime)) {
            programs.remove(0);
        }

        if (!programs.isEmpty()) {
            return calculateNotifyTime(programs.get(0).startTime);
        } else {
            return null;
        }
    }
}
