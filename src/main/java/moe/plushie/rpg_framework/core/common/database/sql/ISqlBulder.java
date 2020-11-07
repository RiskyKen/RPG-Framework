package moe.plushie.rpg_framework.core.common.database.sql;

public interface ISqlBulder {

    
    
    public ISqlBulder.ISqlBulderCreateTable createTable(String name);
    
    
    /**
     * "CREATE TABLE IF NOT EXISTS
     * DEFAULT
     * UPDATE
     * SET
     * SELECT
     * FROM
     * WHERE
     * INSERT INTO
     * VALUES
     */
    
    
    /**
     * bigint
     * int
     * smallint
     * tinyint
     * bit
     * DATETIME
     * 
     */

    public interface ISqlBulderCreateTable {

        public ISqlBulder.ISqlBulderColumn addColumn(String name, DataType dataTypes);

        public void ifNotExists(boolean value);

        public void setPrimaryKey(String key);

        public void addKey(String name, boolean unique, String... keys);

        public String build();
    }

    public interface ISqlBulderColumn {
        
        public ISqlBulder.ISqlBulderColumn setSize(int size);

        public ISqlBulder.ISqlBulderColumn setCharacterSet(String charset, String collation);

        public ISqlBulder.ISqlBulderColumn setUnsigned(boolean value);

        public ISqlBulder.ISqlBulderColumn setNotNull(boolean value);

        public ISqlBulder.ISqlBulderColumn setAutoIncrement(boolean value);

        public ISqlBulder.ISqlBulderColumn setDefault(String value);
    }

    public enum DataType {
        INT, VARCHAR, TEXT, DATETIME, LONGTEXT
    }
}
