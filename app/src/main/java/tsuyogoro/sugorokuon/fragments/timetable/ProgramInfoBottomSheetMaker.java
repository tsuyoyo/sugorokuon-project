package tsuyogoro.sugorokuon.fragments.timetable;


import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.models.entities.Program;

public class ProgramInfoBottomSheetMaker {

    private static final String TEXT_HTML = "text/html";

    private static final String UTF_8 = "UTF-8";

    public static void show(Program program, Context context) {

        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);

        View bottomSheetRoot = LayoutInflater.from(context)
                .inflate(R.layout.program_bottom_sheet_layout, null, false);

        WebView infoView = (WebView) bottomSheetRoot.findViewById(
                R.id.program_bottom_sheet_program_info);
        infoView.loadDataWithBaseURL(null,
                program.description + "<BR><BR><BR>" + program.info,
                TEXT_HTML, UTF_8, null);

        TextView title = (TextView) bottomSheetRoot.findViewById(
                R.id.program_bottom_sheet_program_title);
        title.setText(program.title);

        bottomSheet.setContentView(bottomSheetRoot);

        bottomSheet.show();
    }

}
