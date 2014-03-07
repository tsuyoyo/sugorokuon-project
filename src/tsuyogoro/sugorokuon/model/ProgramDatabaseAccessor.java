/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.datatype.OnedayTimetable;
import tsuyogoro.sugorokuon.datatype.Program;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 番組情報を格納したDBへのAccessor。
 * 
 * (メモ)
 * http://d.hatena.ne.jp/ukiki999/20100524/p1 によると、
 * DBのcloseはCursolに対してだけ呼ぶのが好ましいらしい。
 * 
 * @author Tsuyoyo
 *
 */
public class ProgramDatabaseAccessor extends SQLiteOpenHelper {

	private static final String DB_NAME = "programinfo";

	private static final int DB_VERSION = 1;

	private static final String TABLE_NAME = "programtable";
	
	private static class Column {
		static final String STATION_ID 		= "stationid";
		static final String START_TIME 		= "starttime";
		static final String END_TIME 		= "endtime";
		static final String TITLE 			= "title";
		static final String SUBTITLE 		= "subtitle";
		static final String PERSONALITIES 	= "personalities";
		static final String DESCRIPTION 	= "description";
		static final String INFO 			= "info";
		static final String URL 			= "url";
		static final String YEAR			= "year";
		static final String MONTH			= "month";
		static final String DATE			= "date";
		static final String ISRECOMMEND     = "isrecommend";
		
		static final String[] allColumns() {
			return new String[] {STATION_ID, START_TIME, END_TIME, TITLE,
					SUBTITLE, PERSONALITIES, DESCRIPTION, INFO, URL, 
					ISRECOMMEND };
		}
	}
	
	private static final String CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " ("
		+ Column.STATION_ID		+ " TEXT," // Station ID
		+ Column.START_TIME		+ " TEXT," // 番組の開始時刻。yyyymmddhhmmssのフォーマット。
		+ Column.END_TIME   	+ " TEXT," // 番組の終了時刻。（同上）
		+ Column.TITLE			+ " TEXT," // 番組のタイトル。
		+ Column.SUBTITLE		+ " TEXT," // 番組のサブタイトル。
		+ Column.PERSONALITIES  + " TEXT," // パーソナリティ。
		+ Column.DESCRIPTION	+ " TEXT,"  // description（空っぽのことが多い）。
		+ Column.INFO			+ " TEXT,"  // html形式の番組info。
		+ Column.URL			+ " TEXT,"  // 番組サイトのURL
		+ Column.YEAR			+ " INTEGER," // onAirのyear
		+ Column.MONTH			+ " INTEGER," // onAirのmonth (-1)
		+ Column.DATE			+ " INTEGER," // onAirのdate
		+ Column.ISRECOMMEND    + " INTEGER," // Recommendかどうか(0:false, 1:true)
		// StationIdとstart timeがあれば、番組を一意に特定できるのでprimary key。
		+ "PRIMARY KEY (" + Column.STATION_ID + "," + Column.START_TIME + ")"
		+ ")";

//	private ProgramInfoCacheManager mInfoCacheMgr;

	/**
	 * その番組がオススメかどうかを判定するクラスのinterface。
	 * 
	 * @author Tsuyoyo
	 *
	 */
	public interface IRecommender {
		/**
		 * targetの番組がオススメ番組かどうかを判定する。
		 * 
		 * @param target
		 * @return
		 */
		public boolean isRecommend(Program target);
	}
	
	/**
	 * コンストラクタ。
	 * 
	 * @param context Contextインスタンス。
	 */
	public ProgramDatabaseAccessor(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
//		mInfoCacheMgr = new ProgramInfoCacheManager();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTable(db);
        onCreate(db);
	}
	
