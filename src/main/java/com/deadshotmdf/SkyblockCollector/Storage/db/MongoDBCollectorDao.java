package com.deadshotmdf.SkyblockCollector.Storage.db;

import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Utils.Utils;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.bukkit.Location;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBCollectorDao extends CollectorDao {

    private MongoDatabase database;

    public MongoDBCollectorDao(String connectionString, String dbName) {
        MongoClient client = MongoClients.create(connectionString);
        this.database = client.getDatabase(dbName);
    }

    //Note to self
    //Currently this implementation of MongoDB does not remove collectors that have been broken
    //This in unusable at the current time
    @Override
    public void saveAllCollectors(Map<Location, Collector> collectors) {
        MongoCollection<Document> collection = database.getCollection("collectors");
        List<WriteModel<Document>> writes = new LinkedList<>();
        for (Map.Entry<Location, Collector> entry : collectors.entrySet()) {
            Collector collector = entry.getValue();
            collector.sellAll();
            collector.removeHologram();
            Document doc = new Document("loc", Utils.locationToString(entry.getKey()))
                    .append("owner", collector.getOwner().toString())
                    .append("totalMoneySold", collector.getTotalMoneySold())
                    .append("totalItemsSold", collector.getTotalItemsSold())
                    .append("radiusLevel", collector.getRadiusLevel())
                    .append("sellMultiplier", collector.getSellMultiplier())
                    .append("canLoadChunk", collector.isCanLoadChunk());
            writes.add(new ReplaceOneModel<>(eq("loc", Utils.locationToString(entry.getKey())), doc, new ReplaceOptions().upsert(true)));
        }
        collection.bulkWrite(writes);
    }

    @Override
    public HashMap<Location, Collector> loadAllCollectors() {
        HashMap<Location, Collector> collectors = new HashMap<>();
        MongoCollection<Document> collection = database.getCollection("collectors");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Location loc = Utils.locationFromString(doc.getString("loc"));
                UUID uuid = UUID.fromString(doc.getString("owner"));
                collectors.put(loc, new Collector(uuid,
                        doc.getInteger("totalMoneySold"),
                        doc.getInteger("totalItemsSold"),
                        doc.getInteger("radiusLevel"),
                        doc.getDouble("sellMultiplier"),
                        doc.getBoolean("canLoadChunk"),
                        loc));
            }
        }
        return collectors;
    }
}