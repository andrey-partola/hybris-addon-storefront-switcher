package com.aswitcher.view;

import com.aswitcher.settings.SwitcherApplicationSettings;
import com.aswitcher.settings.SwitcherState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConfigurationForm {

    private JPanel rootPanel;
    private JTextField selectedStorefront;
    private JLabel currentStorefrontLabel;
    private JLabel currentAddonsLabel;
    private JList<String> currentAddonsList;
    private JButton addAddonButton;
    private JButton removeAddonButton;
    private JTextField addonToAddTextField;
    private JLabel addAddonLabel;
    private JList<String> addonModules;
    private JLabel addStoreftontLabel;
    private JScrollPane storefrontModulesScrollPane;
    private JScrollPane currentAddonsScrollPane;
    private JList<String> storefrontModules;
    private JButton selectStorefrontButton;

    public ConfigurationForm() {
        setUpModules();
        setUpData();
        setUpListeners();
    }

    private void setUpListeners() {
        setUpAddAddonButtonListeners();
        setUpRemoveAddonButtonListeners();
        setUpSelectStorefrontButtonListeners();
    }

    private void setUpAddAddonButtonListeners() {
        addAddonButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultListModel<String> listModel = (DefaultListModel<String>) currentAddonsList.getModel();
                addAddonTextToList(listModel);
            }
        });
    }

    private void setUpRemoveAddonButtonListeners() {
        removeAddonButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultListModel<String> listModel = (DefaultListModel<String>) currentAddonsList.getModel();
                listModel.removeElementAt(currentAddonsList.getSelectedIndex());
            }
        });
    }

    private void setUpSelectStorefrontButtonListeners() {
        selectStorefrontButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedModule = storefrontModules.getSelectedValue();
                if (selectedModule != null) {
                    selectedStorefront.setText(selectedModule);
                    storefrontModules.clearSelection();
                }
            }
        });
    }

    private void setUpModules() {
        DefaultListModel<String> model = new DefaultListModel<>();
        getCurrentProjectModules().stream()
                .map(Module::getName)
                .forEach(model::addElement);
        addonModules.setModel(model);
        storefrontModules.setModel(model);
    }

    private List<Module> getCurrentProjectModules() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        Module[] modules = ModuleManager.getInstance(project).getModules();
        List<Module> modulesList = Arrays.asList(modules);
        modulesList.sort(Comparator.comparing(Module::getName));
        return modulesList;
    }

    private void setUpData() {
        currentAddonsList.setModel(new DefaultListModel<>());
        SwitcherApplicationSettings settings = getSettings();
        selectedStorefront.setText(settings.getStorefront());
        DefaultListModel<String> listModel = (DefaultListModel<String>) currentAddonsList.getModel();
        settings.getAddons().forEach(listModel::addElement);
    }

    private SwitcherApplicationSettings getSettings() {
        SwitcherApplicationSettings settings = SwitcherState.getInstance().getState();
        if (settings == null) {
            throw new RuntimeException("Cannot obtain settings");
        }
        return settings;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public boolean isModified(SwitcherApplicationSettings data) {
        return !getStorefront().equals(data.getStorefront()) ||
                !getAddons().equals(data.getAddons());
    }

    public String getStorefront() {
        return selectedStorefront.getText();
    }

    public List<String> getAddons() {
        return IntStream.range(0, currentAddonsList.getModel().getSize())
                .mapToObj(i -> currentAddonsList.getModel().getElementAt(i))
                .collect(Collectors.toList());
    }

    private void addAddonTextToList(DefaultListModel<String> listModel) {
        String selectedModule = addonModules.getSelectedValue();
        if (selectedModule != null && !listModel.contains(selectedModule)) {
            listModel.addElement(selectedModule);
            addonModules.clearSelection();
        }
    }

}
