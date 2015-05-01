/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

class ProgramIconCacheTableDefiner extends BaseTableDefiner {

    public static final String TABLE_NAME = "program_icon_cache";

    public enum TableColumns implements ColumnEnumBase {

        // 元画像のURL
        ORIGINAL_URL("original_url", "TEXT"),

        // cacheが置かれている端末ローカルのパス
        CACHE_PATH("cache_path", "TEXT");

        private String name;

        private String type;

        @Override
        public String columnName() {
            return name;
        }

        @Override
        public String columnType() {
            return type;
        }

        TableColumns(String name, String type) {
            this.name = name;
            this.type = type;
        }

    }

    @Override
    protected ColumnEnumBase[] getFields() {
        return TableColumns.values();
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

}
