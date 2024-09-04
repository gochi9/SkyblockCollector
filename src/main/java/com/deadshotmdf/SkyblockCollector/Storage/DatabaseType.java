package com.deadshotmdf.SkyblockCollector.Storage;

public enum DatabaseType {

    MYSQL(0),
    SQLITE(1),
    MONGODB(2),
    MARIADB(3);

    private final int value;

    private DatabaseType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DatabaseType getDatabaseType(int value) {
        switch (value) {
            case 0:
                return MYSQL;
            case 2:
                return MONGODB;
            case 3:
                return MARIADB;
            default:
                return SQLITE;
        }
    }

}
