package top.fpsmaster.forge.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    /**
     * @author CMCat
     * @reason fixed
     */
    @Overwrite
    public boolean canEntityBeSeen(Entity entityIn) {
        return this.worldObj.rayTraceBlocks(new Vec3(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ),
                new Vec3(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ)) == null
                || this.worldObj.rayTraceBlocks(
                new Vec3(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ),
                new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ)) == null;
    }

}
