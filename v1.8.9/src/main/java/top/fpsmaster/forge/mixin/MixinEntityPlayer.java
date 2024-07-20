package top.fpsmaster.forge.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase{
    @Shadow
    public PlayerCapabilities capabilities;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract ItemStack getItemInUse();

    @Shadow
    public abstract int getItemInUseDuration();

}
