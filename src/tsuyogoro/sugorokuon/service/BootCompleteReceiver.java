/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * �[�����N���������ɁATimer�̒���Ȃ��������݂�N���X�B
 * 
 * @author Tsuyoyo
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
    		context.startService(new Intent(SugorokuonService.ACTION_UPDATE_TIMER));
    	}
    }

}
