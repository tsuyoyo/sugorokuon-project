/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

abstract class BaseTableDefiner {

    protected static interface ColumnEnumBase {

        public String columnName();

        public String columnType();

    }

    // CREATA_TABLE...のSQL文を作る
    String createTableSql() {

        ColumnEnumBase[] columns = getFields();

        String statement = "CREATE TABLE " + getTableName() + " (";

        for (int i = 0; i < columns.length; i++) {
            statement += columns[i].columnName() + " " + columns[i].columnType();
            if (i != columns.length - 1) {
                statement += ",";
            }
        }

        String primaryKeySql = primaryKeySql();
        if (null != primaryKeySql) {
            statement += "," + primaryKeySql;
        }

        String uniqueDefSql = uniqueSql();
        if (null != uniqueDefSql) {
            statement += "," + uniqueDefSql;
        }

        statement += ")";

        return statement;
    }

    abstract protected ColumnEnumBase[] getFields();

    abstract protected String getTableName();

    // "PRIMARY KEY"の文が必要であればoverrideする
    protected String primaryKeySql() {
        return null;
    }

    // "UNIQUE"の文 (重複チェック) が必要であればoverrideする
    protected String uniqueSql() {
        return null;
    }
}
