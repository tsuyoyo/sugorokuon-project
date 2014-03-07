/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.settings.SugorokuonSettingActivity;
import tsuyogoro.sugorokuon.settings.preference.LaunchedCheckPreference;
import tsuyogoro.sugorokuon.util.GATrackingUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Setting���N�����鎞�Ɏg��dialog�B
 * 
 * @author Tsuyoyo
 */
public class SettingsLauncherDialogFragment extends DialogFragment {

	private static final String ID_TITLE = "id_title";
	private static final String ID_MESSAGE = "id_message";
	private static final String ID_IS_WELCOME = "id_is_welcome";
	
	private boolean mIsWelcome;
	private int mTitle = -1;
	private int mMessage = -1;
	
	public static SettingsLauncherDialogFragment getInstance(boolean isWelcome) {
		SettingsLauncherDialogFragment dialog = new SettingsLauncherDialogFragment();
		dialog.mIsWelcome = isWelcome;
		dialog.setStringIds();
		return dialog;
	}
	
	public SettingsLauncherDialogFragment() {
		super();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ID_TITLE, mTitle);
		outState.putInt(ID_MESSAGE, mMessage);
		outState.putBoolean(ID_IS_WELCOME, mIsWelcome);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null != savedInstanceState) {
			mIsWelcome = savedInstanceState.getBoolean(ID_IS_WELCOME, true);
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		if(-1 == mTitle && -1 == mMessage) {
			mTitle = savedInstanceState.getInt(ID_TITLE);
			mMessage = savedInstanceState.getInt(ID_MESSAGE);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitle);
		builder.setMessage(mMessage);
		builder.setPositiveButton(getActivity().getString(R.string.ok), 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Welcome dialog�Ƃ��Ă��g����̂ŁAflag���Ă��s���B
						LaunchedCheckPreference.setLaunched(getActivity());
						Intent intent = new Intent(getActivity(), SugorokuonSettingActivity.class);
						getActivity().startActivity(intent);
						
						// For mobile google analytics tracking.
						// (Download���ċN�����āA�ݒ�܂ł����Ɛi��ł��ꂽ�l�j						
						if(mIsWelcome) {
							EasyTracker.getTracker().trackEvent(
									getText(R.string.ga_event_category_welcome).toString(),
									getText(R.string.ga_event_action_welcome_tap_ok).toString(), 
									GATrackingUtil.getModelAndProductName(), null);
						}	
					}
				});
		
		return builder.create();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		// �i�����j
		// DialogFragment�̒��ŁADialog��onCancelListener�͐ݒ�ł��Ȃ��iexeption����ԁj�B
		// ����āA�����DialogFragment��onCancel���I�[�o�[���C�h����B
		// (�Q�l�Fhttp://memory.empressia.jp/article/44110106.html�j
		super.onCancel(dialog);
		
		// ����Dialog��
		// �uSetting��ʂŐݒ���s���Ă����Ȃ��Ƃ���ȏ㉽���ł��Ȃ��v�Ƃ������Ɏg����̂ŁA
		// Cancel���ꂽ��A�I�������ւ�������𓾂Ȃ��B
		getActivity().finish();
		
		// For mobile google analytics tracking.
		// (Download���ċN�����Ă��ꂽ���ǂ����Ă���Ȃ������l�j
		if(mIsWelcome) {
			EasyTracker.getTracker().trackEvent(
					getText(R.string.ga_event_category_welcome).toString(),
					getText(R.string.ga_event_action_welcome_cancel).toString(), 
					GATrackingUtil.getModelAndProductName(), null);
		}	
	}

	private void setStringIds() {
		if(mIsWelcome) {
			mTitle = R.string.welcome_dialog_title;
			mMessage = R.string.welcome_dialog_meesage;
		} else {
			mTitle = R.string.no_area_dialog_title;
			mMessage = R.string.no_area_dialog_message;
		}
	}
	
}
