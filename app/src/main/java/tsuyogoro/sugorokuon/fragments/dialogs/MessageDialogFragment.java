/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import tsuyogoro.sugorokuon.R;

/**
 * メッセージを出して、その後特に何もしないダイアログ
 */
public class MessageDialogFragment extends DialogFragment {

    public MessageDialogFragment() {
        super();
    }

    public static final String KEY_MESSAGE = "param_key_message";

    public static final String KEY_TITLE = "param_key_title";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString(KEY_TITLE, ""))
                .setMessage(getArguments().getString(KEY_MESSAGE, ""))
                .setPositiveButton(R.string.ok, null);

        return builder.create();
    }
}
