package top.fpsmaster.features.impl.ecat

import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.ChatComponentText
import net.minecraft.util.MathHelper
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.event.events.EventUpdate
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.utils.math.RandomUtils
import java.util.*
import kotlin.math.atan2

class AimAssist : Module("AimAssist", Category.ECat) {

    private var hSpeed = NumberSetting("hSpeed", 45, 0, 180, 1)
    private var vSpeed = NumberSetting("vSpeed", 0, 0, 89, 1)
    private var speedDelta = NumberSetting("speedDelta", 10, 0, 180, 1)
    private var fov = NumberSetting("fov", 180, 0, 360, 1)
    private var minDistance = NumberSetting("minDistance", 4.5, 0, 10, 0.1)

    private var clickAim = BooleanSetting("clickAim", true)
    private var breakBlock = BooleanSetting("canBreakBlock", true)

    private var targetPlayer = BooleanSetting("targetPlayer", true)
    private var targetAnimal = BooleanSetting("targetAnimal", false)
    private var targetMob = BooleanSetting("targetMob", false)
    private var targetInvisible = BooleanSetting("targetInvisible", false)
    private var targetTeam = BooleanSetting("targetTeam", false)

    private var target : Entity? = null

    init{
        addSettings(hSpeed, vSpeed, speedDelta, fov, minDistance, clickAim, breakBlock,
            targetPlayer, targetAnimal, targetMob, targetInvisible, targetTeam)
    }


    @Subscribe
    fun onUpdate(e: EventUpdate) {
        //currentScreenMinecraft?
        if(mc.currentScreen != null)
            return

        //Player in game?
        if(mc.thePlayer == null || mc.theWorld == null)
            return

        //break block?
        if(ProviderManager.mcProvider.isBreakingBlock() && breakBlock.value)
            return

        if(clickAim.value && !mc.gameSettings.keyBindAttack.isKeyDown)
            return

        target = getTarget()
        val rotation = faceTarget(target, hSpeed.value.toFloat(), vSpeed.value.toFloat())
        mc.thePlayer.rotationYaw = rotation[0]
        if(vSpeed.value.toInt() != 0) {
//            mc.thePlayer.addChatMessage(ChatComponentText("§bvset vspeed = " + vSpeed.value + " == 0?" + (vSpeed.value == 0)))
            mc.thePlayer.rotationPitch = rotation[1]
        }
    }

    @Subscribe
    fun onTick(e: EventTick){
    }


    private fun getTarget(): Entity? {
        val entities: List<Entity> = mc.theWorld.loadedEntityList.filter { entity: Entity? ->
            isValidToAttack(
                entity
            )
        }.sortedBy{
                entity: Entity? -> entity?.getDistanceToEntity(mc.thePlayer) }


        if (entities.isEmpty()) {
            return null
        }
        if(entities[0] == mc.thePlayer)
            return if(entities.size > 1 && entities[1] != null) entities[1] else null


//        mc.thePlayer.addChatMessage(ChatComponentText("entity found! " + entities[0]))
        return entities[0]
    }


    private fun isValidToAttack(entity: Entity?) : Boolean{

        if(entity == null || entity.isDead)
            return false

        if(mc.thePlayer.getDistanceSqToEntity(entity) > minDistance.value.toDouble())
            return false

        if(entity is EntityPlayer && !targetPlayer.value)
            return false

        if((entity is EntityAnimal || entity is EntityVillager) && !targetAnimal.value)
            return false

        if(entity is EntityMob && !targetMob.value)
            return false

        if(entity.isInvisible && !targetInvisible.value)
            return false

        if(getTeamColor(entity) == getTeamColor(mc.thePlayer) && !targetTeam.value)
            return false

        if(fov.value != 360 && !isValidFov(entity))
            return false

        return true
    }


    private fun getTeamColor(player: Entity?): Int {
        var var2 = 16777215

        if(player == null)
            return var2

        if (player is EntityPlayer) {
            val var6 = player.team as ScorePlayerTeam

            if (var6 != null) {
                val var7 = FontRenderer.getFormatFromString(var6.colorPrefix)

                if (var7.length >= 2) {
                    if (!"0123456789abcdef".contains(var7[1].toString())) return var2

                    var2 = mc.fontRendererObj.getColorCode(var7[1])
                }
            }
        }

        return var2
    }


    private fun fovToEntity(ent: Entity): Float {
        val x: Double = ent.posX - mc.thePlayer.posX
        val z: Double = ent.posZ - mc.thePlayer.posZ
        val yaw = atan2(x, z) * 57.2957795
        return (yaw * -1.0).toFloat()
    }

    private fun isValidFov(entity: Entity): Boolean {
        var fov = fov.value
        fov = (fov.toDouble() * 0.5).toFloat()
        val v: Double =
            ((mc.thePlayer.rotationYaw - fovToEntity(entity)).toDouble() % 360.0 + 540.0) % 360.0 - 180.0
        return v > 0.0 && v < fov.toDouble() || (-fov).toDouble() < v && v < 0.0
    }


    private fun faceTarget(target: Entity?, yawSpeed: Float, pitchSpeed: Float): FloatArray {
        if(target == null)
            return floatArrayOf(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        val deltaX: Double = target.posX - mc.thePlayer.posX
        val deltaZ: Double = target.posZ - mc.thePlayer.posZ
        val deltaY: Double = if (target is EntityLivingBase) {
            target.posY + target.eyeHeight - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
        } else {
            (target.entityBoundingBox.minY + target.entityBoundingBox.maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
        }
        val rnd = Random()
        val offset = if (false) (rnd.nextInt(15) * 0.25f + 5.0f) else 0.0f
        val d = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ).toDouble()
        val arctanYaw = (atan2(deltaZ + offset, deltaX) * 180.0 / 3.141592653589793).toFloat() - 90.0f
        val arctanPitch = (-(atan2(
            deltaY - (if ((target is EntityPlayer)) 0.5f else 0.0f) + offset,
            d
        ) * 180.0 / 3.141592653589793)).toFloat()
        val pitch = changeRotation(mc.thePlayer.rotationPitch, arctanPitch, pitchSpeed)
        val yaw = changeRotation(mc.thePlayer.rotationYaw, arctanYaw, yawSpeed)
        return floatArrayOf(yaw, pitch)
    }

    private fun changeRotation(playerTheta: Float, delta: Float, absOmega: Float): Float {
        var omega = MathHelper.wrapAngleTo180_float(delta - playerTheta)
        var fixedDelta = RandomUtils.nextFloat(0F, speedDelta.value.toFloat())
        if (omega > absOmega + fixedDelta) {
            omega = absOmega + fixedDelta
        }
        if (omega < -absOmega - fixedDelta) {
            omega = -absOmega - fixedDelta
        }
        return playerTheta + omega
    }
//    private fun getEnemy() : Entity {
//        val fov : Int = fov.value.toInt()
//    }

}
