package moe.plushie.rpg_framework.core.common.database.driver;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.SqlBuilder;

public class SQLiteBuilder extends SqlBuilder {

    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_TEXT = "TEXT";
    private static final String TYPE_BLOB = "BLOB";
    private static final String TYPE_REAL = "REAL";
    private static final String TYPE_NUMERIC = "NUMERIC";

    public String convertType(ISqlBulder.DataType dataType, int size) {
        switch (dataType) {
        case INT:
            return TYPE_INTEGER;
        case DATETIME:
            break;
        case LONGTEXT:
            return TYPE_TEXT;
        case TEXT:
            break;
        case VARCHAR:
            return TYPE_TEXT;
        }
        return dataType.toString();
    }

    @Override
    public ISqlBulderCreateTable createTable(String name) {
        return new SQLiteBuilderCreateTable(name);
    }

    public class SQLiteBuilderCreateTable extends SqlBuilderCreateTable {

        private final ArrayList<SQLiteBuilder.SQLiteBuilderColumn> columns = new ArrayList<SQLiteBuilder.SQLiteBuilderColumn>();

        public SQLiteBuilderCreateTable(String name) {
            super(name);
        }

        @Override
        public ISqlBulder.ISqlBulderColumn addColumn(String name, DataType dataTypes) {
            SQLiteBuilder.SQLiteBuilderColumn column = new SQLiteBuilder.SQLiteBuilderColumn(name, dataTypes);
            columns.add(column);
            return column;
        }

        @Override
        public String build() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ");
            if (ifNotExists) {
                sb.append("IF NOT EXISTS ");
            }
            sb.append("`");
            sb.append(name);
            sb.append("`");
            sb.append(" (");
            for (int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i).build());
                if (i < columns.size() - 1) {
                    sb.append(",");
                }
            }
            if (primaryKey != null) {
                sb.append(",PRIMARY KEY (`");
                sb.append(primaryKey);
                sb.append("`)");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public class SQLiteBuilderColumn extends SqlBuilderColumn {

        public SQLiteBuilderColumn(String name, DataType type) {
            super(name, type);
        }

        public String build() {
            StringBuilder sb = new StringBuilder();
            sb.append("`");
            sb.append(name);
            sb.append("`");
            sb.append(" ");
            sb.append(convertType(type, size));
            if (unsigned) {
                sb.append(" UNSIGNED");
            }
            if (notNull) {
                sb.append(" NOT NULL");
            }
            if (autoIncrement) {
                sb.append(" AUTOINCREMENT");
            }
            if (defaultValue != null) {
                sb.append(" DEFAULT ");
                sb.append(defaultValue);
            }
            return sb.toString();
        }
    }
}
