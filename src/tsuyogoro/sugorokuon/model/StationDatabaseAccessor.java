/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.datatype.Station;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Station�����i�[���Ă���database�ւ�accessor�N���X�B
 * 
 * (����1)
 * http://d.hatena.ne.jp/kuwalab/20110212/1297508786 ����������������₷���B
 * 
 * (����2)
 * http://d.hatena.ne.jp/ukiki999/20100524/p1 �ɂ��ƁA
 * DB��close��Cursol�ɑ΂��Ă����ĂԂ̂��D�܂����炵���B
 * 
 * @author Tsuyoyo
 *
 */
public class StationDatabaseAccessor extends SQLiteOpenHelper {
		
	private static final String DB_NAME = "stationinfo";

	private static final int DB_VERSION = 1;

	private static final String TABLE_NAME = "stationtable";
	
	private static class Column {
		static final String ID 			= "id";
		static final String NAME 		= "name";
		static final String ASCII_NAME 	= "ascii_name";
		static final String SITE_URL 	= "site_url";
		static final String LOGO_URL 	= "logo_url";
		static final String BANNER_URL 	= "banner_url";
		static final String LOGO_CACHE 	= "logo_cache";
		
		static final String[] allColumns() {
			return new String[] {ID, NAME, ASCII_NAME, SITE_URL, 
					LOGO_URL, BANNER_URL, LOGO_CACHE};
		}
	}
	
	private static final String CREATE_TABLE = 
			"CREATE TABLE " + TABLE_NAME + " ("
			+ Column.ID 		+ " TEXT PRIMARY KEY," // Station ID
			+ Column.NAME 		+ " TEXT," // Station name (��F��������)
			+ Column.ASCII_NAME + " TEXT," // ASCII name (��FBUNKA_HOSO�j
			+ Column.SITE_URL 	+ " TEXT," // �ǂ�Site URL
			+ Column.LOGO_URL 	+ " TEXT," // �ǂ�Logo��URL
			+ Column.BANNER_URL + " TEXT," // �ǂ�Banner��URL
			+ Column.LOGO_CACHE + " TEXT"  // Logo��local�L���b�V���̃t�@�C���p�X
			+ ")";

	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param context
	 */
	public StationDatabaseAccessor(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

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
	}

	/**
	 * Station�f�[�^��DB�Ɋi�[����B
	 * �f�[�^��Download������A����method���g���Ċi�[���Ă������B
	 * 
	 * @param stations Download����parse����station�̃f�[�^�B
	 */
	public void insertStationData(List<Station> stations) {
		SQLiteDatabase db = getWritableDatabase();
		for(Station s : stations) {
			ContentValues cv = new ContentValues();
	        cv.put(Column.ID, 			s.id);
	        cv.put(Column.NAME, 		s.name);
	        cv.put(Column.ASCII_NAME, 	s.ascii_name);
	        cv.put(Column.SITE_URL, 	s.siteUrl);
	        cv.put(Column.LOGO_URL, 	s.logoUrl);
	        cv.put(Column.BANNER_URL, 	s.bannerUrl);
	        cv.put(Column.LOGO_CACHE, 	"");
	        
	        db.insert(TABLE_NAME, "", cv);
		}
		db.close();
	}
	
	/**
	 * Station�f�[�^���i�[����Ă���DB(Table)���폜����i�ԑg���X�V�̍ۂɎg���j�B
	 * 
	 */
	public void clearStationData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}
	
	/**
	 * �SStation�̃f�[�^���擾�B
	 * 
	 * @return Station�f�[�^��������������list���Ԃ�B
	 */
	public List<Station> getStationData() {
		SQLiteDatabase db = getReadableDatabase();
		List<Station> stations = new ArrayList<Station>();
		
		// Station�̑S�Ă�row���擾�B
		Cursor c = db.query(TABLE_NAME, Column.allColumns(), 
				null, null, null, null, null);
		
		c.moveToFirst();		
		for (int i = 0; i < c.getCount(); i++) {
			// Station�f�[�^�������list�ɂ��܂��B
			Station station = createStation(c);
			stations.add(station);
			c.moveToNext();
		}
		c.close();
		db.close();
		return stations;
	}
	
	/**
	 * �w�肵��ID��Station�̃f�[�^���擾�B
	 * 
	 * @param stationId �f�[�^���~����station��ID�B
	 * @return Station�f�[�^������������null���Ԃ�B
	 */
	public Station getStationData(String stationId) {
		SQLiteDatabase db = getReadableDatabase();
		
		// Station�́AID��stationId�ƈ�v������̂�T���B
		Cursor c = db.query(TABLE_NAME, Column.allColumns(), 
				Column.ID + "= ? ", new String[]{stationId}, null, null, null);
		if(1 == c.getCount()) {
			c.close();
			db.close();
			return createStation(c);
		}
		c.close();
		db.close();
		return null;
	}
	
	/**
	 * �w�肵��ID��station�f�[�^�ɁALogo file�̏���ǉ�����B
	 * 
	 * @param stationId �X�V������station��ID�B
	 * @param filePath Logo�t�@�C����file path�B
	 * @return ������������true�A���s��������false�B
	 */
	public boolean updateLogoInfo(String stationId, String filePath) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Column.LOGO_CACHE, filePath);
		int rows = db.update(TABLE_NAME, cv, Column.ID + "=?", new String[]{stationId});
		db.close();
		if(1 == rows) {
			return true;
		} else {
			return false;
		}
		
	}

	private Station createStation(Cursor c) {
		Station.Builder builder = new Station.Builder();		
		builder.id 			= c.getString(0);
		builder.name 		= c.getString(1);
		builder.ascii_name 	= c.getString(2);
		builder.siteUrl  	= c.getString(3);
		builder.logoUrl 	= c.getString(4);
		builder.bannerUrl 	= c.getString(5);
		builder.logoCachePath = c.getString(6);
		return builder.create();
	}
	
}
