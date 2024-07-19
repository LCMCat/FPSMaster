package top.fpsmaster.wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Session;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.fpsmaster.forge.api.IMinecraft;
import top.fpsmaster.interfaces.game.IMinecraftProvider;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class MinecraftProvider implements IMinecraftProvider {
    public File getGameDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    public FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    public EntityPlayerSP getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public boolean isHoveringOverBlock() {
        return Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
    }

    public boolean isBreakingBlock(){
        return (float) ReflectionHelper.getPrivateValue(PlayerControllerMP.class, Minecraft.getMinecraft().playerController, "curBlockDamageMP", "field_78770_f") != 0F;
    }

    public ItemStack getPlayerHeldItem() {
        return Minecraft.getMinecraft().thePlayer.getHeldItem();
    }

    public WorldClient getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    public ItemStack[] getArmorInventory() {
        return getPlayer().inventory.armorInventory;
    }

    public void setSession(Session mojang) {
        ((IMinecraft) Minecraft.getMinecraft()).arch$setSession(mojang);
    }

    public Integer getRespondTime() {
        if (Minecraft.getMinecraft().isSingleplayer())
            return 0;
        return Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID()).getResponseTime();
    }
    public void drawString(String text, float x, float y, int color) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text, (int) x, (int) y, color);
    }
    @NotNull
    public String getServerAddress() {
        if (Minecraft.getMinecraft().isSingleplayer())
            return "localhost";
        return Minecraft.getMinecraft().getNetHandler().getNetworkManager().getRemoteAddress().toString();
    }
    public void removeClickDelay() {
        ((IMinecraft) Minecraft.getMinecraft()).arch$setLeftClickCounter(0);
    }

    @Override
    public void printChatMessage(Object message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((IChatComponent) message);
    }

    @Override
    public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
        return Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
    }

    @Nullable
    @Override
    public Object getCurrentScreen() {
        return Minecraft.getMinecraft().currentScreen;
    }

    @Override
    public void clickMouse() {
        try{

            Method clickMouseMethod = ReflectionHelper.findMethod(Minecraft.class, Minecraft.getMinecraft(), new String[]{"clickMouse", "func_147116_af"});
            clickMouseMethod.invoke(Minecraft.getMinecraft());
        }catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rightClickMouse() {
        try{

            Method rightClickMouseMethod = ReflectionHelper.findMethod(Minecraft.class, Minecraft.getMinecraft(), new String[]{"rightClickMouse", "func_147118_V"});
            rightClickMouseMethod.invoke(Minecraft.getMinecraft());
        }catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
