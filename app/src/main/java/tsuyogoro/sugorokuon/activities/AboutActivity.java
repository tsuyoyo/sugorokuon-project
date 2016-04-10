package tsuyogoro.sugorokuon.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class AboutActivity extends DrawableActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        webView.setLayoutParams(layoutParams);
        setContentView(webView);

        webView.loadUrl("file:///android_asset/license.html");


    }
}
