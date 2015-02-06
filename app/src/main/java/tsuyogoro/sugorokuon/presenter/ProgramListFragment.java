/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 番組情報をlistで表示する箇所のfragment。
 *
 * @author Tsuyoyo
 *
 */
public class ProgramListFragment extends Fragment
        implements StationDataManager.IEventListener,
        ProgramDataManager.IEventListener, IViewFlowListener {

    private DataViewFlow mDataViewFlow;
    private ProgramListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataViewFlow = DataViewFlow.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(SugorokuonConst.LOGTAG, "+ ProgramListFragment:onCreateView");

        super.onCreateView(inflater, container, savedInstanceState);

        View body = inflater.inflate(R.layout.program_list_fragment, null);

        // 表示するためのデータがViewFlowの中にあるかどうかで表示を切り替える。
        if(0 >= mDataViewFlow.getProgramDataMgr().getFocusedProgramList().size()) {
            body.findViewById(R.id.program_list_no_data).setVisibility(View.VISIBLE);
            body.findViewById(R.id.program_list).setVisibility(View.GONE);
        } else {
            body.findViewById(R.id.program_list_no_data).setVisibility(View.GONE);
            body.findViewById(R.id.program_list).setVisibility(View.VISIBLE);
        }

        // Adapterの設定
        ListView progList = (ListView) body.findViewById(R.id.program_list);
        mAdapter = new ProgramListAdapter();
        inflateListItems();
        progList.setAdapter(mAdapter);

        // listをclickしたときの動作
        progList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                DataViewFlow.getInstance().getProgramDataMgr().setFocusedIndex(index);
            }
        });

        // Flick操作によって、番組表を前日/翌日に切り替える。
        progList.setOnTouchListener(
                new ProgramListFragmentFlickHandler(getActivity(), progList));

        Log.d(SugorokuonConst.LOGTAG, "- ProgramListFragment:onCreateView");

        return body;
    }

    @Override
    public void onPause() {
        super.onPause();

        // 自分が画面から消える時はlistener解除。
        DataViewFlow.getInstance().getProgramDataMgr().listeners.remove(this);
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        if(null != stationMgr) {
            DataViewFlow.getInstance().getStationDataMgr().listeners.remove(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // 自分が画面に表示される際にlistenerをset。
        DataViewFlow.getInstance().getProgramDataMgr().listeners.add(this);
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        if(null != stationMgr) {
            stationMgr.listeners.add(this);
        } else {
            // もしstationMgrがnullだったら、
            // DataViewFlowでのdataロード完了後にlistener登録を行う。
            // onViewFlowEventでCOMPLETEイベントを受け取るために、DataViewFlowへ登録を行う。
            DataViewFlow.getInstance().register(this);
        }

        // Focusを、前回当たっていたものへ戻す。
        setFocus();
    }

    @Override
    public void onStationIndexChanged(int newIndex) {
        // UIスレッドでViewが生成されることを保障するために、ここでProgramListのViewを生成する。
        // 参考：http://blogs.yahoo.co.jp/hiro5_188/20237842.html
        inflateListItems();

        // Listへ変更通知を送る。
        mAdapter.notifyDataSetChanged();

        // 今の時間のところにfocusを持っていく。
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                setFocusOnCurrentTime((ListView)(getView().findViewById(R.id.program_list)));
            }
        });

        // (メモ)
        // version1.5.5にて、
        // 今放送中の番組へfocusが行くようにしたが、(GBの)縦横切り替えの挙動が変わってしまった。
        // （これまでは、切り替え前の番組へfocusを移動 → 放送中の番組へ移動。になった）
        // 縦横切り替え（configuration change）であることを知る術はあるはずなので、
        // それが分かったら、↓を呼ぶフローを付け加える。
