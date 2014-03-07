/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ShouldLoadAlertDialogFragment extends DialogFragment {

	public ShouldLoadAlertDialogFragment() {
	}		
	
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
				SugorokuonActivity activity = (SugorokuonActivity) getActivity();
				switch(which) {
				case DialogInterface.BUTTON_POSITIVE:
					// ã≠êßupdateÇ™Ç©Ç©ÇÈÇÊÇ§Ç…ÅALastUpdateÇÃì˙ïtÇclearÅB
					UpdatedDateManager.getInstance(getActivity()).clearLastUpdate();
					activity.onLoadNotificationSelected(true);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					activity.onLoadNotificationSelected(false);
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
