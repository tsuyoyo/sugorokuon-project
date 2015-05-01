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

    public static interface OnOptionSelectedListener {
        public void onTimeTableFetchSelected(
                boolean startUpdate, boolean updateStation, boolean updateToday);
    }

    public TimeTableFetchAlertDialog() {
    }

    public static final String KEY_UPDATE_STATION = "update_station";

    public static final String KEY_UPDATE_TODAY = "update_today";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ((OnOptionSelectedListener) getActivity()).onTimeTableFetchSelected(
                                true,
                                getArguments().getBoolean(KEY_UPDATE_STATION, false),
                                getArguments().getBoolean(KEY_UPDATE_TODAY, false));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        ((OnOptionSelectedListener) getActivity()).onTimeTableFetchSelected(
                                false,
                                getArguments().getBoolean(KEY_UPDATE_STATION, false),
                                getArguments().getBoolean(KEY_UPDATE_TODAY, false));
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.ask_if_to_update_data_title));
        builder.setMessage(getActivity().getString(R.string.ask_if_to_update_data_message));
        builder.setPositiveButton(getActivity().getString(R.string.yes), listener);
        builder.setNegativeButton(getActivity().getString(R.string.no), listener);
        builder.setCancelable(false);

        return builder.create();
    }

}