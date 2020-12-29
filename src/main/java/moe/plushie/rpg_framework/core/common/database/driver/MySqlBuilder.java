package moe.plushie.rpg_framework.core.common.database.driver;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.SqlBuilder;

public class MySqlBuilder extends SqlBuilder  {
    
    public String convertType(ISqlBulder.DataType dataType, int size) {
        switch (dataType) {
        case INT:
            break;
        case DATETIME:
            break;
        case LONGTEXT:
            break;
        case TEXT:
            break;
        case VARCHAR:
            return "VARCHAR (" + size + ")";
        case BOOLEAN:
            break;
        }
        return dataType.toString();
    }
    
    @Override
    public ISqlBulderCreateTable createTable(String name) {
        return new MySqlBuilderCreateTable(name);
    }

    @Override
    public ISqlBulderAlterTable alterTable(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public class MySqlBuilderCreateTable extends SqlBuilderCreateTable {

        private final ArrayList<MySqlBuilder.MySqlBuilderColumn> columns = new ArrayList<MySqlBuilder.MySqlBuilderColumn>();

        public MySqlBuilderCreateTable(String name) {
            super(name);
        }

        @Override
        public MySqlBuilder.MySqlBuilderColumn addColumn(String name, DataType dataTypes) {
            MySqlBuilder.MySqlBuilderColumn column = new MySqlBuilder.MySqlBuilderColumn(name, dataTypes);
            columns.add(column);
            return column;
        }
        
        @Override
        public void setPrimaryKey(String key) {
            super.setPrimaryKey(key);
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).getName().equals(key)) {
                    columns.get(i).setPrimaryKey(true);
                    break;
                }
            }
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
    
    public class MySqlBuilderColumn extends SqlBuilderColumn {

        public MySqlBuilderColumn(String name, DataType type) {
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
            if (autoIncrement) {
                if (primaryKey) {
                    // sb.append(" PRIMARY KEY");
                }
                sb.append(" AUTOINCREMENT");
            }
            if (notNull) {
                sb.append(" NOT NULL");
            }
            if (defaultValue != null) {
                sb.append(" DEFAULT ");
                sb.append(defaultValue);
            }
            return sb.toString();
        }
        
        public String getName() {
            return name;
        }
        
        public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }
    }
}
