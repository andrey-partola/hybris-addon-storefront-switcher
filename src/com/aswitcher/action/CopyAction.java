package com.aswitcher.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class CopyAction extends SwitchAction {

    private static final String MESSAGE_CANNOT_FIND_FILE_TO_COPY = "Cannot find file to copy";
    private static final String MESSAGE_CANNOT_PERFORM_COPY = "Cannot perform copy";

    private static final String MESSAGE_FILE_COPIED_FROM_STOREFRONT_TO_ADDON = "File copied from storefront to addon";
    private static final String MESSAGE_FILE_COPIED_FROM_ADDON_TO_STOREFRONT = "File copied from addon to storefront";

    @Override
    public void actionPerformed(AnActionEvent event) {
        String sourceFilePath = getSourceFilePath(event);
        String fileToCopyPath = replacePath(sourceFilePath);
        if (fileToCopyPath == null) {
            createMessagePopup(MESSAGE_CANNOT_FIND_FILE_TO_COPY, event.getProject());
            return;
        }
        VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(sourceFilePath);
        VirtualFile fileToCopy = LocalFileSystem.getInstance().findFileByPath(fileToCopyPath);
        copyFile(sourceFile, fileToCopy, event.getProject());

        createMessagePopup(createSuccessCopyMessage(sourceFilePath), event.getProject());
    }

    private String createSuccessCopyMessage(String sourceFilePath) {
        if (isStorefront(sourceFilePath)) {
            return MESSAGE_FILE_COPIED_FROM_STOREFRONT_TO_ADDON;
        }
        return MESSAGE_FILE_COPIED_FROM_ADDON_TO_STOREFRONT;
    }

    private void copyFile(VirtualFile sourceFile, VirtualFile fileToCopy, Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                saveFile(sourceFile);
                fileToCopy.setBinaryContent(sourceFile.contentsToByteArray(false));
            } catch (IOException e) {
                throw new RuntimeException(MESSAGE_CANNOT_PERFORM_COPY);
            }
        });
    }

    private void saveFile(VirtualFile file) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(file);
        if (document != null) {
            fileDocumentManager.saveDocument(document);
        }
    }

}
