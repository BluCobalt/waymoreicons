package dev.blucobalt.waymoreicons;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;


@Config(name = "waymoreicons")
public class WayMoreIconsConfig
    implements ConfigData
{
    static final WayMoreIconsConfig backup = new WayMoreIconsConfig();

    public boolean enabled = true;

    public boolean useOldIcon = false;

    public int iconSize = 4;

    public boolean preferNonDefaultGpu = false;

    public boolean useCustomIcon = false;

    public String customIconPath = "";
}
