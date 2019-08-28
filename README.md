# Skins
Plugin created by MrIvanPlays

Supports 1.13.2 & 1.14.x

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

## Getting a jar
(27.08.2019)
The plugin's gonna be soon published at spigot, we need only to add one more thing, but
if you want jar right now, you can clone the repo, cd into the basedir, run `mvn package`, then
get a jar from `plugin/bootstrap/target`