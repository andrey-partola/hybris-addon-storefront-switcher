package com.aswitcher.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Objects;

public class SwitchAction extends AddonStorefrontAction {

    private static final String MESSAGE_CANNOT_FIND_FILE_TO_OPEN = "Cannot find file to open";

    @Override
    public void actionPerformed(AnActionEvent event) {
        String sourceFilePath = getSourceFilePath(event);
        String fileToOpenPath = replacePath(sourceFilePath);
        if (fileToOpenPath == null) {
            createMessagePopup(MESSAGE_CANNOT_FIND_FILE_TO_OPEN, event.getProject());
            return;
        }
        VirtualFile fileToOpen = LocalFileSystem.getInstance().findFileByPath(fileToOpenPath);
        openFile(event, fileToOpen);
    }

    private void openFile(AnActionEvent event, VirtualFile fileToOpen) {
        if (fileToOpen == null) {
            throw new RuntimeException(MESSAGE_CANNOT_FIND_FILE_TO_OPEN);
        }
        FileEditorManager.getInstance(Objects.requireNonNull(event.getProject())).openFile(fileToOpen, true);
    }

}
