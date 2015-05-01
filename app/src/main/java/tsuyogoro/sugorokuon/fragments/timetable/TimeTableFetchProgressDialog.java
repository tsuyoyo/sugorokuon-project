/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.services.TimeTableService;

/**
 * Loading中のprogressを示すdialogのクラス。
 *
 */
public class TimeTableFetchProgressDialog extends DialogFragment {

    private final String KEY_VALUE_PROGRESS = "key_progress";

    private final String KEY_VALUE_MAX = "key_max";

    private ProgressDialog mProgDialog;

    private BroadcastReceiver mBroadCastReceiver;

    private TimeTableService.TimeTableServiceBinder mTimeTableService;

    private ServiceConnection mTimeTableServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTimeTableService = (TimeTableService.TimeTableServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * Dialogが閉じる時に通知を受けるlistener
     */
    public interface IListener {
        public void onDismissFetchProgress(boolean completed);
    }

    public TimeTableFetchProgressDialog() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

        Intent intent = new Intent(getActivity(), TimeTableService.class);
        getActivity().bindService(intent, mTimeTableServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mTimeTableServiceConnection);
    }


    @Override
    public void onResume() {
        super.onResume();
        registerBroadCastReceiver();

        // ダイアログが延々と出てしまう事象に対しての対応策
        if (null != mTimeTableService) {
            if (!mTimeTableService.runningWeeklyUpdate()
                    && !mTimeTableService.runningTodaysUpdate()) {
                dismiss();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadCastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != mProgDialog) {
            outState.putInt(KEY_VALUE_PROGRESS, mProgDialog.getProgress());
            outState.putInt(KEY_VALUE_MAX, mProgDialog.getMax());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mProgDialog = new ProgressDialog(getActivity());
        mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if(null != savedInstanceState) {
            int max  = savedInstanceState.getInt(KEY_VALUE_MAX, 100);
            int progress = savedInstanceState.getInt(KEY_VALUE_PROGRESS, 0);

            mProgDialog.setMax(max);
            mProgDialog.setProgress(progress);

            // 「~局中~局読み込み完了」の文字を設定。
            String progStr = getActivity().getString(R.string.data_loading_progress);
            mProgDialog.setMessage(String.format(progStr, max, progress));
        } else {
            mProgDialog.setMessage(getActivity().getString(R.string.data_loading));
            mProgDialog.setProgress(0);
            mProgDialog.setMax(10);
        }
        return mProgDialog;
    }

    private void registerBroadCastReceiver() {
        IntentFilter progressFilter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_WEEKLY_FETCH_PROGRESS);

        IntentFilter filter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED);

        IntentFilter errorFilter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED);

        mBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED.equals(action)) {
                    dismiss();
                    ((IListener) getActivity()).onDismissFetchProgress(true);
                }
                else if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED.equals(action)) {
                    dismiss();
                    ((IListener) getActivity()).onDismissFetchProgress(false);
                }
                else if (TimeTableService.NotifyAction.ACTION_NOTIFY_WEEKLY_FETCH_PROGRESS.equals(action)) {
                    Bundle extras = intent.getExtras();

                    int total = extras.getInt(
                            TimeTableService.NotifyAction.EXTRA_WEEKLY_FETCH_PROGRESS_TOTAL);
                    int progress = extras.getInt(
                            TimeTableService.NotifyAction.EXTRA_WEEKLY_FETCH_PROGRESS_FETCHED);

                    mProgDialog.setMax(total);
                    mProgDialog.setProgress(progress);

                    String progStr = getActivity().getString(R.string.data_loading_progress);
                    mProgDialog.setMessage(String.format(progStr, total, progress));
                }
            }
        };
        getActivity().registerReceiver(mBroadCastReceiver, progressFilter);
        getActivity().registerReceiver(mBroadCastReceiver, filter);
        getActivity().registerReceiver(mBroadCastReceiver, errorFilter);
    }

}