package dev.blucobalt.waymoreicons.mixin;


import dev.blucobalt.waymoreicons.WayMoreIcons;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(value = GLFW.class, remap = false)
public class GLFWMixin
{
    @Inject(at = @At("TAIL"), method = "glfwDefaultWindowHints")
    private static void wmi_injectWindowHints(CallbackInfo ci)
    {
        if (!WayMoreIcons.onWayland())
        {
            WayMoreIcons.LOGGER.error("[WMI] not on wayland. not doing anything");
            return;
        }
        if (WayMoreIcons.getConfig().enabled)
            WayMoreIcons.getInstance().secretSauce();
        else
            WayMoreIcons.LOGGER.info("[WMI] WayMoreIcons is disabled, not doing anything.");
    }

    @Inject(at = @At("HEAD"), method = "glfwSetWindowIcon", cancellable = true)
    private static void wmi_InterceptWindowIcon(long window, Buffer images, CallbackInfo ci)
    {
        if (!WayMoreIcons.onWayland())
        {
            WayMoreIcons.LOGGER.error("[WMI] not on wayland. not doing anything");
            return;
        }
        ci.cancel();

        if (!WayMoreIcons.getConfig().enabled)
         WayMoreIcons.LOGGER.warn("[WMI] Even though WayMoreIcons is disabled, the call to GLFW#glfwSetWindowIcon was intercepted to prevent a crash.");
    }
}
