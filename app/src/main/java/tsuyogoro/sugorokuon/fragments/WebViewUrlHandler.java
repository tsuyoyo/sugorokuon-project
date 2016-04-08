package tsuyogoro.sugorokuon.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

public class WebViewUrlHandler {

    public static boolean handleOverrideUrl(WebView webView, FragmentActivity context, String url) {
        if ("mailto:".length() < url.length()
                && url.substring(0, 7).equals("mailto:")) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            context.startActivity(intent);
            webView.reload();
        } else if ("http://twitter.com".length() < url.length()
                && url.substring(0, 18).equals("http://twitter.com")) {
            Intent tweetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(tweetIntent);
        } else if ("http://www.facebook.com/".length() < url.length()
                && url.substring(0, 24).equals("http://www.facebook.com/")) {
            Intent fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(fbIntent);
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            SugorokuonUtils.launchChromeTab(context, Uri.parse(url));
        } else {
            webView.loadUrl(url);
        }
        return true;
    }
}
