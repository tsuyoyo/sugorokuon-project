/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.database;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.datatype.Station;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Station情報を格納しているdatabaseへのaccessorクラス。
 *
 * (メモ1)
 * http://d.hatena.ne.jp/kuwalab/20110212/1297508786 ここが凄く分かりやすい。
 *
 * (メモ2)
 * http://d.hatena.ne.jp/ukiki999/20100524/p1 によると、
 * DBのcloseはCursolに対してだけ呼ぶのが好ましいらしい。
 *
 * @author Tsuyoyo
 *
 */
public class StationDbOpenHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "stationinfo";

	private static final int DB_VERSION = 1;

    private List<TableDefinerBase> mTableDefiners = new ArrayList<TableDefinerBase>();

    /**
     * コンストラクタ
     *
     * @param context
     */
	public StationDbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

        // このOpenHelperで取り扱うTableDefinerをここでaddしていく
        mTableDefiners.add(new StationTableDefiner());
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        for (TableDefinerBase definer : mTableDefiners) {
            db.execSQL(definer.createTableSql());
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTable(db);
        onCreate(db);
	}
	
	private void dropTable(SQLiteDatabase db) {
        for (TableDefinerBase definer : mTableDefiners) {
            db.execSQL("DROP TABLE IF EXISTS " + definer.getTableName());
        }
	}

    /**
     * StationデータをDBに格納する。
     * データをDownloadした後、このmethodを使って格納していく事。
     *
     * @param stations Downloadしてparseしたstationのデータ。
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
     * Stationデータが格納されているDB(Table)を削除する（番組情報更新の際に使う）。
     *
     */
	public void clearStationData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}



    /**
     * 指定したIDのstationデータに、Logo fileの情報を追加する。
     *
     * @param stationId 更新したいstationのID。
     * @param filePath Logoファイルのfile path。
     * @return 成功だったらtrue、失敗だったらfalse。
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

	
}
