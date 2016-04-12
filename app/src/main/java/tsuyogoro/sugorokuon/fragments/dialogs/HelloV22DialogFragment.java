package tsuyogoro.sugorokuon.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.activities.SugorokuonActivity;

public class HelloV22DialogFragment extends DialogFragment {

    public HelloV22DialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.hello_v2_2_dialog_meesage))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((SugorokuonActivity) getActivity()).onStartV2app(true);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ((SugorokuonActivity) getActivity()).onStartV2app(false);
                    }
                });

        return builder.create();
    }
}
