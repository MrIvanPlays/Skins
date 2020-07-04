package com.mrivanplays.skins.core.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.config.SkinsConfiguration;
import com.mrivanplays.skins.core.config.SkinsConfiguration.DatabaseCredentials;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
            .append("ownerUUID", skin.getOwnerUUID().toString())
            .append("texture", skin.getSkin().getTexture())
            .append("signature", skin.getSkin().getSignature())
            .append(
                "acquirers",
                skin.getAcquirers().stream().map(UUID::toString).collect(Collectors.toList()));

    Document search = new Document("ownerUUID", skin.getOwnerUUID().toString());
    Document found = collection.find(search).first();

    if (found != null) {
      collection.replaceOne(found, doc);
    } else {
      collection.insertOne(doc);
    }
  }

  @Override
  public StoredSkin findByName(String name) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_storage");
    Document search = new Document().append("ownerName", name);
    return getStoredSkin(collection, search);
  }

  @Override
  public StoredSkin find(UUID uuid) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_storage");
    Document search = new Document().append("ownerUUID", uuid.toString());
    return getStoredSkin(collection, search);
  }

  private StoredSkin getStoredSkin(MongoCollection<Document> collection, Document search) {
    Document found = collection.find(search).first();
    if (found != null) {
      StoredSkin storedSkin =
          new StoredSkin(
              new Skin(found.getString("texture"), found.getString("signature")),
              found.getString("ownerName"),
              UUID.fromString(found.getString("ownerUUID")));
      found.getList("acquirers", String.class).stream()
          .map(UUID::fromString)
          .forEach(storedSkin::addAcquirer);
      return storedSkin;
    }
    return null;
  }

  @Override
  public StoredSkin acquired(UUID uuid) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("skins_storage");
    for (Document next : collection.find()) {
      List<UUID> acquirerList =
          next.getList("acquirers", String.class).stream()
              .map(UUID::fromString)
              .collect(Collectors.toList());
      if (acquirerList.contains(uuid)) {
        StoredSkin storedSkin =
            new StoredSkin(
                new Skin(next.getString("texture"), next.getString("signature")),
                next.getString("ownerName"),
                UUID.fromString(next.getString("ownerUUID")));
        storedSkin.getAcquirers().addAll(acquirerList);
        return storedSkin;
      }
    }
    return null;
  }

  @Override
  public void closeConnection() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }
}
