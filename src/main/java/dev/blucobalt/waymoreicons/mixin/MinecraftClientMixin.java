package dev.blucobalt.waymoreicons.mixin;


import dev.blucobalt.waymoreicons.WayMoreIcons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Inject(at = @At(value = "TAIL"), method = "close")
    private void wmi_cleanup(CallbackInfo ci)
    {
        WayMoreIcons.getInstance().cleanup();
    }
}
