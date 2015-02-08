/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.database;

abstract class TableDefinerBase {

    protected static interface ColumnEnumBase {

        public String columnName();

        public String columnType();

    }

    // CREATA_TABLE...のSQL文を作る
    protected String createTableSql() {

        ColumnEnumBase[] columns = getFields();

        String statement = "CREATE_TABLE " + getTableName() + " (";

        for (int i = 0; i < columns.length; i++) {
            statement += columns[i].columnName() + " " + columns[i].columnType();
            if (i != columns.length - 1) {
                statement += ",";
            }
        }

        statement += ")";

        return statement;
    }

    abstract protected ColumnEnumBase[] getFields();

    abstract protected String getTableName();

}
