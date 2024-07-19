package top.fpsmaster.features.impl.ecat

import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.util.ChatComponentText
import org.lwjgl.input.Keyboard
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventAttack
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.event.events.EventUpdate
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.utils.math.MathTimer
import top.fpsmaster.utils.math.RandomUtils

class KeepRange : Module("KeepRange", Category.ECat) {

    private var workDistance = NumberSetting("workDistance", 2.9, 0, 6, 0.1)
    private var distanceDelta = NumberSetting("distanceDelta", 0.1, 0, 6, 0.1)
    private var keepTick = NumberSetting("keepTick", 10, 0, 40, 1)
    private var restTick = NumberSetting("restTick", 5, 0, 40, 1)
    private var comboOnly = BooleanSetting("comboOnly", true)
//    private var stopForwardOnly = BooleanSetting("stopForwardOnly", true)

    private var isComboing: Boolean = false
    var target: EntityPlayer? = null
    private var startRangeMs = 0L

    init{
        addSettings(workDistance, distanceDelta, keepTick, restTick, comboOnly)
    }

    @Subscribe
    fun onAttack(e: EventAttack){
        target = if( e.target is EntityPlayer ) e.target as EntityPlayer else null
        isComboing = (target?.hurtTime?: 0) > 1
    }


    @Subscribe
    fun onUpdate(e: EventUpdate) {
        if(target == null)
            return
        if(target!!.isDead){
            target = null
            return
        }
        if(comboOnly.value && !isComboing)
            return
        val distance = mc.thePlayer.getDistanceToEntity(target)
        if(distance >= workDistance.value.toFloat() + distanceDelta.value.toFloat()){
            target = null
            return
        }
        if(System.currentTimeMillis() - startRangeMs <= 20 * keepTick.value.toInt()) {
            return
        }
        if(distance <= workDistance.value.toFloat() - distanceDelta.value.toFloat()){
            target = null
            resetKeyDown()
            return
        }
        if(System.currentTimeMillis() - startRangeMs >= (20 * (keepTick.value.toInt() + restTick.value.toInt()))){
            startRangeMs = System.currentTimeMillis()
//            distance =(passed rest tick)=> pause forward
//            not in distance=> reset
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
//            mc.thePlayer.addChatComponentMessage(ChatComponentText("break"))
        }
        if(System.currentTimeMillis() - startRangeMs >= (20 * keepTick.value.toInt()))
            resetKeyDown()
    }

    @Subscribe
    fun onTick(e: EventTick){

        //最小距离不能低于 0
        if(workDistance.value.toFloat() - distanceDelta.value.toFloat() < 0)
            distanceDelta.value = 0

        if(workDistance.value.toFloat() + distanceDelta.value.toFloat() > 6)
            distanceDelta.value = 0
    }


    private fun resetKeyDown() {
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode)) {
//            mc.thePlayer.addChatComponentMessage(ChatComponentText("reset"))

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)
        }
    }

}
