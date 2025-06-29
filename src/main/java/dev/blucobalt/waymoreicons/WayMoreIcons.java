package dev.blucobalt.waymoreicons;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WaymoreIcons
        implements ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("WaymoreIcons");

    private static WaymoreIcons INSTANCE;

    public WaymoreIcons()
    {
        WaymoreIcons.INSTANCE = this;

    }

    public static WaymoreIcons getInstance()
    {
        if (INSTANCE == null)
            throw new IllegalStateException("WaymoreIcons has not been initialized yet.");

        return INSTANCE;
    }


    @Override
    public void onInitializeClient()
    {
        AutoConfig.register(WayMoreIconsConfig.class, Toml4jConfigSerializer::new);
        var config = AutoConfig.getConfigHolder(WayMoreIconsConfig.class).getConfig();
    }
}
