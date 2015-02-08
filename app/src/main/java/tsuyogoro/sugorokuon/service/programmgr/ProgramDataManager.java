/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service.programmgr;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.datatype.OnedayTimetable;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.database.ProgramDatabaseAccessor;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;

public class ProgramDataManager {



    /**
     * ProgramDataManagerからのEventを受け取るためのlistener。
     *
     * @author Tsuyoyo
     *
     */
    public static interface IEventListener {
        /**
         * focusされているindexが変わった時に通知を受ける。
         * 通知はUIスレッドに届く。
         *
         * @param newIndex
         */
        public void onFocusedProgramIndexChanged(int newIndex);

        /**
         * focusされている日付が変わった時に通知を受ける。
         * 通知はUIスレッドに届く。
         *
         * @param newIndex
         */
        public void onFocusedProgramDateChanged(Calendar newDate);
    }

    // 現在focusの当たっている番組list。
    private List<Program> mPrograms = new ArrayList<Program>();

    /**
     * ProgramDataManagerからの通知を受ける人たちのList。
     */
    public List<IEventListener> listeners = new ArrayList<IEventListener>();

    /**
     *  現在focusの当たっているindex
     */
    private int mFocusedIndex = 0;

    /**
     *  現在focusの当たっている日付 （Recommendがfocusedだと、ここがnullになる）
     */
    private Calendar mFocusedDate;

    /**
     * 現在フォーカスの当たっている番組リストを返却。
     * ViewFlowをloadしてから使うこと。
     *
     * @return
     */
    public List<Program> getFocusedProgramList() {
        return mPrograms;
    }

    /**
     * DBにstoreされている、すべてのオススメ番組情報をDBからメンバ変数へloadする。
     * この後、getPrograms()でその情報を取得可能。
     *
     * @param context
     * @return
     */
    public void loadRecommendPrograms(Context context) {
        mPrograms = loadRecommendProgramsFromDB(context);
        setFocusedIndex(0);
        setFocusedDate(null);
    }

    /**
     * 今Focusが当たっている次の日の番組情報を、DBからメンバ変数にloadする。。
     * この後、getPrograms()でその情報を取得可能。
     * 今focusの当たっている曜日が日曜なら（翌日は無いので）何もしない（その場合はfalseが返る）。
     *
     * @param context
     * @param stationId
     * @return 切り替え処理が行われたらtrue。
     */
    public boolean loadNextdayTimetable(Context context, String stationId) {
        boolean res = false;
        Calendar focusedDate = getFocusedCalendar();
        if(Calendar.SUNDAY != focusedDate.get(Calendar.DAY_OF_WEEK)) {
            focusedDate.add(Calendar.DATE, 1);
            loadOnedayTimetable(context, focusedDate, stationId);
            res = true;
        }
        return res;
    }

    /**
     * 今Focusが当たっている前の日の番組情報を、DBからメンバ変数にloadする。。
     * この後、getPrograms()でその情報を取得可能。
     * 今focusの当たっている曜日が月曜なら（前日は無いので）何もしない（その場合はfalseが返る）。
     *
     * @param context
     * @param stationId
     * @return 切り替え処理が行われたらtrue。
     */
    public boolean loadPreviousdayTimetable(Context context, String stationId) {
        boolean res = false;
        Calendar focusedDate = getFocusedCalendar();
        if(Calendar.MONDAY != focusedDate.get(Calendar.DAY_OF_WEEK)) {
            focusedDate.add(Calendar.DATE, -1);
            loadOnedayTimetable(context, focusedDate, stationId);
            res = true;
        }
        return res;
    }

    /**
     * 指定した日の、指定した局の１日分の番組情報を、DBからメンバ変数にloadする。。
     * この後、getPrograms()でその情報を取得可能。
     *
     * @param context
     * @param date 		YY/MM/DDまで使う。
     * @param stationId
     */
    public void loadOnedayTimetable(Context context,
                                    Calendar date, String stationId) {
        ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
        OnedayTimetable onedayData = db.getTimetable(date, stationId);
        mPrograms = onedayData.programs;
        setFocusedIndex(0);
        setFocusedDate(date);
    }

