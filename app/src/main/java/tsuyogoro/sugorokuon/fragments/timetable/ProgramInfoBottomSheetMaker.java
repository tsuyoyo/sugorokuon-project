package tsuyogoro.sugorokuon.fragments.timetable;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.ProgramBottomSheetLayoutBinding;
import tsuyogoro.sugorokuon.fragments.WebViewUrlHandler;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

public class ProgramInfoBottomSheetMaker {

    private static final String TEXT_HTML = "text/html";

    private static final String UTF_8 = "UTF-8";

    public static void show(final Program program, final FragmentActivity activity) {

        final BottomSheetDialog bottomSheet = new BottomSheetDialog(activity);

        ProgramBottomSheetLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(activity), R.layout.program_bottom_sheet_layout, null, false);

        binding.setProgram(program);

        binding.programBottomSheetButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });

        binding.programBottomSheetButtonOpenBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SugorokuonUtils.launchChromeTab(activity, Uri.parse(program.url));
            }
        });

        binding.programBottomSheetProgramInfo.loadDataWithBaseURL(null,
                program.description + "<BR><BR><BR>" + program.info,
                TEXT_HTML, UTF_8, null);

        binding.programBottomSheetProgramInfo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return WebViewUrlHandler.handleOverrideUrl(view, activity, url);
            }
        });

        bottomSheet.setContentView(binding.getRoot());

        bottomSheet.show();
    }

}
