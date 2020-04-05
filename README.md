[![Build Status](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.mrivanplays.com%2Fjob%2FSkins%2F&style=for-the-badge)](https://ci.mrivanplays.com/job/Skins/)
![license](https://img.shields.io/github/license/MrIvanPlays/Skins.svg?style=for-the-badge)
![issues](https://img.shields.io/github/issues/MrIvanPlays/Skins.svg?style=for-the-badge)
![api version](https://img.shields.io/maven-metadata/v?color=%20blue&label=latest%20version&metadataUrl=https%3A%2F%2Frepo.mrivanplays.com%2Frepository%2Fivan-snapshots%2Fcom%2Fmrivanplays%2Fskins-api%2Fmaven-metadata.xml&style=for-the-badge)
[![support](https://img.shields.io/discord/493674712334073878.svg?colorB=Blue&logo=discord&label=Support&style=for-the-badge)](https://mrivanplays.com/discord)
# Skins
Plugin created by MrIvanPlays

Supports 1.12.x, 1.13.2, 1.14.x, 1.15.x

Spigot page: click [here](https://www.spigotmc.org/resources/skins-1-13-2-1-14-x.70829/)

## Development builds
Usually development builds are not recommended for a big server as they may contain
a bunch of bugs. It is better to use a stable build which you can find at
[spigot](https://www.spigotmc.org/resources/skins-1-13-2-1-14-x.70829/)

Retrieving a development build can be easily done by cloning the repository, then
cd into the base directory, then run `mvn package`, retrieving jar from 
`plugin/bootstrap/target` or downloading jar from CI located [here](https://ci.mrivanplays.com/job/Skins/)

## Developer API

#### Installation:

NOTE: For older versions that 1.1.6-SNAPSHOT, you should use the old bintray repo url (https://dl.bintray.com/mrivanplaysbg/mrivanplays/) in order to achieve download.

Maven: 
```html
<repositories>
     <repository>
         <id>ivan</id>
         <url>https://repo.mrivanplays.com/repository/ivan/</url>
     </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>com.mrivanplays</groupId>
      <artifactId>skins-api</artifactId>
      <version>VERSION</version> <!-- Replace with latest version -->
      <scope>provided</scope>
    </dependency>
</dependencies>
```

Gradle:
```groovy
repositories {
  maven {
    url "https://repo.mrivanplays.com/repository/ivan/"
  }
}

dependencies {
  compileOnly 'com.mrivanplays:skins-api:VERSION' // replace version with latest version
}
```

#### Obtaining api instance
```java
public class MyMainClass extends JavaPlugin {

    private SkinsApi skins;

    @Override
    public void onEnable() {
        setupSkinsApi();
        if (!getServer().getPluginManager().isPluginEnabled(this)) {
            return;
        }
        // other code
    }

    private void setupSkinsApi() {
        if (getServer().getPluginManager().getPlugin("Skins") == null) {
            getLogger().severe("Skins plugin required, disabling!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        RegisteredServiceProvider<SkinsApi> provider = 
                getServer().getServicesManager().getRegistration(SkinsApi.class);
        this.skins = provider.getProvider();
    }
}
```
