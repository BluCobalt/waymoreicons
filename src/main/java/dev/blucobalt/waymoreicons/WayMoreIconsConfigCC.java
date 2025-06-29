package dev.blucobalt.waymoreicons;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.PrefixText;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;


@Config(name = "waymoreicons")
public class WayMoreIconsConfigCC
    implements ConfigData
{
    public boolean enabled = true;

    public boolean useOldIcon = false;

    @Tooltip
    @BoundedDiscrete(min = 0L, max = 5L)
    @PrefixText
    public int iconSize = 4;

    @Excluded
    public boolean preferNonDefaultGpu = false;

    public boolean useCustomIcon = false;

    public String customIconPath = "";
}
