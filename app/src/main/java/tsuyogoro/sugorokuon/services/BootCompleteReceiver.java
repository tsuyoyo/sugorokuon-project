/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 端末が起動した時に、Timerの張りなおしを試みるクラス。
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // TimeTable関連のタイマーを仕掛ける
            Intent updateTimerIntent = new Intent(TimeTableService.ACTION_UPDATE_TIMER);
            updateTimerIntent.setPackage(context.getPackageName());
            context.startService(updateTimerIntent);

            // OnAir曲取得のタイマー
            Intent onAirFetchIntent = new Intent(OnAirSongsService.ACTION_FETCH_ON_AIR_SONGS);
            onAirFetchIntent.setPackage(context.getPackageName());
            context.startService(onAirFetchIntent);
        }
    }

}