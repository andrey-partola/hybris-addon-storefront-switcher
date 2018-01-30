package com.aswitcher;

import com.intellij.ide.util.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class SwitcherApplicationSettings {

    @PropertyName("storefront")
    private String storefront = "";
    @PropertyName("addons")
    private List<String> addons = new ArrayList<>();

    public String getStorefront() {
        return storefront;
    }

    public void setStorefront(String storefront) {
        this.storefront = storefront;
    }

    public List<String> getAddons() {
        return addons;
    }

    public void setAddons(List<String> addons) {
        this.addons = addons;
    }

}
