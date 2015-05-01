/**
 * Copyright (c) 
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

/**
 * Keywordを与えて、検索のフィルタを掛けるためのクラス
 * キーワードを使ってSQLiteのwhere句を生成する
 *
 */
public class ProgramSearchKeywordFilter extends ProgramSearchFilter {

	private String[] mKeyWords;

	public ProgramSearchKeywordFilter(String[] keyWords) {
		mKeyWords = keyWords;
	}

    @Override
    Condition getCondition() {

        // 検索するcolumn
        ProgramTableDefiner.ProgramTableColumn[] targetColumns =
                new ProgramTableDefiner.ProgramTableColumn[] {
                        ProgramTableDefiner.ProgramTableColumn.TITLE,
                        ProgramTableDefiner.ProgramTableColumn.SUBTITLE,
                        ProgramTableDefiner.ProgramTableColumn.PERSONALITIES,
                        ProgramTableDefiner.ProgramTableColumn.DESCRIPTION,
                        ProgramTableDefiner.ProgramTableColumn.INFO
        };

        String where = "";
        String[] whereArgs = new String[mKeyWords.length * targetColumns.length];

        for (int i = 0; i < mKeyWords.length; i++) {

            where += "(";

            // LIKE文と、その引数 (キーワード) を設定
            for (int c = 0; c < targetColumns.length; c++) {
                // (Memo 1 : Androidでのlikeはハマるらしい)
                // http://d.hatena.ne.jp/Kyakujin/20130318/1363598748
                // (Memo 2: SQLiteのLIKEは大文字小文字は区別しないらしい)
                // http://d.hatena.ne.jp/sutara_lumpur/20120818/1345280287
                where += targetColumns[c].columnName() + " LIKE '%' || ? || '%'";

                whereArgs[i * targetColumns.length + c] = mKeyWords[i];

                if (c < targetColumns.length - 1) {
                    where += " OR ";
                }
            }

            where += ")";

            if (i < mKeyWords.length - 1) {
                where += " OR ";
            }
        }

        return new Condition(where, whereArgs);
    }

}
