package dev.blucobalt.waymoreicons;


public class WayMoreIconsConfig
{
    static final WayMoreIconsConfig backup = new WayMoreIconsConfig();

    public boolean enabled = true;

    public boolean useOldIcon = false;

    public int iconSize = 4;


    public boolean preferNonDefaultGpu = false;

    public boolean useCustomIcon = false;

    public String customIconPath = "";

    public WayMoreIconsConfig(WayMoreIconsConfigCC configCC)
    {
        this.enabled = configCC.enabled;
        this.useOldIcon = configCC.useOldIcon;
        this.iconSize = configCC.iconSize;
        this.preferNonDefaultGpu = configCC.preferNonDefaultGpu;
        this.useCustomIcon = configCC.useCustomIcon;
        this.customIconPath = configCC.customIconPath;
    }

    private WayMoreIconsConfig() {}
}
