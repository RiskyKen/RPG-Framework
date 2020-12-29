package moe.plushie.rpg_framework.core.common.database.sql;

import java.util.ArrayList;

public abstract class SqlBuilder implements ISqlBulder {

    public abstract class SqlBuilderCreateTable implements ISqlBulder.ISqlBulderCreateTable {

        protected final String name;
        protected boolean ifNotExists = false;
        protected String primaryKey = null;
        protected ArrayList<Index> indexs = new ArrayList<SqlBuilder.SqlBuilderCreateTable.Index>();

        public SqlBuilderCreateTable(String name) {
            this.name = name;
        }

        @Override
        public void ifNotExists(boolean value) {
            this.ifNotExists = value;
        }

        @Override
        public void setPrimaryKey(String key) {
            this.primaryKey = key;
        }

        @Override
        public void addKey(String name, boolean unique, String... keys) {
            indexs.add(new Index(name, unique, keys));
        }

        public class Index {

            private final String name;
            private final boolean unique;
            private final String[] keys;

            public Index(String name, boolean unique, String[] keys) {
                this.name = name;
                this.unique = unique;
                this.keys = keys;
            }

            public String getName() {
                return name;
            }

            public boolean isUnique() {
                return unique;
            }

            public String[] getKeys() {
                return keys;
            }
        }
    }

    public abstract class SqlBulderAlterTable implements ISqlBulder.ISqlBulderAlterTable {

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
    }

    public abstract class SqlBuilderColumn implements ISqlBulder.ISqlBulderColumn {

        protected final String name;
        protected final ISqlBulder.DataType type;
        protected int size = 0;
        protected boolean unsigned = false;
        protected boolean notNull = false;
        protected boolean autoIncrement = false;
        protected String defaultValue = null;
        protected boolean primaryKey = false;

        public SqlBuilderColumn(String name, ISqlBulder.DataType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public ISqlBulder.ISqlBulderColumn setSize(int size) {
            this.size = size;
            return this;
        }

        @Override
        public ISqlBulder.ISqlBulderColumn setCharacterSet(String charset, String collation) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public ISqlBulder.ISqlBulderColumn setUnsigned(boolean value) {
            this.unsigned = value;
            return this;
        }

        @Override
        public ISqlBulder.ISqlBulderColumn setNotNull(boolean value) {
            this.notNull = value;
            return this;
        }

        @Override
        public ISqlBulder.ISqlBulderColumn setAutoIncrement(boolean value) {
            this.autoIncrement = value;
            return this;
        }

        @Override
        public ISqlBulder.ISqlBulderColumn setDefault(String value) {
            this.defaultValue = value;
            return this;
        }
    }
}
