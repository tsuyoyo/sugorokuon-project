/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 次の自動番組更新時間を表示するためのdialog。
 *
 * @author Tsuyoyo
 *
 */
public class UpdateInfoDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getLastUpdateTimeStr() + "\n\n" + getNextUpdateTimeStr();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.update_status_dialog_title));
        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.ok), null);
        builder.setCancelable(false);

        return builder.create();
    }

    private String getNextUpdateTimeStr() {
        String nextUpdateDate =
                getActivity().getString(R.string.update_status_dialog_next_update);
        SimpleDateFormat sdf = new SimpleDateFormat(nextUpdateDate, Locale.JAPANESE);

        UpdatedDateManager dateMgr = UpdatedDateManager.getInstance(getActivity());
        long nextUpdateTime = dateMgr.calculateNextUpdateTime(Calendar.getInstance(Locale.JAPAN));
        return sdf.format(new Date(nextUpdateTime));
    }

    private String getLastUpdateTimeStr() {
        String nextUpdateDate =
                getActivity().getString(R.string.update_status_dialog_last_update);
        SimpleDateFormat sdf = new SimpleDateFormat(nextUpdateDate, Locale.JAPANESE);

        UpdatedDateManager dateMgr = UpdatedDateManager.getInstance(getActivity());
        long lastUpdateTime = dateMgr.getLastUpdatedByMilSec();
        return sdf.format(new Date(lastUpdateTime));
    }

}