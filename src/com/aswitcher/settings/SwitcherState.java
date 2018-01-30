package com.aswitcher.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "HybrisAddonStorefrontSwitcherApplicationSettings",
        storages = {@Storage(
                file = "$APP_CONFIG$/hybrisAddonStorefrontSwitcher.xml"
        )}
)
public final class SwitcherState implements PersistentStateComponent<SwitcherApplicationSettings> {

    private SwitcherApplicationSettings switcherApplicationSettings = new SwitcherApplicationSettings();

    private SwitcherState() {
    }

    @NotNull
    public static SwitcherState getInstance() {
        return ServiceManager.getService(SwitcherState.class);
    }

    @Nullable
    @Override
    public SwitcherApplicationSettings getState() {
        return switcherApplicationSettings;
    }

    @Override
    public void loadState(SwitcherApplicationSettings state) {
        XmlSerializerUtil.copyBean(state, this.switcherApplicationSettings);
    }

}
