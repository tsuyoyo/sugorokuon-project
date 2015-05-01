/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.activities.SugorokuonSettingActivity;
import tsuyogoro.sugorokuon.models.prefs.LaunchedCheckPreference;

/**
 * Settingを起動する時に使うdialog。
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
                        Intent intent = new Intent(getActivity(), SugorokuonSettingActivity.class);
                        getActivity().startActivity(intent);

                        LaunchedCheckPreference.setLaunched(getActivity());
                        LaunchedCheckPreference.setLaunchedV2(getActivity());

                        // For mobile google analytics tracking.
                        // (Downloadして起動して、設定までちゃんと進んでくれた人）
                        if(mIsWelcome) {
//                            EasyTracker.getTracker().trackEvent(
//                                    getText(R.string.ga_event_category_welcome).toString(),
//                                    getText(R.string.ga_event_action_welcome_tap_ok).toString(),
//                                    GATrackingUtil.getModelAndProductName(), null);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // （メモ）
        // DialogFragmentの中で、DialogにonCancelListenerは設定できない（exeptionが飛ぶ）。
        // よって、代わりにDialogFragmentのonCancelをオーバーライドする。
        // (参考：http://memory.empressia.jp/article/44110106.html）
        super.onCancel(dialog);

        // このDialogは
        // 「Setting画面で設定を行ってもらわないとこれ以上何もできない」という時に使われるので、
        // Cancelされたら、終了処理へいかざるを得ない。
        getActivity().finish();

        // For mobile google analytics tracking.
        // (Downloadして起動してくれたけどつかってくれなかった人）
        if(mIsWelcome) {
//            EasyTracker.getTracker().trackEvent(
//                    getText(R.string.ga_event_category_welcome).toString(),
//                    getText(R.string.ga_event_action_welcome_cancel).toString(),
//                    GATrackingUtil.getModelAndProductName(), null);
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