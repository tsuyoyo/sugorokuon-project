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
 * �ԑg�����i�[����DB�ւ�Accessor�B
 * 
 * (����)
 * http://d.hatena.ne.jp/ukiki999/20100524/p1 �ɂ��ƁA
 * DB��close��Cursol�ɑ΂��Ă����ĂԂ̂��D�܂����炵���B
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
		+ Column.START_TIME		+ " TEXT," // �ԑg�̊J�n�����Byyyymmddhhmmss�̃t�H�[�}�b�g�B
		+ Column.END_TIME   	+ " TEXT," // �ԑg�̏I�������B�i����j
		+ Column.TITLE			+ " TEXT," // �ԑg�̃^�C�g���B
		+ Column.SUBTITLE		+ " TEXT," // �ԑg�̃T�u�^�C�g���B
		+ Column.PERSONALITIES  + " TEXT," // �p�[�\�i���e�B�B
		+ Column.DESCRIPTION	+ " TEXT,"  // description�i����ۂ̂��Ƃ������j�B
		+ Column.INFO			+ " TEXT,"  // html�`���̔ԑginfo�B
		+ Column.URL			+ " TEXT,"  // �ԑg�T�C�g��URL
		+ Column.YEAR			+ " INTEGER," // onAir��year
		+ Column.MONTH			+ " INTEGER," // onAir��month (-1)
		+ Column.DATE			+ " INTEGER," // onAir��date
		+ Column.ISRECOMMEND    + " INTEGER," // Recommend���ǂ���(0:false, 1:true)
		// StationId��start time������΁A�ԑg����ӂɓ���ł���̂�primary key�B
		+ "PRIMARY KEY (" + Column.STATION_ID + "," + Column.START_TIME + ")"
		+ ")";

//	private ProgramInfoCacheManager mInfoCacheMgr;

	/**
	 * ���̔ԑg���I�X�X�����ǂ����𔻒肷��N���X��interface�B
	 * 
	 * @author Tsuyoyo
	 *
	 */
	public interface IRecommender {
		/**
		 * target�̔ԑg���I�X�X���ԑg���ǂ����𔻒肷��B
		 * 
		 * @param target
		 * @return
		 */
		public boolean isRecommend(Program target);
	}
	
	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param context Context�C���X�^���X�B
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
	 * 1������Program�f�[�^��DB�Ɋi�[����B
	 * �f�[�^��Download������A����method���g���Ċi�[���Ă������B
	 * 
	 * @param timeTable 1�����̔ԑg�f�[�^�B
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
	 * Program�f�[�^���i�[����Ă���DB���폜����i�ԑg���X�V�̍ۂɎg���j�B
	 *  
	 * @param db DB�C���X�^���X�B
	 * 
	 */
	public void clearAllProgramData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}
	
	/**
	 * 1�����̔ԑg�f�[�^��DB������B
	 * 
	 * @param date �f�[�^���~�������t�B
	 * @param stationId �ǂ�ID�B
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
	 * �V����Recommend��filter��n���āADB�̃I�X�X�����ǂ�����flag���X�V���Ă����B
	 * 
	 * @param recommender Recommend�̔ԑg���ǂ����𔻒���s���B
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
	 * �I�X�X���̔ԑg��DB������B
	 * 
	 * @param db DB�C���X�^���X�B
	 * @return DB����P�T�ԕ��̃I�X�X���ԑg�����B
	 */
	public List<Program> getAllRecommendPrograms() {
		SQLiteDatabase db = getReadableDatabase();
		List<Program> progs = new ArrayList<Program>();
		Cursor c = db.query(TABLE_NAME, Column.allColumns(), 
				Column.ISRECOMMEND + "= ?", new String[]{"1"}, null, null, 
				Column.START_TIME); // �����J�n���Ƀ\�[�g�B
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
