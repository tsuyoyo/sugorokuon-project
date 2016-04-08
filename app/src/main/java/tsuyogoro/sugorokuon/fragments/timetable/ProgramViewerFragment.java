/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.fragments.WebViewUrlHandler;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.models.prefs.BrowserCacheSettingPreference;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

/**
 * Listとブラウザを半々に表示する画面のFragmentクラス
 * サイズの変更や、Listのアイテム選択からのブラウザloadをhandlingする
 * このクラスを継承するFragmentは、layoutの中に
 * <include layout="@layout/program_info_viewer_layout"></include>
 * を含むこと
 */
@SuppressLint("SetJavaScriptEnabled")
abstract class ProgramViewerFragment extends Fragment
        implements IProgramListItemTappedListener {

    public ProgramViewerFragment() {
        super();
    }

    /**
     * 番組リストの領域 (ツマミでサイズを切り替えたい部分) のView ID
     *
     * @return
     */
    abstract protected int listAreaViewId();

    private static final String TEXT_HTML = "text/html";
    private static final String UTF_8 = "UTF-8";

    private static final int PROGRESS_MAX = 100;

    // 「番組サイト」「番組情報」のタブで、focusが当たっている方。static変数で管理。
    private static int sFocusedInfoType = R.id.program_viewer_switcher_homepage;

    private WebView mSiteViewer;

    private WebView mInfoViewer;

    private ProgressBar mProgressBar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 起動直後、縦画面ならbrowser画面は出さない (という仕様にした)
        if (!SugorokuonUtils.isLandscape(getActivity())) {
            view.findViewById(R.id.program_viewer_root).setVisibility(View.GONE);
        }

        // Progress bar
        mProgressBar = (ProgressBar) view.findViewById(R.id.program_viewer_loading_progress);
        mProgressBar.setMax(PROGRESS_MAX);

        // Viewerのswitch
        mSiteViewer = (WebView) view.findViewById(R.id.program_viewer_program_site_webview);
        mInfoViewer = (WebView) view.findViewById(R.id.program_viewer_program_info);

        setupWebView(mSiteViewer, true, true, true);
        setupWebView(mInfoViewer, false, true, true);

        // 縦横切り替えの場合に、番組サイトタブの方の履歴情報を復活させる。
        if (null != savedInstanceState) {
            mSiteViewer.restoreState(savedInstanceState);
        }

        // ボタンのsetup。
        setupButtons(view);

        // Viewerのサイズを伸縮するボタン
        // 横の時はブラウザ領域のサイズ変更は無効
        View expander = getView().findViewById(R.id.program_viewer_size_expander);
        if (null != expander) {
            expander.setOnTouchListener(new AreaSizeExpanderListener());
        }
    }

    @Override
    public void onProgramTapped(Program program) {

        View infoViewer = getView().findViewById(R.id.program_viewer_root);
        if (infoViewer.getVisibility() != View.VISIBLE) {
            infoViewer.setVisibility(View.VISIBLE);
        }

        // 番組サイト。その番組がHPを持っていなかったら、「この番組にはHPがありません」と表示。
        if (program.url != null && 0 < program.url.length()) {
            getView().findViewById(R.id.program_viewer_program_site_no_hp)
                    .setVisibility(View.GONE);
            mSiteViewer.setVisibility(View.VISIBLE);
            mSiteViewer.loadUrl(program.url);
        } else {
            getView().findViewById(R.id.program_viewer_program_site_no_hp)
                    .setVisibility(View.VISIBLE);
            mSiteViewer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }

        // 詳細情報。
        mInfoViewer.loadDataWithBaseURL(null,
                program.description + "<BR><BR><BR>" + program.info,
                TEXT_HTML, UTF_8, null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // 縦横切り替えなどで、Fragmentの作り変えが生じた場合に、番組サイトタブの方は履歴情報を引き継ぐ。
        if (null != mSiteViewer) {
            mSiteViewer.saveState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cacheが無効なら、cacheを消す (Cookieは残す)
        if (!BrowserCacheSettingPreference.isCacheEnabled(getActivity())) {
            mSiteViewer.clearCache(true);
            mInfoViewer.clearCache(true);
        }
        SugorokuonApplication.getRefWatcher(getActivity()).watch(this);
    }

    private void setupWebView(WebView webView,
                              boolean adjustLayout, boolean zoomEnable, boolean javaScriptEnable) {
        // WebViewClient
        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (View.VISIBLE == view.getVisibility()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(0);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (View.VISIBLE == view.getVisibility()) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                WebViewUrlHandler.handleOverrideUrl(webView, getActivity(), url);
                return true;
            }

        };
        webView.setWebViewClient(client);

        // WebChromeClient (Progressを受け取るため）
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (View.VISIBLE == view.getVisibility()) {
                    mProgressBar.setProgress(newProgress);
                }
            }
        };
        webView.setWebChromeClient(chromeClient);

        // ページをViewの横幅にあわせる。
        if (adjustLayout) {
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

        // ズームを有効にする。
        if (zoomEnable) {
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
        }

        // javascriptを有効にする
        if (javaScriptEnable) {
            webView.getSettings().setJavaScriptEnabled(true);
        }

    }

    private void setupInfoTypeSelecter(View root) {
        // focusを当てる。
        int ids[] = {
                R.id.program_viewer_switcher_homepage,
                R.id.program_viewer_switcher_info};
        for (int id : ids) {
            if (id == sFocusedInfoType) {
                ((RadioButton) root.findViewById(id)).setChecked(true);
            } else {
                ((RadioButton) root.findViewById(id)).setSelected(false);
            }
        }

        // RadioButtonにListenerを設定。
        RadioGroup radioGroup = (RadioGroup) root.findViewById(R.id.program_viewer_switcher);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sFocusedInfoType = checkedId;
                switchViewer();
            }
        });
    }

    private void switchViewer() {
        // Focusの当たっているtabに対応するViewをVISIBILEにする。
        int switcherIds[] = {
                R.id.program_viewer_switcher_homepage,
                R.id.program_viewer_switcher_info
        };
        View viewers[] = {
                getView().findViewById(R.id.program_viewer_program_site_area),
                mInfoViewer
        };
        for (int i = 0; i < switcherIds.length; i++) {
            if (sFocusedInfoType != switcherIds[i]) {
                viewers[i].setVisibility(View.GONE);
            } else {
                viewers[i].setVisibility(View.VISIBLE);
            }
        }

        getView().findViewById(R.id.program_viewer_button_area).setVisibility(View.VISIBLE);

        // Progressbarが表示されていたら一旦消す。
        if (View.VISIBLE == mProgressBar.getVisibility()) {
            mProgressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // 「番組サイト」「番組情報」「onAir曲」のタブのセットアップ。
        setupInfoTypeSelecter(getView());

        // 前回当たっていたタブのフォーカスを復元
        switchViewer();
    }

    private void setupButtons(View viewRoot) {
        // Reload、back、forward、stopのボタンのセットアップ。
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
//                    case R.id.program_viewer_browser_back:
//                        backWebView();
//                        break;
//                    case R.id.program_viewer_browser_forward:
//                        forwardWebView();
//                        break;
                    case R.id.program_viewer_browser_app_open:
                        String focusedSite = mSiteViewer.getUrl();
                        if (null != focusedSite) {
                            Uri uri = Uri.parse(focusedSite);
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(i);
                        }
                        break;
                    case R.id.program_viewer_close:
                        closeInfoViewer();
                        break;
                }
            }
        };
