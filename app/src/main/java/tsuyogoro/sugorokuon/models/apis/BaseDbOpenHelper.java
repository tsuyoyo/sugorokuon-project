/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

abstract class BaseDbOpenHelper extends SQLiteOpenHelper {

    /**
     * 継承クラスで利用するTableDefinerを作らせる
     *
     * @return TableDefinerのlist (nullを返しちゃダメ)
     */
    abstract List<BaseTableDefiner> getTableDefiners();

    protected BaseDbOpenHelper(String dbName, int dbVersion, Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<BaseTableDefiner> definers = getTableDefiners();
        for (BaseTableDefiner definer : definers) {
            db.execSQL(definer.createTableSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
        onCreate(db);
    }

    private void dropTable(SQLiteDatabase db) {
        List<BaseTableDefiner> definers = getTableDefiners();
        for (BaseTableDefiner definer : definers) {
            db.execSQL("DROP TABLE IF EXISTS " + definer.getTableName());
        }
    }

}
