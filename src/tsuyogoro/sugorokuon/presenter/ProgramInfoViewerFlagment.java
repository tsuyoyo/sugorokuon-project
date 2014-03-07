/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.util.Calendar;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Feed;
import tsuyogoro.sugorokuon.datatype.OnAirSong;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.service.SugorokuonService;
import tsuyogoro.sugorokuon.settings.preference.BrowserCacheSettingPreference;
import tsuyogoro.sugorokuon.settings.preference.OnAirSearchTargetPreference;
import tsuyogoro.sugorokuon.util.GATrackingUtil;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.FeedDataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("SetJavaScriptEnabled")
public class ProgramInfoViewerFlagment extends Fragment 
	implements ProgramDataManager.IEventListener, 
			   IViewFlowListener, StationDataManager.IEventListener {

	private static final String TEXT_HTML = "text/html";
	private static final String UTF_8 = "UTF-8";
	
	private static final int PROGRESS_MAX = 100;
	
	// 「番組サイト」「番組情報」のタブで、focusが当たっている方。static変数で管理。
	private static int sFocusedInfoType = R.id.program_viewer_switcher_homepage;
	
	private WebView mSiteViewer;
	private WebView mInfoViewer;
	private LinearLayout mOnAirViewer;
	
	private ProgressBar mLoadingProgressBar;
	
	private ProgramInfoOnAirSongsAdapter mOnAirSongAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(SugorokuonConst.LOGTAG, "+ ProgramInfoViewerFlagment:onCreateView");
		
		super.onCreateView(inflater, container, savedInstanceState);
		View body = inflater.inflate(R.layout.program_info_viewer_fragment, null);
		
		// Progress bar
		mLoadingProgressBar = 
			(ProgressBar) body.findViewById(R.id.program_viewer_loading_progress);
		mLoadingProgressBar.setMax(PROGRESS_MAX);
		
		// Viewerのswitch
		mSiteViewer = (WebView) body.findViewById(R.id.program_viewer_program_site_webview);
		mInfoViewer = (WebView) body.findViewById(R.id.program_viewer_program_info);
		mOnAirViewer = (LinearLayout) body.findViewById(R.id.program_viewer_onair_songs);
		
		setupViewers();
	
		// 縦横切り替えの場合に、番組サイトタブの方の履歴情報を復活させる。
		if(null != savedInstanceState) {
			mSiteViewer.restoreState(savedInstanceState);
		}
		
		// ボタンのsetup。
		setupButtons(body);
		
		// Viewerのサイズを伸縮するボタン
		setupAreasizeExpander(body);
		
		Log.d(SugorokuonConst.LOGTAG, "- ProgramInfoViewerFlagment:onCreateView");		
		
		return body;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// 縦横切り替えなどで、Fragmentの作り変えが生じた場合に、番組サイトタブの方は履歴情報を引き継ぐ。
		if(null != mSiteViewer) {
			mSiteViewer.saveState(outState);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Cacheが無効なら、cacheを消す。
		// (メモ) Cookieは残そう。
		if(!BrowserCacheSettingPreference.isCacheEnabled(getActivity())) {
			mSiteViewer.clearCache(true);
			mInfoViewer.clearCache(true);
		}
	}
	
	private void setupViewers() {
		setupWebView(mSiteViewer, true, true, true);
		setupWebView(mInfoViewer, false, true, true);
		
		mOnAirSongAdapter = new ProgramInfoOnAirSongsAdapter(getActivity());
		ListView onAirList = (ListView) mOnAirViewer.findViewById(R.id.program_viewer_onair_songs_list);
		onAirList.setAdapter(mOnAirSongAdapter);
		onAirList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OnAirSong song = (OnAirSong) mOnAirSongAdapter.getItem(position);
				if(null != song) {
					Intent intent = new Intent(SugorokuonService.ACTION_SEARCH_SONG_ON_WEB);
					intent.putExtra(SugorokuonService.EXTRA_ARTIST, song.artist);
					intent.putExtra(SugorokuonService.EXTRA_SONG_TITLE, song.title);
					getActivity().startService(intent);
					
					// For Google Analytics tracking.
					String eventCategory = "";
					switch(OnAirSearchTargetPreference.getSearchTarget(getActivity())) {
						case OnAirSearchTargetPreference.SEARCH_TARGET_YOUTUBE:
							eventCategory = getString(R.string.ga_event_onair_song_search_youtube);
							break;
						case OnAirSearchTargetPreference.SEARCH_TARGET_GOOGLE:
							eventCategory = getString(R.string.ga_event_onair_song_search_google);							
							break;
					}
					EasyTracker.getTracker().trackEvent(
							getText(R.string.ga_event_onair_song_search).toString(),
							eventCategory, GATrackingUtil.getModelAndProductName(), null);
				}
			}
		});
	}
	
	private void setupWebView(WebView webView, 
			boolean adjustLayout, boolean zoomEnable, boolean javaScriptEnable) {
		// WebViewClient
		WebViewClient client = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if(View.VISIBLE == view.getVisibility()) {
					mLoadingProgressBar.setVisibility(View.VISIBLE);
					mLoadingProgressBar.setProgress(0);
				}			
			}			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(View.VISIBLE == view.getVisibility()) {
					mLoadingProgressBar.setVisibility(View.GONE);
				}
			}
			@Override 
			public boolean shouldOverrideUrlLoading(WebView webView, String url) { 
				if("mailto:".length() < url.length() 
						&& url.substring(0, 7).equals("mailto:")) { 
					Uri uri = Uri.parse(url); 
					Intent intent = new Intent(Intent.ACTION_SENDTO, uri); 
					startActivity(intent); 
					webView.reload(); 
				}
				else if("http://twitter.com".length() < url.length() 
						&& url.substring(0, 18).equals("http://twitter.com")) {
					Intent tweetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(tweetIntent);
				}
				else if("http://www.facebook.com/".length() < url.length() 
						&& url.substring(0, 24).equals("http://www.facebook.com/")) {
					Intent fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(fbIntent);					
				}
				else { 
					webView.loadUrl(url); 
				}
				return true; 
			} 

		};
		webView.setWebViewClient(client);
		
		// WebChromeClient (Progressを受け取るため）
		WebChromeClient chromeClient = new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if(View.VISIBLE == view.getVisibility()) {
					mLoadingProgressBar.setProgress(newProgress);
				}
			}
		};
		webView.setWebChromeClient(chromeClient);
		
		// ページをViewの横幅にあわせる。
		if(adjustLayout) {
			webView.getSettings().setLoadWithOverviewMode(true);  
			webView.getSettings().setUseWideViewPort(true);
		}			
		
		// ズームを有効にする。
		if(zoomEnable) {
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setBuiltInZoomControls(true);
		}
		
		// javascriptを有効にする
		if(javaScriptEnable) {
			webView.getSettings().setJavaScriptEnabled(true);
		}
		
	}
	
	private void setupInfoTypeSelecter(View root) {
		// focusを当てる。
		int ids[] = {R.id.program_viewer_switcher_homepage, 
				     R.id.program_viewer_switcher_info,
				     R.id.program_viewer_switcher_onair };
		for(int id : ids) {
			if(id == sFocusedInfoType) {
				((RadioButton) root.findViewById(id)).setChecked(true);
			} else {
				((RadioButton) root.findViewById(id)).setSelected(false);
			}
		}
		
		// RadioButtonにListenerを設定。
		RadioGroup radioGroup = (RadioGroup) root.findViewById(R.id.program_viewer_switcher);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
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
				R.id.program_viewer_switcher_info,
				R.id.program_viewer_switcher_onair };
		View viewers[] = {
				getView().findViewById(R.id.program_viewer_program_site_area),
				mInfoViewer, mOnAirViewer };
		for(int i=0; i < switcherIds.length; i++) {
			if(sFocusedInfoType != switcherIds[i]) {
				viewers[i].setVisibility(View.GONE);
			} else {
				viewers[i].setVisibility(View.VISIBLE);
			}
		}
		
		// ボタンを出さない場合はボタン領域を消す。
		if(R.id.program_viewer_switcher_onair == sFocusedInfoType) {
			getView().findViewById(R.id.program_viewer_button_area).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.program_viewer_button_area).setVisibility(View.VISIBLE);			
		}
		
		// onAir曲タブに当たったら、データをloadして表示。
		if(R.id.program_viewer_switcher_onair == sFocusedInfoType) {
			loadOnAirSongData();
		}

		// Progressbarが表示されていたら一旦消す。
		if(View.VISIBLE == mLoadingProgressBar.getVisibility()) {
			mLoadingProgressBar.setVisibility(View.GONE);
		}

		// For mobile google anlaytics.
		int actionStrId = -1;
		switch(sFocusedInfoType) {
		case R.id.program_viewer_switcher_homepage:
			actionStrId = R.string.ga_event_action_tap_homepage_tab;			
			break;
		case R.id.program_viewer_switcher_info:
			actionStrId = R.string.ga_event_action_tap_proginfo_tab;
			break;
		case R.id.program_viewer_switcher_onair:
			actionStrId = R.string.ga_event_action_tap_onair_song_tab;
			break;
		}
		SugorokuonActivityEventTracker.submitGAEvent(
				actionStrId, getActivity(), GATrackingUtil.getModelAndProductName());
	}
	
	private void loadOnAirSongData() {
		// 今focusの当たっているstationのonAir曲情報を取る
		FeedDataViewFlow feedViewFlow = FeedDataViewFlow.getInstance();
		Feed feed = feedViewFlow.getFocusedStationFeed();
		if(null == feed) {
			// 無ければdownloadを開始する。
			int res = feedViewFlow.invokeDownloadFeed(getActivity());

			TextView info = 
				(TextView) getView().findViewById(R.id.program_viewer_onair_songs_execution_info);			
			String msg = "";
			switch(res) {
			case FeedDataViewFlow.START_TASK_EXECUTION:
				// load中という旨を表示
				msg = getString(R.string.program_viewer_switch_loading).toString();
				break;
			case FeedDataViewFlow.NO_FEED_FOR_RECOMMEND_CATEGORY:
				msg = getString(R.string.program_viewer_onair_song_search_no_info_for_recommend);
				break;
			case FeedDataViewFlow.STATION_MANAGE_HAS_NOT_INITIALIZED:
				msg = String.format(getString(R.string.program_viewer_onair_song_search_error), res);				
				break;
			}
			info.setText(msg);
			info.setVisibility(View.VISIBLE);
			
			// Listの部分はGoneにする（そもそも表示されてないかもだが、念のため）
			getView().findViewById(R.id.program_viewer_onair_songs_area).setVisibility(View.GONE);

		} else {
			activateSongInfoArea(feed);
		}
	}
	
	private void activateSongInfoArea(Feed feed) {
		if(0 < feed.onAirSongs.size()) {
			// あれば即表示。
			mOnAirSongAdapter.setFeed(feed);
			mOnAirSongAdapter.notifyDataSetChanged();
			
			getView().findViewById(R.id.program_viewer_onair_songs_execution_info).setVisibility(View.GONE);
			getView().findViewById(R.id.program_viewer_onair_songs_area).setVisibility(View.VISIBLE);
			
			// 「～のonAir曲情報」を表示。下線をつけるために色々。
			StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
			String infoMsg = String.format(
					getString(R.string.program_viewer_onair_song_search_info).toString(),
					stationMgr.getStationInfo().get(stationMgr.getFocusedIndex() - 1).name);
			Spannable t = Spannable.Factory.getInstance().newSpannable(infoMsg);
			UnderlineSpan us = new UnderlineSpan();
			t.setSpan(us, 0, infoMsg.length(), t.getSpanFlags(us));
			TextView info = (TextView) getView().findViewById(R.id.program_viewer_onair_songs_info);
			info.setText(t, TextView.BufferType.SPANNABLE);
			
			// Updateボタンをsetup
			setupUpdateOnAirSongBtn();
		} 
		// onAir情報が取れなかった場合。
		// 「～は、直近の曲情報を提供していないようです」と表示。
		else {
			StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
			String noInfoMsg = String.format(
					getString(R.string.program_viewer_onair_song_search_no_info).toString(),
					stationMgr.getStationInfo().get(stationMgr.getFocusedIndex() - 1).name);
			
			TextView infoText = (TextView) getView().findViewById(R.id.program_viewer_onair_songs_execution_info);
			infoText.setText(noInfoMsg);
			getView().findViewById(R.id.program_viewer_onair_songs_area).setVisibility(View.GONE);
			
		}
	}
	
	private void setupUpdateOnAirSongBtn() {
		ImageButton updateBtn = (ImageButton) getView().findViewById(
				R.id.program_viewer_onair_songs_reload_btn);
		updateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FeedDataViewFlow.getInstance().removeCurrentFocusedStationCache();
				loadOnAirSongData();
			}
		});
	}
	
		
	@Override
	public void onResume() {
		super.onResume();
		DataViewFlow.getInstance().register(this);
		
		DataViewFlow.getInstance().getProgramDataMgr().listeners.add(this);
		FeedDataViewFlow.getInstance().register(this);
		
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			stationMgr.listeners.add(this);
		}
		
		// 「番組サイト」「番組情報」「onAir曲」のタブのセットアップ。
		setupInfoTypeSelecter(getView());
		
		// 前回当たっていたタブのフォーカスを復元
		switchViewer();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		DataViewFlow.getInstance().unregister(this);
		
		DataViewFlow.getInstance().getProgramDataMgr().listeners.remove(this);
		FeedDataViewFlow.getInstance().unregister(this);
		
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			stationMgr.listeners.remove(this);
		}
	}

	@Override
	public void onFocusedProgramIndexChanged(int newIndex) {
		// 違う番組が選択されたら、選択された時点で各Viewerにその番組のURLなどを読ませる。
		ProgramDataManager dataMgr = DataViewFlow.getInstance().getProgramDataMgr();
		if(0 < dataMgr.getFocusedProgramList().size()) {
			Program focusedProg = dataMgr.getFocusedProgramList().get(newIndex);
			// 番組サイト。その番組がHPを持っていなかったら、「この番組にはHPがありません」と表示。
			if(0 < focusedProg.url.length()) {
				getView().findViewById(R.id.program_viewer_program_site_no_hp)
					.setVisibility(View.GONE);
				mSiteViewer.setVisibility(View.VISIBLE);
				mSiteViewer.loadUrl(focusedProg.url);
			} else {
				getView().findViewById(R.id.program_viewer_program_site_no_hp)
					.setVisibility(View.VISIBLE);
				mSiteViewer.setVisibility(View.GONE);
				mLoadingProgressBar.setVisibility(View.GONE);
			}
			// 詳細情報。
			mInfoViewer.loadDataWithBaseURL(null, 
					focusedProg.description + "<BR><BR><BR>" + focusedProg.info, 
					TEXT_HTML, UTF_8, null);
		}
	}

	@Override
	public void onFocusedProgramDateChanged(Calendar newDate) {
		// Noting to do.
	}
	
	private void setupButtons(View viewRoot) {
		// Reload、back、forward、stopのボタンのセットアップ。
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebView target = null;
				if(sFocusedInfoType == R.id.program_viewer_switcher_homepage) {
					target = mSiteViewer;
				} else if(sFocusedInfoType == R.id.program_viewer_switcher_info) {
					target = mInfoViewer;
				} else {
					return;
				}

				int actionStrId = -1;
				switch(v.getId()) {
				case R.id.program_viewer_browser_back:
					target.goBack();
					actionStrId = R.string.ga_event_action_tap_back_btn;					
					break;
				case R.id.program_viewer_browser_cancel:
					target.stopLoading();
					actionStrId = R.string.ga_event_action_tap_stop_btn;
					break;
				case R.id.program_viewer_browser_forward:
					target.goForward();
					actionStrId = R.string.ga_event_action_tap_forward_btn;						
					break;
				case R.id.program_viewer_browser_refresh:
					target.reload();
					actionStrId = R.string.ga_event_action_tap_reload_btn;
					break;				
				}
				
				// For mobile google analytics.
				if(-1 != actionStrId) {
					SugorokuonActivityEventTracker.submitGAEvent(
							actionStrId, getActivity(), GATrackingUtil.getModelAndProductName());
				}
			}
		};
		viewRoot.findViewById(R.id.program_viewer_browser_back).setOnClickListener(listener);
		viewRoot.findViewById(R.id.program_viewer_browser_forward).setOnClickListener(listener);
		viewRoot.findViewById(R.id.program_viewer_browser_cancel).setOnClickListener(listener);		
		viewRoot.findViewById(R.id.program_viewer_browser_refresh).setOnClickListener(listener);
		
		// 横の時はブラウザ領域のサイズ変更は無効
		if(SugorokuonUtils.isLandscape(getActivity())) {
			viewRoot.findViewById(R.id.program_viewer_size_expander).setVisibility(View.GONE);
		}
		
	}
	
	private void setupAreasizeExpander(View root) {
		View expander = root.findViewById(R.id.program_viewer_size_expander);
		expander.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean consumed = false;
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					consumed = true;
					// Google Analytics tracking.
					SugorokuonActivityEventTracker.submitGAEvent(
							R.string.ga_event_action_change_programinfo_viewer_size, 
							getActivity(), GATrackingUtil.getModelAndProductName());
					break;
				case MotionEvent.ACTION_MOVE:
					// getY() で取れるのは、このViewからの相対座標になる。
					float movedY = event.getY();
					((SugorokuonActivity) getActivity()).changeLayoutWeight(movedY);
					break;
				}
				return consumed;
			}
		});
	}

	@Override
	public void onViewFlowEvent(ViewFlowEvent event) {
		switch(event) {
		case COMPLETE_DATA_UPDATECOMPLETE:
			StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
			if(null != stationMgr) {
				stationMgr.listeners.add(this);
			}
			break;
		case COMPLETE_FEED_DOWNLOAD:
			activateSongInfoArea(FeedDataViewFlow.getInstance().getFocusedStationFeed());
			break;
		case FAILED_FEED_DONWLOAD:
			// Loadingを消す。
			getView().findViewById(R.id.program_viewer_onair_songs_execution_info).setVisibility(View.GONE);
			
			// エラーメッセージを表示。
			getView().findViewById(R.id.program_viewer_onair_songs_area).setVisibility(View.VISIBLE);
			TextView info = 
				(TextView) getView().findViewById(R.id.program_viewer_onair_songs_info);
			String msg = String.format(getString(R.string.program_viewer_onair_song_search_error), -1);
			info.setText(msg);
			
			// Updateボタンを設定。
			setupUpdateOnAirSongBtn();
			break;	
		default:
			break;
		}
	}

	@Override
	public void onProgress(int whatsRunning, int progress, int max) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStationIndexChanged(int newIndex) {
		if(R.id.program_viewer_switcher_onair == sFocusedInfoType) {
			loadOnAirSongData();
		}
	}
	
}