	private void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//		mInfoCacheMgr.clearInfoCache();
	}
	
	/**
	 * 1日分のProgramデータをDBに格納する。
	 * データをDownloadした後、このmethodを使って格納していく事。
	 * 
	 * @param timeTable 1日分の番組データ。
	 */
	public void insertOnedayTimetable(OnedayTimetable timeTable, IRecommender recommender) {
		SQLiteDatabase db = getWritableDatabase();
		for(Program p : timeTable.programs) {
			ContentValues cv = new ContentValues();
			
	        cv.put(Column.STATION_ID,	p.stationId);
	        cv.put(Column.START_TIME, 	p.start);
	        cv.put(Column.END_TIME, 	p.end);
	        cv.put(Column.TITLE, 		p.title);
	        cv.put(Column.SUBTITLE, 	p.subtitle);
	        cv.put(Column.PERSONALITIES,p.personalities);
	        cv.put(Column.DESCRIPTION, 	p.description);
	        cv.put(Column.INFO,		 	p.info); //mInfoCacheMgr.createInfoCache(p));
	        cv.put(Column.URL, 			p.url);
	        cv.put(Column.YEAR, 	timeTable.date.get(Calendar.YEAR));
	        cv.put(Column.MONTH,	timeTable.date.get(Calendar.MONTH));
	        cv.put(Column.DATE, 	timeTable.date.get(Calendar.DATE));
	        
	        if(recommender.isRecommend(p)) {
	        	cv.put(Column.ISRECOMMEND, 1);
	        } else {
	        	cv.put(Column.ISRECOMMEND, 0);
	        }
	        
	        db.insert(TABLE_NAME, null, cv);
		}
		db.close();
	}
	
	/**
	 * Programデータが格納されているDBを削除する（番組情報更新の際に使う）。
	 *  
	 * @param db DBインスタンス。
	 * 
	 */
	public void clearAllProgramData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}
	
	/**
	 * 1日分の番組データをDBから取る。
	 * 
	 * @param date データが欲しい日付。
	 * @param stationId 局のID。
	 * @return 
	 * 
	 */
	public OnedayTimetable getTimetable(Calendar date, String stationId) {
		
		SQLiteDatabase db = getReadableDatabase();
		
		String selection = Column.STATION_ID + "=? AND " 
						 + Column.YEAR 		 + "=? AND "
						 + Column.MONTH 	 + "=? AND "
						 + Column.DATE 		 + "=?";
		
		String selectionArgs[] = {stationId, 
				Integer.toString(date.get(Calendar.YEAR)),
				Integer.toString(date.get(Calendar.MONTH)), 
				Integer.toString(date.get(Calendar.DATE))};
		
		Cursor c = db.query(TABLE_NAME, Column.allColumns(), selection,
				selectionArgs, null, null, Column.START_TIME);

		OnedayTimetable timeTable = new OnedayTimetable(date, stationId);
		
		c.moveToFirst();
		for(int i=0; i < c.getCount(); i++) {
			timeTable.programs.add(createProgram(c));
			c.moveToNext();
		}
		
		c.close();
		db.close();
		
		return timeTable;
	}

	/**
	 * 新しいRecommendのfilterを渡して、DBのオススメかどうかのflagを更新していく。
	 * 
	 * @param recommender Recommendの番組かどうかを判定を行う。
	 */
	public void updateRecommendPrograms(IRecommender recommender) {
		SQLiteDatabase db = getWritableDatabase();
		
		Cursor c = db.query(TABLE_NAME, Column.allColumns(), 
					null, null, null, null, null);
		c.moveToFirst();
		for(int i=0; i < c.getCount(); i++) {
			Program p = createProgram(c);
			ContentValues cv = new ContentValues();
			if(recommender.isRecommend(p)) {
				cv.put(Column.ISRECOMMEND, 1);
			} else {
				cv.put(Column.ISRECOMMEND, 0);
			}
			db.update(TABLE_NAME, cv, 
					Column.STATION_ID + "= ? AND " + Column.START_TIME + "= ?", 
					new String[]{p.stationId, p.start});
			c.moveToNext();
		}
		
		c.close();
		db.close();
	}
	
	/**
	 * オススメの番組をDBから取る。
	 * 
	 * @param db DBインスタンス。
	 * @return DBから１週間分のオススメ番組を取る。
	 */
	public List<Program> getAllRecommendPrograms() {
		SQLiteDatabase db = getReadableDatabase();
		List<Program> progs = new ArrayList<Program>();
		Cursor c = db.query(TABLE_NAME, Column.allColumns(), 
				Column.ISRECOMMEND + "= ?", new String[]{"1"}, null, null, 
				Column.START_TIME); // 放送開始順にソート。
		c.moveToFirst();
		for(int i=0; i < c.getCount(); i++) {
			progs.add(createProgram(c));
			c.moveToNext();
		}
		c.close();
		db.close();
		return progs;
	}
	
	private Program createProgram(Cursor c) {
		Program.Builder builder = new Program.Builder();

		builder.stationId = c.getString(0);
		builder.start = c.getString(1);
		builder.end = c.getString(2);
		builder.title = c.getString(3);
		builder.subtitle = c.getString(4);
		builder.personalities = c.getString(5);
		builder.description = c.getString(6);
		builder.info = c.getString(7); //mInfoCacheMgr.readInfoCache(c.getString(7));
		builder.url = c.getString(8);
		
		return builder.create();
	}
	
}
