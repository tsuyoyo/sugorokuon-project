/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.util.Calendar;
import java.util.Locale;

import jp.co.cayto.appc.sdk.android.WebViewActivity;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.appinfo.AboutAppActivity;
import tsuyogoro.sugorokuon.service.SugorokuonService;
import tsuyogoro.sugorokuon.settings.SugorokuonSettingActivity;
import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import tsuyogoro.sugorokuon.settings.preference.AreaSettingPreference;
import tsuyogoro.sugorokuon.settings.preference.LaunchedCheckPreference;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * ���̃A�v����Main��Activity�B
 * 
 * @author Tsuyoyo
 *
 */
public class SugorokuonActivity extends FragmentActivity 
	implements IViewFlowListener {

	// Menu��ID
	private final int MENU_LAUNCH_RADIO 	= 0;
	private final int MENU_LAUNCH_SETTINGS 	= 1;
	private final int MENU_INVOKE_UPDATE 	= 2;
	private final int MENU_CHECK_NEXTUPDATE = 3;
	private final int MENU_ABOUT_APP 		= 4;
	private final int MENU_RATE_APP			= 5;
	
	// DialogFragment��tag
	private static final String TAG_PROGRESS_DIALOG = "progress_dialog";
	private static final String TAG_SHOULD_LOAD_DIALOG = "should_load_dialog";
	private static final String TAG_CHECK_UPDATE_INFO_DIALOG = "updateinfo_dialog";
	private static final String TAG_WELCOME_DIALOG = "welcome_dialog";	
	private static final String TAG_NO_AREA_DIALOG = "no_area_dialog";
	
	private static final int REQUESTCODE_SETTINGS = 100;
	
	private static final String GOOGLE_PLAY_URL = "market://details?id=tsuyogoro.sugorokuon";
	
	private StationSpinnerAdapter mSpinnerAdapter;
	
	private DataViewFlow mDataViewFlow;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.mainactivitylayout);
        
        mDataViewFlow = DataViewFlow.getInstance();
        setupActionbarMode();        
        setupActionbarDropdown();
        
        // ���߂Ă̋N���Ȃ�Welcome��dialog���o���B
        if(!LaunchedCheckPreference.hasLaunched(this)) {
        	showWelcomDialog();
        }   
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		// For mobile Google analytics.
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// For mobile Google analytics.
		EasyTracker.getInstance().activityStop(this);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Gingerbread MR1���傫��Platform�œ����Ă���ꍇ�́A
		// Actionbar�̏�ɁA����A�v���N���Ɛݒ���ڂ���B
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			// ����A�v�����N��
			menu.add(Menu.NONE, MENU_LAUNCH_RADIO, 0, getString(R.string.option_launch_radio_app))
				.setIcon(R.drawable.hardware_headphones);
			MenuItemCompat.setShowAsAction(
					menu.findItem(MENU_LAUNCH_RADIO), MenuItem.SHOW_AS_ACTION_ALWAYS);

			// �ݒ�
			menu.add(Menu.NONE, MENU_LAUNCH_SETTINGS, 1, getString(R.string.option_settings))
				.setIcon(R.drawable.action_settings);
			MenuItemCompat.setShowAsAction(
					menu.findItem(MENU_LAUNCH_SETTINGS), MenuItem.SHOW_AS_ACTION_ALWAYS);			
		}
		
		// ����A�v���N���Ɛݒ�ȊO��option�Ɋւ��ẮA��ʂ�orientation�����āA�o�����ǂ��������߂�B
		int menuVisibility = MenuItem.SHOW_AS_ACTION_NEVER;
		if(SugorokuonUtils.isLandscape(this)) {
			menuVisibility = MenuItem.SHOW_AS_ACTION_ALWAYS;
		}
		
		// �������ԑg�\�X�V
		menu.add(Menu.NONE, MENU_INVOKE_UPDATE, 2, getString(R.string.option_update_now))
			.setIcon(R.drawable.navigation_refresh);
		MenuItemCompat.setShowAsAction(menu.findItem(MENU_INVOKE_UPDATE), menuVisibility);
		
		// �ŏI�X�V�����Ǝ���update����
		menu.add(Menu.NONE, MENU_CHECK_NEXTUPDATE, 3, getString(R.string.option_check_update_date))
			.setIcon(R.drawable.action_check_update_date);
		MenuItemCompat.setShowAsAction(menu.findItem(MENU_CHECK_NEXTUPDATE), menuVisibility);		
		
		// ���̃A�v���ɂ���
		menu.add(Menu.NONE, MENU_ABOUT_APP, 4, getString(R.string.option_about_app))
			.setIcon(android.R.drawable.ic_dialog_info);
		MenuItemCompat.setShowAsAction(menu.findItem(MENU_ABOUT_APP), MenuItem.SHOW_AS_ACTION_NEVER);

		// ���̃A�v����]��
		menu.add(Menu.NONE, MENU_RATE_APP, 5, getString(R.string.option_rate_app))
			.setIcon(android.R.drawable.star_on);
		MenuItemCompat.setShowAsAction(menu.findItem(MENU_RATE_APP), MenuItem.SHOW_AS_ACTION_NEVER);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_LAUNCH_RADIO:
				launchRadiko();
				trackActionEvent(getText(R.string.ga_event_action_radiolaunch).toString());
				break;
			case MENU_INVOKE_UPDATE:
				showShouldLoadAlertDialog();
				trackActionEvent(getText(R.string.ga_event_action_force_update).toString());
				break;
			case MENU_LAUNCH_SETTINGS:
				launchSettings();
				trackActionEvent(getText(R.string.ga_event_action_settings).toString());
				break;
			case MENU_CHECK_NEXTUPDATE:
				UpdateInfoDialogFragment updateInfoDialog = new UpdateInfoDialogFragment();
				updateInfoDialog.show(getSupportFragmentManager(), TAG_CHECK_UPDATE_INFO_DIALOG);
				trackActionEvent(getText(R.string.ga_event_action_check_update).toString());
				break;
			case MENU_ABOUT_APP:
				startActivity(new Intent(this, AboutAppActivity.class));
				trackActionEvent(getText(R.string.ga_event_action_about_app).toString());
				break;
			case MENU_RATE_APP:
				launchGooglePlayForFeedback();
				trackActionEvent(getText(R.string.ga_event_action_rate_app).toString());				
				break;
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void trackActionEvent(String label) {
		EasyTracker.getTracker().trackEvent(
				getText(R.string.ga_event_category_action).toString(),
				getText(R.string.ga_event_action_selected).toString(), 
				label, null);		
	}

	/**
	 * ProgramList��Info�̗̈�̃G���A�z����ς���B
	 * 
	 * @param expandProgramInfo
	 */
	public void changeLayoutWeight(float diffProgramInfoArea_Y) {
		// �^�e��ʂ̂�
		if(!SugorokuonUtils.isLandscape(this)) {			
			View programInfoArea = findViewById(R.id.program_info_fragment);
			View programListArea = findViewById(R.id.program_list_fragment);
			
			float programListAreaHeight = programListArea.getHeight() + diffProgramInfoArea_Y;
			float programInfoAreaHeight = programInfoArea.getHeight() - diffProgramInfoArea_Y;

			if(50 < SugorokuonUtils.calculateDpfromPx(this, programInfoAreaHeight)
					&& 50 < SugorokuonUtils.calculateDpfromPx(this, programListAreaHeight)) {
				programListArea.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, 0, (int) programListAreaHeight));
				programInfoArea.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, 0, (int) programInfoAreaHeight));
			}
			
		}			
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// �ݒ�l���ς�����玩���I��update���J�n�B
		if(REQUESTCODE_SETTINGS == requestCode) {
			switch(resultCode) {
			// Area���ς������ēx�ԑg�\�X�V�B
			case SugorokuonSettingActivity.RESULT_AREA_SETTINGS_UPDATED:
				showSettingsChangedDialog();
				break;
			// TODO : 
			// Recommend keyword��ς�����A�S�Ă�list��update����̂ł͂Ȃ��A
			// DB�����Recommend��update����悤�ɂ���B
			case SugorokuonSettingActivity.RESULT_KEYWORD_UPDATED:
				showSettingsChangedDialog();
				break;
			// Reminder�̐ݒ�𔽉f������B
			case SugorokuonSettingActivity.RESULT_REMINDER_UPDATED:
				startService(new Intent(SugorokuonService.ACTION_UPDATE_TIMER));
				break;								
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
        // Area�ݒ肪�Ȃ������瑦setting�ցB
        if(0 == AreaSettingPreference.getTargetAreas(this).size() 
        		&& LaunchedCheckPreference.hasLaunched(this)) {
        	showNoAreaDialog();
        	return;
        } 
        
        // DataViewFlow�̏��������o���Ă��Ȃ��āA������N���ł͂Ȃ����B
        DataViewFlow dataViewFlow = DataViewFlow.getInstance();
        if(dataViewFlow.shouldLoadData() && LaunchedCheckPreference.hasLaunched(this)) {            
			// �l�b�g���[�N����̍X�V���K�v�ȏꍇ�B
			Calendar now = Calendar.getInstance(Locale.JAPAN);
			if(UpdatedDateManager.getInstance(this).shouldUpdate(now)) {
				// Dialog����ʂɏo�Ă��Ȃ�������o���B
				if(null == getSupportFragmentManager().findFragmentByTag(TAG_SHOULD_LOAD_DIALOG)
					&& null == getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG)) {
					// �������Ȃ�wait�A�����łȂ�������uUpdate���܂����H�v��dialog�B
					if(dataViewFlow.isUpdating()) {
						showProgressDialog();					
					} else {
						showShouldLoadAlertDialog();					
					}
				}
			}
			// �l�b�g���[�N����̍X�V�͕s�v������ViewFlow��setup�������Ă��Ȃ��B�Ƃ�����ԁB
			else {
				if(null == getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG)) {
					showProgressDialog();
				}
				if(!dataViewFlow.isUpdating()) {
					invokeProgramDataLoad();
				}
			}
        }
        // DataViewFlow�̏������͊��ɍς�ł���ꍇ�B
        else if(LaunchedCheckPreference.hasLaunched(this)){
        	// �c���؂�ւ��ȂǁAload�͊��ɏI����Ă����Ԃł�Activity����������B
   			// ����ɔ����āA������layout�̓ǂݍ��݂�����B
			if(null == findViewById(R.id.mainactiity_root)) {			

        	}        	
        }
        	
        // ��ʐ؂�ւ���spinner��setup�B
        // �i���Ńf�[�^���ς���Ă邩������Ȃ��̂�onResume�ŌĂԁj�B
        setActionbarFocusIndex(false);
        
        mDataViewFlow.register(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        mDataViewFlow.unregister(this);
	}
	
	/*
	 * �ԑg���̓ǂݍ��݂��n�߂܂����H��dialog�ɑ΂���yes/no�̌��ʂ��󂯎��B
	 */
	void onLoadNotificationSelected(boolean startLoad) {
		if(startLoad) {
			invokeProgramDataLoad();
		} else {
			// �\������f�[�^�������ɂ�������炸�u�ǂݍ��݂܂���v���I�΂ꂽ��A�d���Ȃ��̂ŏI���B
			if(DataViewFlow.getInstance().shouldLoadData()) {
				finish();
			}			
		}
	}
	
	/*
	 * �ݒ肪�ς��܂����B�ԑg�̍X�V�����܂����H��dialog�ɑ΂��Ă�yes/no�̌��ʂ��󂯎��B
	 */
	void onSettingsDialogSelected(boolean startLoad) {
		if(startLoad) {
			invokeProgramDataLoad();
		}
	}
	
	private void launchRadiko() {
		Intent launchIntent = new Intent(SugorokuonService.ACTION_LAUNCH_RADIO_APP);
		startService(launchIntent);
	}
	
	private void launchSettings() {
		Intent intentForSettings =  
			new Intent(SugorokuonActivity.this, SugorokuonSettingActivity.class);
		startActivityForResult(intentForSettings, REQUESTCODE_SETTINGS);
	}
	
	private void invokeProgramDataLoad() {
		// Service����DataViewFlow��invokeLoad��kick����B
		startService(new Intent(SugorokuonService.ACTION_LOAD_PROGRAM_DATA));
		
        // progressDialog���o���B
		showProgressDialog();
	}
	
	private void showProgressDialog() {
		LoadingProgressDialogFragment progressDlg = new LoadingProgressDialogFragment();
		progressDlg.show(getSupportFragmentManager(), TAG_PROGRESS_DIALOG);
	}
	
	private void showShouldLoadAlertDialog() {
		ShouldLoadAlertDialogFragment dialog = new ShouldLoadAlertDialogFragment();
		dialog.show(getSupportFragmentManager(), TAG_SHOULD_LOAD_DIALOG);
	}
	
	private void showSettingsChangedDialog() {
		SettingsChangedAlertDialog dialog = new SettingsChangedAlertDialog();
		dialog.show(getSupportFragmentManager(), TAG_SHOULD_LOAD_DIALOG);
	}
	
	private void showWelcomDialog() {
		if(null == getSupportFragmentManager().findFragmentByTag(TAG_WELCOME_DIALOG)) {
			SettingsLauncherDialogFragment dialog = 
				SettingsLauncherDialogFragment.getInstance(true);
			dialog.show(getSupportFragmentManager(), TAG_WELCOME_DIALOG);
		}
	}
	
	private void showNoAreaDialog() {
		if(null == getSupportFragmentManager().findFragmentByTag(TAG_NO_AREA_DIALOG)) {
			SettingsLauncherDialogFragment dialog = 
				SettingsLauncherDialogFragment.getInstance(false);
			dialog.show(getSupportFragmentManager(), TAG_NO_AREA_DIALOG);
		}		
	}
	
	/*
	 * Actionbar���[�h�̐ݒ�B
	 * �s�v�Ȃ�����𖳂������߂ɁAonCreate�ŌĂԂ��ƁB
	 */
	private void setupActionbarMode() {
		// Honeycomb�ȍ~
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setDisplayShowTitleEnabled(false);
		} 
		// GB��������AActionBar���option menu��ݒ肷��B
		else {
			ImageButton radikoLaunch = (ImageButton) findViewById(R.id.mainactivity_action_bar_launch_radiko);
			radikoLaunch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchRadiko();
				}
			});
				
			ImageButton settingLaunch = (ImageButton) findViewById(R.id.mainactivity_action_bar_launch_settings);
			settingLaunch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchSettings();
				}
			});
		}
	}
	
	@SuppressLint("NewApi")
	private void setupActionbarDropdown() {

		// Adapter�̐ݒ�
        mSpinnerAdapter = new StationSpinnerAdapter(this);
		
		// Honeycomb�ȍ~�B
		if(SugorokuonUtils.isHigherThanGingerBread()) {
	        // Actionbar��drow down��I����������Listener��ݒ肷��B
	        OnNavigationListener listener = new OnNavigationListener() {
	        	// �����Drop down select�́Aadapter�̐ݒ��ɏ���ɔ��s�����B
	        	// �c���؂�ւ����ɕs�v��event�𔭐������Ȃ����߂ɁA�����select��e���d�g�݂�����B
	        	private boolean mFirstSelectHappened = false;
	        	@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					if(mFirstSelectHappened) {
						if(itemPosition == (mSpinnerAdapter.getCount() - 1)) {
							launchAppcAdvertisement();
						} else {
							mDataViewFlow.setStationFocusIndex(SugorokuonActivity.this, itemPosition);
						}
					} else {
						mFirstSelectHappened = true;
					}
					return true;
				}
			};
			getActionBar().setListNavigationCallbacks(mSpinnerAdapter, listener);
	        
		}
		// Gingerbread�ȑO�B
		else {
			Spinner stationList = (Spinner) findViewById(R.id.mainactivity_action_bar_spinner);
			if(null != stationList) {
				stationList.setAdapter(mSpinnerAdapter);
				stationList.setOnItemSelectedListener(new OnItemSelectedListener() {
		        	private boolean mFirstSelectHappened = false;
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if(mFirstSelectHappened) {
							if(position == (mSpinnerAdapter.getCount() - 1)) {
								launchAppcAdvertisement();
							} else {
								mDataViewFlow.setStationFocusIndex(SugorokuonActivity.this, position);
							}
						} else {
							mFirstSelectHappened = true;
						}
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						mDataViewFlow.setStationFocusIndex(SugorokuonActivity.this, 0);
					}
				});
			}
		}

        setActionbarFocusIndex(true);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setActionbarFocusIndex(boolean forceSet) {

		StationDataManager stationMgr = mDataViewFlow.getStationDataMgr();
		
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			ActionBar actionBar = getActionBar();
			int currentActionBarIndex = actionBar.getSelectedNavigationIndex();
			
			// forceSet��true�̎��͋����I�Ɂu�I�X�X���v�ɓ��Ă�
			if(forceSet) {
				actionBar.setSelectedNavigationItem(0);
			}
			// focus�̐ݒ� (�O��focus�ƕς���Ă��邩�`�F�b�N���āA�ς���Ă����̂ݕς���悤�ɂ���)
			else if(null != stationMgr && 
					stationMgr.getFocusedIndex() != currentActionBarIndex) {
				actionBar.setSelectedNavigationItem(stationMgr.getFocusedIndex());
			}
		} else {
			Spinner spinner = (Spinner) findViewById(R.id.mainactivity_action_bar_spinner);
			if(null != spinner) {
				int currentActionBarIndex = spinner.getSelectedItemPosition();

				// forceSet��true�̎��͋����I�Ɂu�I�X�X���v�ɓ��Ă�
				if(forceSet) {
					spinner.setSelection(0);
				}
				// focus�̐ݒ� (�O��focus�ƕς���Ă��邩�`�F�b�N���āA�ς���Ă����̂ݕς���悤�ɂ���)
				else if(null != stationMgr && 
						stationMgr.getFocusedIndex() != currentActionBarIndex) {
					spinner.setSelection(stationMgr.getFocusedIndex());
				}
			}
		}
	}

    /*
     * ViewFlow�̏��������ʒm���󂯂�B 
     */
	public void onViewFlowEvent(ViewFlowEvent event) {
		switch(event) {
		case COMPLETE_DATA_UPDATECOMPLETE:
			mSpinnerAdapter.notifyDataSetChanged();
			setActionbarFocusIndex(true);			
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgress(int whatsRunning, int progress, int max) {
		// TODO Auto-generated method stub	
	}
	
	private void launchGooglePlayForFeedback() {
		Uri googleplayuri = Uri.parse(GOOGLE_PLAY_URL); 
		Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW,  googleplayuri);
		googlePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(googlePlayIntent);
	}
    
	// appC�̍L�����N��
	private void launchAppcAdvertisement() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("type", "pr_list");
		startActivity(intent);
	}
}
