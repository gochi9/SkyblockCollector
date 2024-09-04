package com.deadshotmdf.SkyblockCollector.Storage.db;

import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Storage.DatabaseType;
import com.deadshotmdf.SkyblockCollector.Utils.Utils;

import org.bukkit.Location;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectorDaoImpl extends CollectorDao {

    private final String databaseUrl;
    private final String saveQuery;
    private final DatabaseType dbType;

    public CollectorDaoImpl(String databaseUrl, String saveQuery, DatabaseType dbType, String database) {
        this.databaseUrl = databaseUrl;
        this.saveQuery = saveQuery;
        this.dbType = dbType;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            if (dbType == DatabaseType.MYSQL || dbType == DatabaseType.MARIADB) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + database);
                stmt.execute("USE " + database);
            }

            String sql = "CREATE TABLE IF NOT EXISTS collectors (" +
                    "loc VARCHAR(36) PRIMARY KEY, " +
                    "owner VARCHAR(36), " +
                    "totalMoneySold INT, " +
                    "totalItemsSold INT, " +
                    "radiusLevel INT, " +
                    "sellMultiplier DOUBLE, " +
                    "canLoadChunk TINYINT(1))" ;

            stmt.execute(dbType != DatabaseType.SQLITE ? sql : sql.replace("VARCHAR(36)", "TEXT").replace("INT", "INTEGER").replace("TINYINT(1)", "INTEGER").replace("DOUBLE", "REAL"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    @Override
    public void saveAllCollectors(Map<Location, Collector> collectors) {
        String locations = collectors.keySet().stream()
                .map(loc -> String.format("'%s'", Utils.locationToString(loc)))
                .collect(Collectors.joining(", "));

        String deleteQuery;
        switch (dbType) {
            case MYSQL:
            case MARIADB:
                deleteQuery = "DELETE FROM collectors WHERE loc NOT IN (SELECT loc FROM (SELECT loc FROM DUAL WHERE loc IN (" + locations + ")) AS temp)";
                break;
            case SQLITE:
                deleteQuery = "DELETE FROM collectors WHERE loc NOT IN (" + locations + ")";
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }

        try (Connection conn = getConnection();
             PreparedStatement saveStmt = conn.prepareStatement(saveQuery)) {

            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(deleteQuery);
            }

            // Insert or update collectors
            for (Map.Entry<Location, Collector> entry : collectors.entrySet()) {
                Collector collector = entry.getValue();
                collector.sellAll();
                collector.removeHologram();

                saveStmt.setString(1, Utils.locationToString(entry.getKey()));
                saveStmt.setString(2, collector.getOwner().toString());
                saveStmt.setDouble(3, collector.getTotalMoneySold());
                saveStmt.setInt(4, collector.getTotalItemsSold());
                saveStmt.setInt(5, collector.getRadiusLevel());
                saveStmt.setDouble(6, collector.getSellMultiplier());
                saveStmt.setBoolean(7, collector.isCanLoadChunk());
                saveStmt.addBatch();
            }
            saveStmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Location, Collector> loadAllCollectors() {
        Map<Location, Collector> collectors = new HashMap<>();
        String sql = "SELECT * FROM collectors";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Location loc = Utils.locationFromString(rs.getString("loc"));
                UUID uuid = UUID.fromString(rs.getString("owner"));
                int money = rs.getInt("totalMoneySold");
                int items = rs.getInt("totalItemsSold");
                int radiusLevel = rs.getInt("radiusLevel");
                double sellMultiplier = rs.getDouble("sellMultiplier");
                boolean canLoadChunk = rs.getBoolean("canLoadChunk");
                collectors.put(loc, new Collector(uuid, money, items, radiusLevel, sellMultiplier, canLoadChunk, loc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collectors;
    }
}
