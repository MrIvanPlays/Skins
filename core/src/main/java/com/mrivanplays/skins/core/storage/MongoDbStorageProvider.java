package com.mrivanplays.skins.core.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsConfiguration.DatabaseCredentials;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;

public class MongoDbStorageProvider implements StorageProvider {

  private final SkinsConfiguration configuration;
  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;

  public MongoDbStorageProvider(SkinsConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void connect() {
    DatabaseCredentials credentials = configuration.getStorageCredentials();
    MongoCredential credential = null;
    if (credentials.getUsername() != null && !credentials.getUsername().isEmpty()) {
      credential =
          MongoCredential.createCredential(
              credentials.getUsername(),
              credentials.getDatabase(),
              credentials.getPassword() == null || credentials.getPassword().isEmpty()
                  ? new char[0]
                  : credentials.getPassword().toCharArray());
    }

    ServerAddress address = new ServerAddress(credentials.getAddress(), credentials.getPort());
    if (credential == null) {
      mongoClient = new MongoClient(address);
    } else {
      mongoClient = new MongoClient(address, credential, MongoClientOptions.builder().build());
    }

    this.mongoDatabase = mongoClient.getDatabase(credentials.getDatabase());
  }

  @Override
  public void storeSkin(StoredSkin skin) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_storage");

    Document doc =
        new Document()
            .append("ownerName", skin.getOwnerName())
            .append("ownerUUID", skin.getSkin().getOwner().toString())
            .append("texture", skin.getSkin().getTexture())
            .append("signature", skin.getSkin().getSignature());

    Document search = new Document("ownerUUID", skin.getSkin().getOwner().toString());
    Document found = collection.find(search).first();

    if (found != null) {
      collection.replaceOne(found, doc);
    } else {
      collection.insertOne(doc);
    }
  }

  @Override
  public void setAcquirer(UUID uuid, UUID skinAcquired) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_acquired");

    Document doc =
        new Document()
            .append("acquirer_uuid", uuid.toString())
            .append("acquired_uuid", skinAcquired.toString());

    Document search = new Document("acquirer_uuid", uuid.toString());
    Document found = collection.find(search).first();

    if (found != null) {
      collection.replaceOne(found, doc);
    } else {
      collection.insertOne(doc);
    }
  }

  @Override
  public StoredSkin findByName(String name) {
    return getSkin(new Document().append("ownerName", name));
  }

  @Override
  public StoredSkin find(UUID uuid) {
    return getSkin(new Document().append("ownerUUID", uuid.toString()));
  }

  private StoredSkin getSkin(Document search) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_storage");
    return getSkinByFound(collection.find(search).first());
  }

  private StoredSkin getSkinByFound(Document found) {
    if (found != null) {
      StoredSkin storedSkin =
          new StoredSkin(
              new Skin(
                  UUID.fromString(found.getString("ownerUUID")),
                  found.getString("texture"),
                  found.getString("signature")),
              found.getString("ownerName"));
      MongoCollection<Document> acquired = mongoDatabase.getCollection("skins_acquired");
      Document acquiredSearch =
          new Document().append("acquired_uuid", found.getString("ownerUUID"));
      MongoCursor<Document> iterator = acquired.find(acquiredSearch).cursor();
      while (iterator.hasNext()) {
        Document foundAcquirer = iterator.next();
        storedSkin.addAcquirer(UUID.fromString(foundAcquirer.getString("acquirer_uuid")));
      }
      return storedSkin;
    }
    return null;
  }

  @Override
  public StoredSkin acquired(UUID uuid) {
    MongoCollection<Document> acquired = mongoDatabase.getCollection("skins_acquired");
    Document search = new Document().append("acquirer_uuid", uuid.toString());
    Document found = acquired.find(search).first();
    if (found != null) {
      return find(UUID.fromString(found.getString("acquired_uuid")));
    }
    return null;
  }

  @Override
  public List<StoredSkin> all() {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_storage");
    List<StoredSkin> list = new ArrayList<>();
    for (Document next : collection.find()) {
      list.add(getSkinByFound(next));
    }
    return list;
  }

  @Override
  public void closeConnection() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }
}