    /**
     * DBにStoreされているオススメ番組で、now時点でまだ放送されていないものをload。
     *
     * @param now 	YY/MM/DD/HH/MM の情報までを使う。
     * @return 無かったら空っぽのリストが返る。
     */
    public void loadRecommendProgramsNotOnAirYet(Context context,
                                                 Calendar now) {
        // 秒をそろえて平等に。
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        mPrograms.clear();
        List<Program> recommends = loadRecommendProgramsFromDB(context);
        for(Program p : recommends) {
            Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
            if(now.getTimeInMillis() < onAirTime.getTimeInMillis()) {
                mPrograms.add(p);
            }
        }
        setFocusedIndex(0);
        setFocusedDate(null);
    }

    /**
     * DBにstoreされているオススメ番組から、指定した日時時間のものを取得。
     * そろそろ放送or放送開始のNotificationが来た時に使う想定。
     *
     * @param date	YY/MM/DD/HH/MM まで利用。
     * @return start時刻が、dateに一致するもののリスト。無かったら空のリスト。
     */
    public List<Program> getRecommendPrograms(Context context, Calendar date) {
        List<Program> results = new ArrayList<Program>();
        List<Program> recommends = loadRecommendProgramsFromDB(context);

        // 一致させないといけないので、YY/MM/DD/HH/MMをそれぞれ比較。
        for(Program p : recommends) {
            Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
            if((date.get(Calendar.YEAR)   == onAirTime.get(Calendar.YEAR))
                    && (date.get(Calendar.MONTH)  == onAirTime.get(Calendar.MONTH))
                    && (date.get(Calendar.DATE)   == onAirTime.get(Calendar.DATE))
                    && (date.get(Calendar.HOUR_OF_DAY) == onAirTime.get(Calendar.HOUR_OF_DAY))
                    && (date.get(Calendar.MINUTE) == onAirTime.get(Calendar.MINUTE))) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * DBにstoreされているオススメ番組で、まだon airしていない番組のlistを取得。
     * on air順にソートされて結果が返される。
     *
     * @param context
     * @return
     */
    public List<Program> getRecommendProgramsBaforeOnAir(Context context) {
        // recommendsは最初から時間でソートされている。
        List<Program> recommends = loadRecommendProgramsFromDB(context);

        Calendar now = Calendar.getInstance(Locale.JAPAN);
        while(recommends.size() > 0) {
            Program p = recommends.get(0);

            // nowよりonAirTimeが前だったら、recommendsからremoveしていく。
            // onAirTimeがnowより後ろになったところでbreak。
            Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
            if(onAirTime.getTimeInMillis() < now.getTimeInMillis()) {
                recommends.remove(p);
            } else {
                break;
            }
        }
        return recommends;
    }

    /**
     * DBのrecommend flagを更新する。 オススメワードの設定が変わった時に使うこと。
     *
     * @param context
     */
    public void updateRecommendPrograms(Context context) {
        ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
        db.updateRecommendPrograms(new KeywordRecommender(context));
    }

    /**
     * Listで、ユーザータップによってfocusが変わったら呼ぶ。
     *
     * @param newIndex
     */
    public void setFocusedIndex(int newIndex) {
        // TODO : Listが空っぽの時は通知しない

        mFocusedIndex = newIndex;

        // Mainスレッドへ、その変更通知を送る。
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(IEventListener listener : listeners) {
                    listener.onFocusedProgramIndexChanged(mFocusedIndex);
                }
            }
        });

    }

    /**
     * 現在番組List上で、どこにfocusが当たっているかを返す。
     *
     * @return
     */
    public int getFocusedIndex() {
        return mFocusedIndex;
    }

    /**
     * 現在、何日の番組にfocusが当たっているかを返す。
     * Recommendがfocusedだと、ここがnullになる。
     * 日付切り替えの部品に用いられる想定。
     *
     * @return
     */
    public Calendar getFocusedCalendar() {
        return mFocusedDate;
    }

    /*
     * Dateの更新は、基本的にloadXXXを呼んだときに行われる。
     */
    private void setFocusedDate(Calendar newDate) {
        mFocusedDate = newDate;

        // Mainスレッドへ、その変更通知を送る。
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(IEventListener listener : listeners) {
                    listener.onFocusedProgramDateChanged(mFocusedDate);
                }
            }
        });
    }



    private List<Program> loadRecommendProgramsFromDB(Context context) {
        ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
        return db.getAllRecommendPrograms();
    }
}