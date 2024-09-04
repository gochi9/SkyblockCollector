package com.deadshotmdf.SkyblockCollector.Storage;

import com.deadshotmdf.SkyblockCollector.Storage.db.CollectorDao;
import com.deadshotmdf.SkyblockCollector.Storage.db.CollectorDaoImpl;
import com.deadshotmdf.SkyblockCollector.Storage.db.MongoDBCollectorDao;
import org.bukkit.configuration.file.FileConfiguration;

public class CollectorDaoFactory {

    private final CollectorDao storage;

    public CollectorDaoFactory(FileConfiguration config, String folderPath) {
        int value = config.getInt("db.type");
        String url = config.getString("db.url");
        String port = config.getString("db.port");
        String database = config.getString("db.database");
        String username = config.getString("db.username");
        String password = config.getString("db.password");


        this.storage = createCollectorDao(DatabaseType.getDatabaseType(value) ,url, port, username, password, database, folderPath);
    }

    public CollectorDao getStorage(){
        return storage;
    }

    private CollectorDao createCollectorDao(DatabaseType dbType, String url, String port, String username, String password, String database, String folderPath) {
        switch (dbType) {
            case MYSQL:
            case MARIADB:
                return new CollectorDaoImpl(
                        String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s", dbType == DatabaseType.MARIADB ? "mariadb" : "mysql", url, port, database, username, password),
                        "INSERT INTO collectors (loc, owner, totalMoneySold, totalItemsSold, radiusLevel, sellMultiplier, canLoadChunk) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE owner=?, totalMoneySold=?, totalItemsSold=?, radiusLevel=?, sellMultiplier=?, canLoadChunk=?",
                        dbType,
                        database
                );
            case SQLITE:
                return new CollectorDaoImpl(
                        String.format("jdbc:sqlite:%s/%s.db", folderPath, database),
                        "INSERT OR REPLACE INTO collectors (loc, owner, totalMoneySold, totalItemsSold, radiusLevel, sellMultiplier, canLoadChunk) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        dbType,
                        database
                );
            case MONGODB:
                return new MongoDBCollectorDao(
                        String.format("mongodb://%s:%s", url, port),
                        database
                );
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
    }
}
