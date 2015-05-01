/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import net.app_c.cloud.sdk.AppCCloud;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.activities.SugorokuonActivity;

/**
 * Created by tsuyoyo on 15/04/24.
 */
public class OhenDialog extends DialogFragment {

    public OhenDialog() {
        super();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((SugorokuonActivity) getActivity()).onOhenAccepted();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.drawer_app_c_advertisement))
                .setMessage(getString(R.string.ohen_dialog_text))
                .setPositiveButton(getString(R.string.ok), listener);

        return builder.create();
    }
}
