package com.aswitcher.action;

import com.aswitcher.settings.SwitcherApplicationSettings;
import com.aswitcher.settings.SwitcherState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.apache.commons.lang.StringUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AddonStorefrontAction extends AnAction {

    private static final String ACCELERATOR_ADDON = "acceleratoraddon";
    private static final String ADDONS = "addons";

    private static final String PATH_SEPARATOR = "/";

    private static final String WEB_INF_REGEXP = "(WEB-INF" + PATH_SEPARATOR + "\\w+" + PATH_SEPARATOR + ")";
    private static final String UI_REGEXP = "(_ui" + PATH_SEPARATOR + ")";

    private static final String MESSAGE_CANNOT_FIND_SOURCE_FILE = "Cannot find source file";
    private static final String MESSAGE_CANNOT_OBTAIN_SETTINGS = "Cannot obtain settings";

    protected void createMessagePopup(String message, Project project) {
        JBPopupFactory factory = JBPopupFactory.getInstance();
        BalloonBuilder builder = factory.createHtmlTextBalloonBuilder(message, MessageType.INFO, null);
        Balloon balloon = builder.createBalloon();
        balloon.show(RelativePoint.getCenterOf(WindowManager.getInstance().getStatusBar(project).getComponent()), Balloon.Position.above);
    }

    protected String getSourceFilePath(AnActionEvent event) {
        VirtualFile vFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null) {
            throw new RuntimeException(MESSAGE_CANNOT_FIND_SOURCE_FILE);
        }
        return vFile.getPath();
    }

    protected String replacePath(String filePath) {
        if (isStorefront(filePath)) {
            return getAvailableAddon(filePath);
        }
        return getAvailableStorefront(filePath);
    }

    private String storefrontName() {
        return getSettings().getStorefront();
    }

    private List<String> availableAddons() {
        return getSettings().getAddons();
    }

    private SwitcherApplicationSettings getSettings() {
        SwitcherApplicationSettings settings = SwitcherState.getInstance().getState();
        if (settings == null) {
            throw new RuntimeException(MESSAGE_CANNOT_OBTAIN_SETTINGS);
        }
        return settings;
    }

    protected boolean isStorefront(String filePath) {
        return filePath.contains(storefrontName());
    }

    private String getAvailableAddon(String filePath) {
        return availableAddons().stream()
                .map(addonName -> replacePathFromStorefrontToAddon(filePath, addonName))
                .filter(replacedPath -> !replacedPath.contains(storefrontName()))
                .filter(replacedPath -> Files.exists(Paths.get(replacedPath)))
                .findFirst()
                .orElse(null);
    }

    private String replacePathFromStorefrontToAddon(String storefrontFilePath, String addonName) {
        return storefrontFilePath
                .replace(storefrontName(), addonName + PATH_SEPARATOR + ACCELERATOR_ADDON)
                .replace(ADDONS + PATH_SEPARATOR + addonName + PATH_SEPARATOR, StringUtils.EMPTY);
    }

    private String getAvailableStorefront(String filePath) {
        return availableAddons().stream()
                .map(addonName -> replacePathFromAddonToStorefront(filePath, addonName))
                .filter(replacedPath -> replacedPath.contains(storefrontName()))
                .filter(replacedPath -> Files.exists(Paths.get(replacedPath)))
                .findFirst()
                .orElse(null);
    }

    private String replacePathFromAddonToStorefront(String addonFilePath, String addonName) {
        String addonLastPartReplacement = "$1" + ADDONS + PATH_SEPARATOR + addonName + PATH_SEPARATOR;
        return addonFilePath
                .replace(addonName + PATH_SEPARATOR + ACCELERATOR_ADDON, storefrontName())
                .replaceAll(WEB_INF_REGEXP, addonLastPartReplacement)
                .replaceAll(UI_REGEXP, addonLastPartReplacement);
    }

}