//        viewRoot.findViewById(R.id.program_viewer_browser_back).setOnClickListener(listener);
//        viewRoot.findViewById(R.id.program_viewer_browser_forward).setOnClickListener(listener);
        viewRoot.findViewById(R.id.program_viewer_browser_app_open).setOnClickListener(listener);

        // 横画面の時は無い
        View closeBtn = viewRoot.findViewById(R.id.program_viewer_close);
        if (null != closeBtn) {
            closeBtn.setOnClickListener(listener);
        }
    }

    protected boolean isInfoViewerVisible() {
        boolean visible = false;
        if (null != getView() &&
                View.VISIBLE == getView().findViewById(R.id.program_viewer_root).getVisibility()) {
            visible = true;
        }
        return visible;
    }

    protected boolean closeInfoViewer() {
        if (null != getView() && null != getView().findViewById(R.id.program_viewer_close)) {
            getView().findViewById(R.id.program_viewer_root).setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    private WebView currentVisibleWebView() {
        if (sFocusedInfoType == R.id.program_viewer_switcher_homepage) {
            return mSiteViewer;
        } else if (sFocusedInfoType == R.id.program_viewer_switcher_info) {
            return mInfoViewer;
        } else {
            return null;
        }
    }

    protected boolean backWebView() {
        WebView target = currentVisibleWebView();
        if (null != target && target.canGoBack()) {
            target.goBack();
            return true;
        } else {
            return false;
        }
    }

    protected boolean forwardWebView() {
        WebView target = currentVisibleWebView();
        if (null != target && target.canGoForward()) {
            target.goForward();
            return true;
        } else {
            return false;
        }
    }

    private class AreaSizeExpanderListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean consumed = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    consumed = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // getY() で取れるのは、このViewからの相対座標になる。
                    sizeChange(event.getY());
                    break;
            }
            return consumed;
        }

        private void sizeChange(float movedY) {

            // タテ画面のみ、ProgramListとInfoの領域のエリア配分を変える
            if (!SugorokuonUtils.isLandscape(getActivity())) {

                View infoArea = getView().findViewById(R.id.program_viewer_root);
                View listArea = getView().findViewById(listAreaViewId());

                float listAreaHeight = listArea.getHeight() + movedY;
                float infoAreaHeight = infoArea.getHeight() - movedY;

                if (50 < SugorokuonUtils.calculateDpfromPx(getActivity(), infoAreaHeight)
                        && 50 < SugorokuonUtils.calculateDpfromPx(getActivity(), listAreaHeight)) {

                    listArea.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 0, (int) listAreaHeight));

                    infoArea.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 0, (int) infoAreaHeight));
                }
            }

            // TODO :
            // 番組表の日時や種類を切り替えると、ここでの変更がリセットされちゃうので、
            // staticで保存させて、大きさが変わらないようにしよう
        }
    }

}
