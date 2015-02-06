/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Loading中のprogressを示すdialogのクラス。
 *
 * @author Tsuyoyo
 */
public class LoadingProgressDialogFragment extends DialogFragment
        implements IViewFlowListener {

    private final String KEY_VALUE_PROGRESS = "key_progress";
    private final String KEY_VALUE_MAX = "key_max";

    private ProgressDialog mProgDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
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
            int prog = savedInstanceState.getInt(KEY_VALUE_PROGRESS, 0);
            onProgress(DataViewFlow.PROGRESS_LOAD_DATA, prog, max);
        } else {
            mProgDialog.setMessage(getActivity().getString(R.string.data_loading));
            mProgDialog.setProgress(0);
            mProgDialog.setMax(10);
        }
        return mProgDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 一度裏に回って表に帰ってきた時、loadingが既に終わっていたら、dialogを閉じる。
        if(DataViewFlow.getInstance().isUpdating()) {
            DataViewFlow.getInstance().register(this);
        } else {
            dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DataViewFlow.getInstance().unregister(this);
    }

    @Override
    public void onViewFlowEvent(ViewFlowEvent event) {
        if(ViewFlowEvent.COMPLETE_DATA_UPDATECOMPLETE.equals(event)
                || ViewFlowEvent.FAILED_DATA_UPDATE.equals(event)
                || ViewFlowEvent.FAILED_STATION_UPDATE.equals(event)) {
            dismiss();
        }
    }

    @Override
    public void onProgress(int whatsRunning, int progress, int max) {
        if(whatsRunning == DataViewFlow.PROGRESS_LOAD_DATA) {
            mProgDialog.setMax(max);
            mProgDialog.setProgress(progress);

            // 「~局中~局読み込み完了」の文字を設定。
            String progStr = getActivity().getString(R.string.data_loading_progress);
            mProgDialog.setMessage(String.format(progStr, max, progress));
        }
    }

}