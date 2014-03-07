/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.constant.Region;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * Area設定に関するPreference。
 * 
 * @author Tsuyoyo
 *
 */
public class AreaSettingPreference {
	
	public static final String PREF_KEY_AREAS = "pref_key_target_area_%d";
	
	public static final String PREF_KEY_REGIONS = "pref_key_target_region_%d";
	
	/**
	 * targetScreenに、Area設定のPreferenceを追加する。
	 * 
	 * @param targetScreen
	 * @return
	 */
	public static Preference[] addPreferenceTo(final PreferenceScreen targetScreen) {
		Context context = targetScreen.getContext();

		// Region単位で設定、のpreference category。
		PreferenceCategory regionCategory = new PreferenceCategory(context);
		regionCategory.setTitle(context.getString(R.string.settings_header_region_category));
		targetScreen.addPreference(regionCategory);
		
		for(Region region : Region.values()) {
			CheckBoxPreference pref = new CheckBoxPreference(targetScreen.getContext());
			pref.setKey(getRegionPreferenceKey(region));
			pref.setTitle(targetScreen.getContext().getString(region.strId));
			targetScreen.addPreference(pref);
		}

		// 県単位で設定、のpreference category。
		PreferenceCategory areaCategory = new PreferenceCategory(context);
		areaCategory.setTitle(context.getString(R.string.settings_header_prefecture_category));
		targetScreen.addPreference(areaCategory);
		
		for(Area area : Area.values()) {
			CheckBoxPreference pref = new CheckBoxPreference(targetScreen.getContext());
			pref.setKey(getAreaPreferenceKey(area));
			pref.setTitle(targetScreen.getContext().getString(area.strId));
			targetScreen.addPreference(pref);
		}
		
		Preference options[] = new Preference[Area.values().length];
		return options;
	}
		
	/**
	 * Preferenceに保存されているTarget areaを取得。
	 * 
	 * @param context
	 * @return Targetに設定されているのAreaの定数のlist。
	 */
	public static List<Area> getTargetAreas(Context context) {
		
		SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		// まずは県単位で設定を見る。
		List<Area> targets = new ArrayList<Area>();
		for(Area area : Area.values()) {
			boolean isTarget = defPref.getBoolean(getAreaPreferenceKey(area), false);
			if(isTarget) {
				targets.add(area);
			}
		}
		
		// 次に地域単位でチェック。
		for(Region region : Region.values()) {
			boolean isTarget = defPref.getBoolean(getRegionPreferenceKey(region), false);
			if(isTarget) {
				for(Area area : region.areas) {
					targets.add(area);
				}
			}
		}
		
		return targets;		
	}
	
	/**
	 * 
	 * @param area
	 * @return
	 */
	public static String getAreaPreferenceKey(Area area) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(PREF_KEY_AREAS, area.ordinal());
		formatter.close();
		return sb.toString();	
	}
	
	public static String getRegionPreferenceKey(Region region) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(PREF_KEY_REGIONS, region.ordinal());
		formatter.close();
		return sb.toString();			
	}

}
