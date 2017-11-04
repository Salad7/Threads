package com.example.msalad.threads;

/**
 * Created by cci-loaner on 11/4/17.
 */

public class Contact {
    public String phone;
    public String name;
    public Boolean isSelected = false;
    public String icon;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getIcon() {
        return name.charAt(0)+"";
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
