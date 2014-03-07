/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.constant;

import java.util.Calendar;

import tsuyogoro.sugorokuon.R;

/**
 * �ԑg���n�܂�ǂ̂��炢�O�ɒʒm���s�����H���`����enum�N���X�B
 *
 * @author Tsuyoyo
 * 
 */
public enum NotifyTiming {
	/** �ʒm���Ȃ� */
	NOT_SET (R.string.settings_remindtiming_not_set),
	/** 10���O */
	BEFORE_10_MIN (R.string.settings_remindtiming_ten_min_before),
	/** 30���O�@*/
	BEFORE_30_MIN (R.string.settings_remindtiming_thirty_min_before),
	/** 1���ԑO�@*/
	BEFORE_1_HOUR (R.string.settings_remindtiming_one_hour_before),
	/** 2���ԑO�@*/
	BEFORE_2_HOUR (R.string.settings_remindtiming_two_hour_before),
	/** 5���ԑO�@*/
	BEFORE_5_HOUR (R.string.settings_remindtiming_five_hour_before),	
	;
	
	/**
	 * �e�l�ɑ΂��āA��ʂɕ\�����邽�߂�String ID�B
	 * 
	 */
	public int optionStrId;
	
	private NotifyTiming(int _optionStrId) {
		optionStrId = _optionStrId;
	}
	
	/**
	 * �ʒm���ׂ����Ԃ��v�Z���Ă����method�B
	 * �Ⴆ�΁ABEFORE_30_MIN�ɑ΂��Ă���method���ĂԂƁA
	 * input��30���O��Calendar�C���X�^���X������ĕԂ��Ă����B
	 * 
	 * @param input �v�Z�����������B
	 * @return value�ɉ����āAnotify���ׂ����Ԃ�ԋp�B
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
