package io.github.giuseppebrb.headsetnotifier.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Represents the information of an application installed on the device.
 */

public class Application implements Comparable<Application> {

    private String name;
    private Drawable icon;
    private String packageName;

    public Application(String name, Drawable icon, String packageName){
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public int compareTo(@NonNull Application o) {
        return name.compareTo(o.getName());
    }
}
