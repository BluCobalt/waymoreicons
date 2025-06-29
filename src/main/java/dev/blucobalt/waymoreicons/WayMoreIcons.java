package dev.blucobalt.waymoreicons;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class WayMoreIcons
        implements ClientModInitializer
{


    public static final Logger LOGGER      = LogManager.getLogger("WayMoreIcons");


    private static WayMoreIcons INSTANCE;
    private        boolean      goodConfig = false;

    private final String identifier  = "mc-waymoreicons-" + UUID.randomUUID();
    private final Path   iconPath    = Paths.get("/tmp", identifier + "-icon.png");
    private final Path   desktopPath = Paths.get(System.getProperty("user.home"), ".local", "share", "applications",
                                               this.identifier + ".desktop");

    public WayMoreIcons()
    {
        WayMoreIcons.INSTANCE = this;

        Path tmpDir = Paths.get("/tmp");
        Path desktopDir = Paths.get(System.getProperty("user.home"), ".local", "share", "applications");
        try {
            DirectoryStream<Path> danglingIcons = Files.newDirectoryStream(tmpDir, "mc-waymoreicons-*");
            DirectoryStream<Path> danglingDesktops = Files.newDirectoryStream(desktopDir, "mc-waymoreicons-*.desktop");
            Stream<Path> danglingFiles = Stream.concat(StreamSupport.stream(danglingIcons.spliterator(), false),
                                                       StreamSupport.stream(danglingDesktops.spliterator(), false));
            danglingFiles.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                    LOGGER.warn("[WMI] deleted dangling file: {}", path);
                } catch (IOException e) {
                    LOGGER.error("[WMI] failed to delete dangling file: {}", path, e);
                }
            });

            danglingIcons.close();
            danglingDesktops.close();
        } catch (IOException e) {
            LOGGER.error("[WMI] failed to search for dangling files in /tmp or ~/.local/share/applications", e);
        }
    }

    public static WayMoreIcons getInstance()
    {
        if (INSTANCE == null) {
            throw new IllegalStateException("WayMoreIcons has not been initialized yet.");
        }

        return INSTANCE;
    }

    public static WayMoreIconsConfig getConfig()
    {
        if (INSTANCE == null)
        {
            throw new IllegalStateException("WayMoreIcons has not been initialized yet.");
        }

        WayMoreIconsConfig config;

        try {
            if (!INSTANCE.goodConfig)
                AutoConfig.register(WayMoreIconsConfigCC.class, Toml4jConfigSerializer::new);
            config = new WayMoreIconsConfig(AutoConfig.getConfigHolder(WayMoreIconsConfigCC.class).getConfig());
        } catch (NoClassDefFoundError e)
        {
            if (!INSTANCE.goodConfig)
                LOGGER.warn("[WMI] Cloth Config not found. using default config.");
            config = WayMoreIconsConfig.backup;
        } catch (Exception e)
        {
            LOGGER.error("[WMI] failed to load config. using default config.", e);
            config = WayMoreIconsConfig.backup;
        }
        INSTANCE.goodConfig = true;
        return config;
    }

    @Override
    public void onInitializeClient()
    {
        getConfig();
    }

    public void secretSauce()
    {
        if (!Files.exists(desktopPath.getParent())) {
            // are we allowed to make one?
            LOGGER.error(
                    new IllegalStateException("~/.local/share/applications/ doesn't exist! refusing to continue"));
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(desktopPath)) {
            writer.write("[Desktop Entry]\n");
            writer.write("Name=Minecraft\n");
            writer.write("Type=Application\n");
            writer.write("NoDisplay=true\n");
            writer.write("Icon=" + this.iconPath.toAbsolutePath() + "\n");
            writer.write("StartupWMClass=" + this.identifier + "\n");
        } catch (IOException e) {
            LOGGER.error("[WMI] failed to write WayMoreIcons desktop entry to {}", desktopPath, e);
            return;
        }

        this.populateIcon();
        GLFW.glfwWindowHintString(GLFW.GLFW_WAYLAND_APP_ID, identifier);
        LOGGER.info("[WMI] ✅✅✅");
    }

    private void populateIcon()
    {
        if (getConfig().useCustomIcon) {
            Path customIconPath = Paths.get(getConfig().customIconPath);
            if (Files.exists(customIconPath)) {
                try {
                    Files.copy(customIconPath, this.iconPath);
                    LOGGER.info("[WMI] copied custom icon from {}", customIconPath);
                    return;
                } catch (IOException e) {
                    LOGGER.error("[WMI] failed to copy custom icon from {}. falling back to original", customIconPath,
                                 e);
                }
            } else {
                LOGGER.error("[WMI] custom icon path {} does not exist! falling back to original", customIconPath);
            }
        }

        String icon = "/assets/waymoreicons/icons";

        if (getConfig().useOldIcon) {
            if (getConfig().iconSize == 1) {
                icon += "/old/icon_16x16.png";
            } else {
                icon += "/old/icon_32x32.png";
            }
        } else {
            try {
                if (!SharedConstants.getGameVersion().stable()) {
                    icon += "/snapshot";
                }
            } catch (NoSuchMethodError ignored) {
                LOGGER.warn("[WMI] not detecting snapshot version, assuming stable");
            }
            switch (getConfig().iconSize) {
                case 1:
                    icon += "/icon_16x16.png";
                    break;
                case 2:
                    icon += "/icon_32x32.png";
                    break;
                case 3:
                    icon += "/icon_48x48.png";
                    break;
                case 4:
                    icon += "/icon_128x128.png";
                    break;
                case 5:
                    icon += "/icon_256x256.png";
                    break;
                default:
                    LOGGER.error(new IllegalStateException("icon size " + getConfig().iconSize
                                                           + " is not supported! falling back to 128x128"));
                    icon += "/icon_128x128.png";
                    break;
            }
        }

        try {
            InputStream iconIs = this.getClass().getResourceAsStream(icon);
            assert iconIs != null;
            Files.copy(iconIs, this.iconPath);
        } catch (Exception e) {
            LOGGER.error("[WMI] failed to copy default icon", e);
        }
    }

    public void cleanup()
    {
        try {
            Files.delete(this.iconPath);
            Files.delete(this.desktopPath);
        } catch (IOException e) {
            LOGGER.error("[WMI] failed to clean up icons", e);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean onWayland()
    {
        String versionString = Version.getVersion();
        Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(versionString);

        int major = 0, minor = 0, revision = 0;
        if (matcher.find()) {
            major = Integer.parseInt(matcher.group(1));
            minor = Integer.parseInt(matcher.group(2));
            revision = Integer.parseInt(matcher.group(3));
        } else {
            LOGGER.error("[WMI] Failed to parse LWJGL version: {}. Assuming not running on Wayland.", versionString);
        }

        // lwjgl 3.3.3 are glfw 3.4. GLFW.glfwGetPlatform() was introduced in glfw 3.4
        if (major < 3 || minor < 3 || revision < 3)
        {
            String display = System.getenv("WAYLAND_DISPLAY");
            LOGGER.warn("[WMI] GLFW < 3.4 detected. using environment variable WAYLAND_DISPLAY to determine if we are on Wayland: {}", display);
            if (display == null || display.isEmpty()) {
                LOGGER.warn("[WMI] WAYLAND_DISPLAY is not set or empty. assuming we are not on Wayland.");
                return false;
            } else
                return true;
        } else
            return (GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WAYLAND);
    }

}