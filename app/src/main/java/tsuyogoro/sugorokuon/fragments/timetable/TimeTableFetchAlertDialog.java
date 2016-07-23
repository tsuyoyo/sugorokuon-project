/**
 * Copyright (c)
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import tsuyogoro.sugorokuon.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Activityから使う想定
 *
 */
public class TimeTableFetchAlertDialog extends DialogFragment {

    public interface OnOptionSelectedListener {
        void onTimeTableFetchSelected(boolean updateStation, boolean updateToday);
    }

    public TimeTableFetchAlertDialog() {
    }

    public static TimeTableFetchAlertDialog createWithUpdateOptions() {
        TimeTableFetchAlertDialog dialogFragment = new TimeTableFetchAlertDialog();
        Bundle params = new Bundle();
        params.putInt(KEY_DIALOG_TYPE, TYPE_ASK_HOW_TO_UPDATE);
        dialogFragment.setArguments(params);
        return dialogFragment;
    }

    public static TimeTableFetchAlertDialog createToAskWeeklyUpdate() {
        TimeTableFetchAlertDialog dialogFragment = new TimeTableFetchAlertDialog();
        Bundle params = new Bundle();
        params.putInt(KEY_DIALOG_TYPE, TYPE_ASK_IF_TO_UPDATE_WEEKLY);
        dialogFragment.setArguments(params);
        return dialogFragment;
    }

    private static final int TYPE_ASK_IF_TO_UPDATE = 0;

    private static final int TYPE_ASK_HOW_TO_UPDATE = 1;

    private static final int TYPE_ASK_IF_TO_UPDATE_WEEKLY = 2;

    private static final String KEY_DIALOG_TYPE = "dialog_type";

    public static final String KEY_UPDATE_STATION = "update_station";

    public static final String KEY_UPDATE_TODAY = "update_today";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int type = getArguments().getInt(KEY_DIALOG_TYPE, TYPE_ASK_IF_TO_UPDATE);

        AlertDialog dialog;

        switch (type) {
            case TYPE_ASK_HOW_TO_UPDATE:
                dialog = createAlerDialogWithUpdateOptions();
                break;
            case TYPE_ASK_IF_TO_UPDATE_WEEKLY:
                dialog = createAlertDialogToAskIfToUpdateWeekly();
                break;
            case TYPE_ASK_IF_TO_UPDATE:
            default:
                dialog = createAlertDialogToAskIfToUpdate();
                break;
        }

        return dialog;
    }

    private AlertDialog createAlertDialogToAskIfToUpdate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getActivity().getString(R.string.ask_if_to_update_data_title));
        builder.setMessage(getActivity().getString(R.string.ask_if_to_update_data_message));
        builder.setNegativeButton(getActivity().getString(R.string.no), null);
        builder.setCancelable(false);

        DialogInterface.OnClickListener clickListener = (dialog, which) -> {
            OnOptionSelectedListener listener = (OnOptionSelectedListener) getActivity();
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    listener.onTimeTableFetchSelected(
                            getArguments().getBoolean(KEY_UPDATE_STATION, false),
                            getArguments().getBoolean(KEY_UPDATE_TODAY, false));
                    break;
            }
        };

        builder.setPositiveButton(getActivity().getString(R.string.yes), clickListener);

        return builder.create();
    }

    private AlertDialog createAlerDialogWithUpdateOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        DialogInterface.OnClickListener clickListener = (dialog, which) -> {
            OnOptionSelectedListener listener = (OnOptionSelectedListener) getActivity();
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    listener.onTimeTableFetchSelected(true, false);
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    listener.onTimeTableFetchSelected(false, true);
                    break;
            }
        };

        builder.setTitle(getString(R.string.ask_if_to_update_data_title));
        builder.setMessage(getString(R.string.ask_if_to_update_data_message));
        builder.setPositiveButton(getString(R.string.ask_if_to_update_weekly_message), clickListener);
        builder.setNeutralButton(getString(R.string.ask_if_to_update_today_message), clickListener);
        builder.setNegativeButton(getActivity().getString(R.string.no), null);
        builder.setCancelable(true);

        return builder.create();
    }

    private AlertDialog createAlertDialogToAskIfToUpdateWeekly() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getActivity().getString(R.string.ask_if_to_update_data_title));
        builder.setMessage(getActivity().getString(R.string.ask_if_to_update_data_message));
        builder.setNegativeButton(getActivity().getString(R.string.no), null);
        builder.setCancelable(false);

        DialogInterface.OnClickListener clickListener = (dialog, which) -> {
            OnOptionSelectedListener listener = (OnOptionSelectedListener) getActivity();
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    listener.onTimeTableFetchSelected(true, false);
                    break;
            }
        };

        builder.setPositiveButton(getActivity().getString(R.string.ask_if_to_update_weekly_message),
                clickListener);

        return builder.create();
    }

}