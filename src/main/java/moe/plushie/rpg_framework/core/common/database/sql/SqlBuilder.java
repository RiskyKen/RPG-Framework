package moe.plushie.rpg_framework.core.common.database.sql;

public abstract class SqlBuilder implements ISqlBulder {

    public abstract class SqlBuilderCreateTable implements ISqlBulderCreateTable {

        protected final String name;
        protected boolean ifNotExists = false;
        protected String primaryKey = null;

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
            // TODO Auto-generated method stub
        }
    }

    public abstract class SqlBuilderColumn implements ISqlBulderColumn {

        protected final String name;
        protected final ISqlBulder.DataType type;
        protected int size = 0;
        protected boolean unsigned = false;
        protected boolean notNull = false;
        protected boolean autoIncrement = false;
        protected String defaultValue = null;

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
