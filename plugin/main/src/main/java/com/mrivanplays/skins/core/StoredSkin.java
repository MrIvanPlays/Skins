/*
    Copyright (C) 2019 Ivan Pekov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Skin;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StoredSkin {

    private Skin skin;
    private final List<String> acquirers;
    private final String configurationKey;
    private final String name;

    public StoredSkin(
            Skin skin,
            String configurationKey,
            String name
    ) {
        this(skin, new ArrayList<>(), configurationKey, name);
    }

    public StoredSkin(
            Skin skin,
            List<String> acquirers,
            String configurationKey,
            String name
    ) {
        this.skin = skin;
        this.acquirers = acquirers;
        this.configurationKey = configurationKey;
        this.name = name;
    }

    public String getName() { // owner name
        return name;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public List<String> getAcquirers() {
        return acquirers;
    }

    public void addAcquirer(UUID uuid) {
        if (!acquirers.contains(uuid.toString())) {
            acquirers.add(uuid.toString());
        }
    }

    public void removeAcquirer(UUID uuid) {
        if (acquirers.contains(uuid.toString())) {
            acquirers.remove(uuid.toString());
        }
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public StoredSkin duplicate() {
        return new StoredSkin(skin, acquirers, configurationKey, name);
    }
}
