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
 */
public class DeviceEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        // AlarmManagerをcancelさせうるイベントが発生したら自ら設定し直す
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) ||
                action.equals(Intent.ACTION_MY_PACKAGE_REPLACED) ||
                action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                action.equals(Intent.ACTION_DATE_CHANGED)) {
            setTimers(context);
        }
        // API level 11以下だと、アプリのupdate時にこのイベントが飛んでくる
        else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            if (intent.getDataString().equals("package:" + context.getPackageName())) {
                setTimers(context);
            }
        }

    }

    private void setTimers(Context context) {
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