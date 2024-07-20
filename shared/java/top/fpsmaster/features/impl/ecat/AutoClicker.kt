package top.fpsmaster.features.impl.ecat

import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.util.ChatComponentText
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.event.events.EventUpdate
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.utils.math.MathTimer
import top.fpsmaster.utils.math.RandomUtils

class AutoClicker : Module("AutoClicker", Category.ECat) {

    private var leftCPS = NumberSetting("leftCPS", 10.5, 1, 20, 0.5)
    private var leftDelta = NumberSetting("leftDelta", 1.5, 0, 20, 0.5)
    private var breakBlock = BooleanSetting("canBreakBlock", true)

    private var lTimer = MathTimer()
    private var ldelay = 0

    init{
        addSettings(leftCPS, leftDelta, breakBlock)
    }


    @Subscribe
    fun onUpdate(e: EventUpdate) {
        if(mc.currentScreen is GuiInventory || mc.currentScreen is GuiChest)
            return

        if(ProviderManager.mcProvider.isBreakingBlock() && breakBlock.value)
            return

        if(lTimer.delay(ldelay.toLong()) && mc.gameSettings.keyBindAttack.isKeyDown){
            ProviderManager.mcProvider.clickMouse()
            setDelay()
//            mc.thePlayer.addChatMessage(ChatComponentText("ldelay = " + ldelay))
        }
    }

    @Subscribe
    fun onTick(e: EventTick){
        if(ldelay < 0)
            ldelay = 0

        //最小CPS不能低于 0
        if(leftCPS.value.toFloat() - leftDelta.value.toFloat() < 1)
            leftDelta.value = 0

        if(leftCPS.value.toFloat() + leftDelta.value.toFloat() > 20)
            leftDelta.value = 0
    }

    private fun setDelay(){

            ldelay = RandomUtils.nextFloat(600.0F / (leftCPS.value.toFloat() - RandomUtils.nextFloat(0F, leftDelta.value.toFloat())), 600.0F / (leftCPS.value.toFloat() + RandomUtils.nextFloat(0F, leftDelta.value.toFloat()))).toInt()



    }

}
