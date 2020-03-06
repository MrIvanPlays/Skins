[![Build Status](https://ci.mrivanplays.com/buildStatus/icon?job=Skins)](https://ci.mrivanplays.com/job/Skins/)
![license](https://img.shields.io/github/license/MrIvanPlays/Skins.svg?style=for-the-badge)
![issues](https://img.shields.io/github/issues/MrIvanPlays/Skins.svg?style=for-the-badge)
![api version](https://img.shields.io/bintray/v/mrivanplaysbg/mrivanplays/Skins.svg?style=for-the-badge)
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
`plugin/bootstrap/target`

## Developer API

#### Installation with maven:
```html
<repositories>
    <repository>
      <id>mrivanplays</id>
      <url>https://dl.bintray.com/mrivanplaysbg/mrivanplays/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>com.mrivanplays</groupId>
      <artifactId>skins-api</artifactId>
      <version>LATEST</version>
      <scope>provided</scope>
    </dependency>
</dependencies>
```

I don't know gradle so I can't show you that

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