//		setFocus();
    }

    @Override
    public void onFocusedProgramIndexChanged(int newIndex) {
    }

    @Override
    public void onFocusedProgramDateChanged(Calendar newDate) {
        // listに表示するViewを更新し、adapterへ更新通知。
        inflateListItems();
        mAdapter.notifyDataSetChanged();
        setFocus();

        getView().findViewById(R.id.program_list_no_data).setVisibility(View.GONE);
        getView().findViewById(R.id.program_list).setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewFlowEvent(ViewFlowEvent event) {
        switch(event) {
            // 番組表更新前のonResumeではStationDataViewFlowが未生成で
            // listener登録が出来ていないので、番組表更新完了の通知をもらって、listener登録。
            case COMPLETE_DATA_UPDATECOMPLETE:
                StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
                stationMgr.listeners.add(this);

                // もう通知受け取る必要が無いので、外す。
                DataViewFlow.getInstance().unregister(this);

                // 初期focusの
                onStationIndexChanged(stationMgr.getFocusedIndex());
                break;
            default:
                // Nothing to do.
                break;
        }
    }

    @Override
    public void onProgress(int whatsRunning, int progress, int max) {
        // Nothing to do.
    }

    // 今の時間のところにfocusをあわせる。
    private void setFocusOnCurrentTime(ListView list) {
        ProgramDataManager progDataMgr = mDataViewFlow.getProgramDataMgr();
        if(0 < progDataMgr.getFocusedProgramList().size()) {
            Calendar now = Calendar.getInstance(Locale.JAPAN);

            ProgramDataManager progMgr = mDataViewFlow.getProgramDataMgr();
            List<Program> progs = progMgr.getFocusedProgramList();
            for(int i = 0; i < progs.size(); i++) {
                Program prog = progs.get(i);
                Calendar startTime = SugorokuonUtils.changeOnAirTimeToCalendar(prog.start);
                Calendar endTime = SugorokuonUtils.changeOnAirTimeToCalendar(prog.end);

                if(now.getTimeInMillis() >= startTime.getTimeInMillis()
                        && now.getTimeInMillis() <= endTime.getTimeInMillis()) {
                    list.invalidate();
                    list.setSelection(i);
                    progDataMgr.setFocusedIndex(i);
                }
            }
        }
    }

    private void setFocus() {
        if(0 < mDataViewFlow.getProgramDataMgr().getFocusedProgramList().size()) {
            ListView progList = (ListView) getView().findViewById(R.id.program_list);
            progList.invalidate();
            progList.setSelection(mDataViewFlow.getProgramDataMgr().getFocusedIndex());
        }
    }

    private void inflateListItems() {
        mAdapter.listItems.clear();

        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        if(null != stationMgr) {
            ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();
            List<Program> progs = progMgr.getFocusedProgramList();
            for(Program prog : progs) {
                mAdapter.listItems.add(getView(prog, stationMgr.getFocusedIndex()));
            }
        }

    }

    public View getView(Program prog, int stationIndex) {
        View item = null;

        // オススメかどうかで、読み込むlayoutが違う。
        if(0 == stationIndex) {
            item = createItemForRecommendProgram(prog, R.layout.program_list_item_recommend);
        } else {
            item = createItemForTimeTable(prog, R.layout.program_list_item);
        }

        return item;
    }

    private View createItemForTimeTable(Program program, int layoutId) {
        // Itemをinflate
        LayoutInflater inflater = getLayoutInflater(getArguments());
        View itemView = inflater.inflate(layoutId, null);

        // OnAirの時間を埋める
        SimpleDateFormat formatStart = new SimpleDateFormat(
                getString(R.string.onair_start_time), Locale.US);
        SimpleDateFormat formatEnd = new SimpleDateFormat(
                getString(R.string.onair_end_time), Locale.US);

        long startTimeInMillis =
                SugorokuonUtils.changeOnAirTimeToCalendar(program.start).getTimeInMillis();
        long endTimeInMillis =
                SugorokuonUtils.changeOnAirTimeToCalendar(program.end).getTimeInMillis();

        TextView startTime = (TextView) itemView.findViewById(R.id.program_list_item_starttime);
        TextView endTime = (TextView) itemView.findViewById(R.id.program_list_item_endtime);

        startTime.setText(formatStart.format(new Date(startTimeInMillis)));
        endTime.setText(formatEnd.format(new Date(endTimeInMillis)));

        // Title
        TextView title = (TextView) itemView.findViewById(R.id.program_list_item_title);
        title.setText(program.title);

        // パーソナリティ
        TextView per = (TextView) itemView.findViewById(R.id.program_list_item_personality);
        per.setText(program.personalities);

        return itemView;
    }

    private View createItemForRecommendProgram(Program program, int layoutId) {
        // 基本的にはtime tableのitemと共通。
        View itemView = createItemForTimeTable(program, layoutId);

        // onAirの日付
        SimpleDateFormat formatDate = new SimpleDateFormat(
                getString(R.string.onair_date), Locale.US);
        long dateTimeInMillis =
                SugorokuonUtils.changeOnAirTimeToCalendar(program.start).getTimeInMillis();
        TextView date = (TextView) itemView.findViewById(R.id.program_list_item_date);
        date.setText(formatDate.format(new Date(dateTimeInMillis)));

        // 局のロゴ
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        ImageView logo = (ImageView) itemView.findViewById(R.id.program_list_item_station_logo);
        logo.setImageBitmap(stationMgr.getStationLogo(program.stationId));

        return itemView;
    }

    /*
     * Program情報を取ってくるためのadapter。
     */
    private class ProgramListAdapter extends BaseAdapter {

        public List<View> listItems = new ArrayList<View>();

        public int getCount() {
            return listItems.size();
        }

        public Object getItem(int position) {
            return listItems.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return listItems.get(position);
        }



    }

}