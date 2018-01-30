package com.aswitcher.settings;

import com.aswitcher.view.ConfigurationForm;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SwitcherConfigurable implements Configurable {

    private final ConfigurationForm configurationForm = new ConfigurationForm();

    @Nls
    @Override
    public String getDisplayName() {
        return "Hybris Addon-Storefront Switcher";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "Hybris Addon-Storefront Switcher Plugin Configuration";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return configurationForm.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return configurationForm.isModified(SwitcherState.getInstance().getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        SwitcherApplicationSettings settings = getSettings();
        settings.setStorefront(configurationForm.getStorefront());
        settings.setAddons(configurationForm.getAddons());
    }

    private SwitcherApplicationSettings getSettings() throws ConfigurationException {
        SwitcherApplicationSettings settings = SwitcherState.getInstance().getState();
        if (settings == null) {
            throw new ConfigurationException("Cannot obtain settings");
        }
        return settings;
    }

}
