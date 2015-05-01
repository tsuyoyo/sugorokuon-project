/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

/**
 * 検索のフィルタを掛けるためのクラス
 * getCondition関数で、SQLiteのwhere句を生成する
 *
 */
abstract class ProgramSearchFilter {

    class Condition {
        public final String where;

        public final String[] whereArgs;

        public Condition(String where, String[] whereArgs) {
            this.where = where;
            this.whereArgs = whereArgs;
        }
    }

    abstract Condition getCondition();

}
