/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * {@link SugorokuonService}�N���X���A
 * ���W�I�A�v���N����Action({@link SugorokuonService#ACTION_LAUNCH_RADIO_APP})
 * ���󂯎�����ۂɏ������s���N���X�B
 * 
 * @author Tsuyoyo
 */
class ActionProcessorLaunchRadioApp {
	
	private static final String LAUNCH_URL = "radiko://radiko.jp";
	
	private static final String GOOGLE_PLAY_URL = "market://details?id=jp.radiko.Player";
	
	/**
	 * ���W�I�A�v���̋N�����s���B
	 * radiko�A�v����install����Ă��Ȃ�������market�֗U���B
	 * 
	 * @param context
	 */
	public void launchRadioApp(Context context) {
		// �A�v���̋N�������݂�B
		Uri uri = Uri.parse(LAUNCH_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch(Exception e) {
			// �_����������}�[�P�b�g��install�y�[�W�ցB
			Uri googleplayuri = Uri.parse(GOOGLE_PLAY_URL); 
			Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW,  googleplayuri);
			googlePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(googlePlayIntent);
		}
	}
}
