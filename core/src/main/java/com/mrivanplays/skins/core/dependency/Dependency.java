package com.mrivanplays.skins.core.dependency;

import com.mrivanplays.skins.core.dependency.relocation.Relocation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * The dependencies used by Skins
 *
 * <p>Original code from: lucko/LuckPerms
 *
 * @author lucko
 */
public enum Dependency {
  ASM("org.ow2.asm", "asm", "7.1", "SrL6K20sycyx6qBeoynEB7R7E+0pFfYvjEuMyWJY1N4="),
  ASM_COMMONS("org.ow2.asm", "asm-commons", "7.1", "5VkEidjxmE2Fv+q9Oxc3TFnCiuCdSOxKDrvQGVns01g="),
  JAR_RELOCATOR("me.lucko", "jar-relocator", "1.4", "1RsiF3BiVztjlfTA+svDCuoDSGFuSpTZYHvUK8yBx8I="),
  CAFFEINE(
      "com{}github{}ben-manes{}caffeine",
      "caffeine",
      "2.8.4",
      "KV9YN5gQj6b507VJApJpPF5PkCon0DZqAi0T7Ln0lag=",
      Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")),
  BYTEBUDDY(
      "net{}bytebuddy",
      "byte-buddy",
      "1.10.9",
      "B7nKbi+XDLA/SyVlHfHy/OJx1JG0TgQJgniHeG9pLU0=",
      Relocation.of("bytebuddy", "net{}bytebuddy")),
  MARIADB_DRIVER(
      "org{}mariadb{}jdbc",
      "mariadb-java-client",
      "2.6.0",
      "fgiCp29Z7X38ULAJNsxZ1wFIVT2u3trSx/VCMxTlA6g=",
      Relocation.of("mariadb", "org{}mariadb{}jdbc")),
  MYSQL_DRIVER(
      "mysql",
      "mysql-connector-java",
      "5.1.48",
      "VuJsqqOCH1rkr0T5x09mz4uE6gFRatOAPLsOkEm27Kg=",
      Relocation.of("mysql", "com{}mysql")),
  POSTGRESQL_DRIVER(
      "org{}postgresql",
      "postgresql",
      "9.4.1212",
      "DLKhWL4xrPIY4KThjI89usaKO8NIBkaHc/xECUsMNl0=",
      Relocation.of("postgresql", "org{}postgresql")),
  H2_DRIVER(
      "com.h2database",
      "h2",
      // seems to be a compat bug in 1.4.200 with older dbs
      // see: https://github.com/h2database/h2database/issues/2078
      "1.4.199",
      "MSWhZ0O8a0z7thq7p4MgPx+2gjCqD9yXiY95b5ml1C4="
      // we don't apply relocations to h2 - it gets loaded via
      // an isolated classloader
      ),
  SQLITE_DRIVER(
      "org.xerial", "sqlite-jdbc", "3.28.0", "k3hOVtv1RiXgbJks+D9w6cG93Vxq0dPwEwjIex2WG2A="
      // we don't apply relocations to sqlite - it gets loaded via
      // an isolated classloader
      ),
  HIKARI(
      "com{}zaxxer",
      "HikariCP",
      "3.4.5",
      "i3MvlHBXDUqEHcHvbIJrWGl4sluoMHEv8fpZ3idd+mE=",
      Relocation.of("hikari", "com{}zaxxer{}hikari")),
  SLF4J_SIMPLE(
      "org.slf4j", "slf4j-simple", "1.7.30", "i5J5y/9rn4hZTvrjzwIDm2mVAw7sAj7UOSh0jEFnD+4="),
  SLF4J_API("org.slf4j", "slf4j-api", "1.7.30", "zboHlk0btAoHYUhcax6ML4/Z6x0ZxTkorA1/lRAQXFc="),
  MONGODB_DRIVER(
      "org.mongodb",
      "mongo-java-driver",
      "3.12.2",
      "eMxHcEtasb/ubFCv99kE5rVZMPGmBei674ZTdjYe58w=",
      Relocation.of("mongodb", "com{}mongodb"),
      Relocation.of("bson", "org{}bson"));

  private final String mavenRepoPath;
  private final String version;
  private final byte[] checksum;
  private final List<Relocation> relocations;
  private final DependencyRepository repository;

  private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

  Dependency(String groupId, String artifactId, String version, String checksum) {
    this(groupId, artifactId, version, checksum, new Relocation[0]);
  }

  Dependency(
      String groupId,
      String artifactId,
      String version,
      String checksum,
      DependencyRepository repository,
      Relocation... relocations) {
    this.mavenRepoPath =
        String.format(
            MAVEN_FORMAT,
            rewriteEscaping(groupId).replace(".", "/"),
            rewriteEscaping(artifactId),
            version,
            rewriteEscaping(artifactId),
            version);
    this.version = version;
    this.checksum = Base64.getDecoder().decode(checksum);
    this.repository = repository;
    this.relocations = Arrays.asList(relocations);
  }

  Dependency(
      String groupId,
      String artifactId,
      String version,
      String checksum,
      Relocation... relocations) {
    this(groupId, artifactId, version, checksum, DependencyRepository.MAVEN_CENTRAL, relocations);
  }

  private static String rewriteEscaping(String s) {
    return s.replace("{}", ".");
  }

  public String getFileName() {
    return name().toLowerCase().replace('_', '-') + "-" + this.version;
  }

  String getMavenRepoPath() {
    return this.mavenRepoPath;
  }

  public byte[] getChecksum() {
    return this.checksum;
  }

  public boolean checksumMatches(byte[] hash) {
    return Arrays.equals(this.checksum, hash);
  }

  public List<Relocation> getRelocations() {
    return this.relocations;
  }

  public DependencyRepository getRepository() {
    return repository;
  }

  /**
   * Creates a {@link MessageDigest} suitable for computing the checksums of dependencies.
   *
   * @return the digest
   */
  public static MessageDigest createDigest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /*
  public static void main(String[] args) {
    Dependency[] dependencies = values();
    java.util.concurrent.ExecutorService pool =
        java.util.concurrent.Executors.newCachedThreadPool();
    for (Dependency dependency : dependencies) {
      pool.submit(
          () -> {
            DependencyRepository repo = dependency.getRepository();
            try {
              byte[] hash = createDigest().digest(repo.downloadRaw(dependency));
              if (!dependency.checksumMatches(hash)) {
                System.out.println(
                    "NO MATCH - "
                        + repo.name()
                        + " - "
                        + dependency.name()
                        + ": "
                        + Base64.getEncoder().encodeToString(hash));
              } else {
                System.out.println("OK - " + repo.name() + " - " + dependency.name());
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    }
    pool.shutdown();
    try {
      pool.awaitTermination(1, java.util.concurrent.TimeUnit.HOURS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
   */
}
