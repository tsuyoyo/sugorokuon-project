/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings;

import java.util.Calendar;
import java.util.Locale;

import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.constant.Region;
import tsuyogoro.sugorokuon.settings.preference.AreaSettingPreference;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * �O��Update�����������Ǘ����A���ɂ���update���ׂ����H�������Ă����N���X�B
 * 
 * @author Tsuyoyo
 */
public class UpdatedDateManager 
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static UpdatedDateManager sInstance;
	
	private final String PREF_KEY = "pref_key_last_update";
	
	private Context mContext;
	
	private UpdatedDateManager(Context context) {
		mContext = context;
		
        // �ݒ�l�ύX�̒ʒm���󂯎�邽�߂�register
        PreferenceManager.getDefaultSharedPreferences(mContext)
        	.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO : Area�ݒ聄Area����ɂ��遄settings��Area�����遄update��������Ȃ�
		// �Ƃ����o�O���������̂ŗv���ӁB�Č�����Ȃ璼���B
		
		// Area�ݒ�̕ύX��������ŏI�X�V�������N���A�B����Main�ɖ߂����Ƃ���update�𑣂��B
		for(Area a : Area.values()) {
			if(key.equals(AreaSettingPreference.getAreaPreferenceKey(a))) {
				clearLastUpdate();
			}
		}
		for(Region r : Region.values()) {
			if(key.equals(AreaSettingPreference.getRegionPreferenceKey(r))) {
				clearLastUpdate();				
			}
		}
	}
	
	/**
	 * UpdatedDataManager�C���X�^���X���擾�B
	 * �C���X�^���X�����̍ہAPreference�̕ύX���󂯎�邽�߂�listener�o�^������B
	 * 
	 * @param context
	 * @return
	 */
	public static UpdatedDateManager getInstance(Context context) {
		if(null == sInstance) {
			sInstance = new UpdatedDateManager(context);
		}
		return sInstance;
	}
	
	/**
	 * LastUpdate�̓������X�V�B
	 * 
	 */
	public void updateLastUpdate() {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putLong(PREF_KEY, Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
		editor.commit();
	}
	
	/**
	 * LastUpdate�̓������N���A�B
	 * 
	 */
	public void clearLastUpdate() {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putLong(PREF_KEY, -1);
		editor.commit();
	}
	
	/**
	 * ��update�iweb����f�[�^��download�j���ׂ����ǂ����𒲂ׂ�B
	 * �O��update����������preference�Ɋi�[����Ă���̂ŁA������now�Ɣ�ׂĔ���B
	 * 
	 * @param now ��update���ׂ��������H�́u���v�B
	 * @return 
	 */
	public boolean shouldUpdate(Calendar now) {
		// -1�ɂȂ�Ƃ������Ƃ́A�܂���x���T�[�o����f�[�^���擾���Ă��Ȃ��B
		long lastUpdated = getLastUpdatedByMilSec();
		if(0 > lastUpdated) {
			return true;
		}
		
		// �O��update�������Ԃ���A����update��������ׂ��������v�Z���A
		// ���ݎ�������������悾������Aupdate��������B
		long nextUpdate = calculateNextUpdateTime(lastUpdated);
		if(now.getTimeInMillis() > nextUpdate) {
			return true;
		}
		return false;
	}
	
	/**
	 * �Ō�ɍX�V�����������擾�B
	 * �܂���x���X�V�������Ƃ��Ȃ�������-1���Ԃ�B
	 * 
	 * @return
	 */
	public long getLastUpdatedByMilSec() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		return pref.getLong(PREF_KEY, -1);
	}
	
	/**
	 * �Ō�ɍX�V�����������擾�B
	 * �܂���x���X�V�������Ƃ��Ȃ�������null���Ԃ�B
	 * 
	 * @return
	 */
	public Calendar getLastUpdatedByCalendar() {
		long lastUpdatedTime = getLastUpdatedByMilSec();
		if(0 > lastUpdatedTime) {
			return null;
		}
		Calendar c = Calendar.getInstance(Locale.JAPAN);
		c.setTimeInMillis(lastUpdatedTime);
		return c;
	}
	
	/**
	 * ���ɔԑg�\���X�V���ׂ����Ԃ��v�Z����B
	 * ��{�I�ɁA����I�Ȕԑg�\�̍X�V�́A���T���j���̑����Ƃ���B
	 *
	 * @param now ���̎������~��sec�ŁB
	 * @return
	 */
	public long calculateNextUpdateTime(long now) {
		Calendar c = Calendar.getInstance(Locale.JAPAN);
		c.setTimeInMillis(now);
		return calculateNextUpdateTime(c);
	}
	
	/**
	 * ���ɔԑg�\���X�V���ׂ����Ԃ��v�Z����B
	 * ��{�I�ɁA����I�Ȕԑg�\�̍X�V�́A���T���j���̑����Ƃ���B
	 * 
	 * @param now ���̎�����Calendar�C���X�^���X�ŁB
	 * @return
	 */
	public long calculateNextUpdateTime(Calendar now) {
		
		// ���j���ŁA5��10�����O�ɍX�V���������ꍇ�́A���̒����5��10�����X�V�^�C���B
		if(Calendar.MONDAY == now.get(Calendar.DAY_OF_WEEK)) {
			if((4 > now.get(Calendar.HOUR_OF_DAY)) 
					|| (4 == now.get(Calendar.HOUR_OF_DAY) && 50 < now.get(Calendar.MINUTE))) {
				now.set(Calendar.HOUR_OF_DAY, 5);
				now.set(Calendar.MINUTE, 10);
				return now.getTimeInMillis();
			}
		}
		
		// ��L�ɓ��Ă͂܂�Ȃ�������A���̌��j�̑���5��10���B
		do {
			now.add(Calendar.DATE, 1);
		} while(Calendar.MONDAY != now.get(Calendar.DAY_OF_WEEK));
		now.set(Calendar.HOUR_OF_DAY, 5);
		now.set(Calendar.MINUTE, 10);

		return now.getTimeInMillis();
	}
	
}
