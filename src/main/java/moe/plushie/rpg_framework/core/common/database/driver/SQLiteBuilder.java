package moe.plushie.rpg_framework.core.common.database.driver;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.RPGFramework;
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
            return "VARCHAR (" + size + ")";
        }
        return dataType.toString();
    }

    @Override
    public ISqlBulderCreateTable createTable(String name) {
        return new SQLiteBuilderCreateTable(name);
    }

    @Override
    public ISqlBulderAlterTable alterTable(String name) {
        return new SQLiteBuilderAlterTable(name);
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
            sb.append("'");
            sb.append(name);
            sb.append("'");
            sb.append(" (");
            for (int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i).build());
                if (i < columns.size() - 1) {
                    sb.append(",");
                }
            }
            if (primaryKey != null) {
                sb.append(",PRIMARY KEY ('");
                sb.append(primaryKey);
                sb.append("')");
            }
            for (int i = 0; i < indexs.size(); i++) {
                Index index = indexs.get(i);
                if (index.isUnique()) {
                    sb.append("UNIQUE ");
                }
                sb.append("INDEX ");
                sb.append("'");
                sb.append(index.getName());
                sb.append("' (");
                for (int j = 0; j < index.getKeys().length; j++) {
                    sb.append("'");
                    sb.append(index.getKeys()[j]);
                    sb.append("'");
                    if (j < index.getKeys().length - 1) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                if (i < indexs.size() - 1) {
                    sb.append(" ");
                }
                /// "CREATE INDEX IF NOT EXISTS idx_item_reg ON " + TABLE_ITEMS_NAME + " (reg_name, meta, count, nbt_whitelist)";
            }
            sb.append(")");
            RPGFramework.getLogger().info("Building SQL: " + sb.toString());
            return sb.toString();
        }
    }

    public class SQLiteBuilderAlterTable implements ISqlBulder.ISqlBulderAlterTable {

        private final ArrayList<SQLiteBuilder.SQLiteBuilderColumn> columns = new ArrayList<SQLiteBuilder.SQLiteBuilderColumn>();

        public SQLiteBuilderAlterTable(String name) {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public ISqlBulderColumn addColumn(String name, DataType dataTypes) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void dropColumn(String name) {
            // TODO Auto-generated method stub
        }

        @Override
        public ISqlBulderColumn modifyColumn(String name, DataType dataTypes) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String build() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public class SQLiteBuilderColumn extends SqlBuilderColumn {

        public SQLiteBuilderColumn(String name, DataType type) {
            super(name, type);
        }

        public String build() {
            StringBuilder sb = new StringBuilder();
            sb.append("'");
            sb.append(name);
            sb.append("'");
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
