package top.fpsmaster.features.impl.ecat

import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemBlock
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

class RightClicker : Module("RightClicker", Category.ECat) {

    private var rightCPS = NumberSetting("rightCPS", 10.5, 1, 20, 0.5)
    private var rightDelta = NumberSetting("rightDelta", 1.5, 0, 20, 0.5)
    private var blockOnly = BooleanSetting("blockOnly", false)


    private var rTimer = MathTimer()
    private var rdelay = 0

    init{
        addSettings(rightCPS, rightDelta, blockOnly)
    }


    @Subscribe
    fun onUpdate(e: EventUpdate) {
        if(mc.currentScreen is GuiInventory || mc.currentScreen is GuiChest)
            return

        if(blockOnly.value && mc.thePlayer.inventory.getCurrentItem().item !is ItemBlock)
            return

        if(rTimer.delay(rdelay.toLong()) && mc.gameSettings.keyBindUseItem.isKeyDown){
            ProviderManager.mcProvider.rightClickMouse()
            rdelay = RandomUtils.nextFloat(600.0F / (rightCPS.value.toFloat() - rightDelta.value.toFloat()), 600.0F / (rightCPS.value.toFloat() + rightDelta.value.toFloat())).toInt()
        }
    }

    @Subscribe
    fun onTick(e: EventTick){
        if(rdelay < 0)
            rdelay = 0

        //最小CPS不能低于 0
        if(rightCPS.value.toFloat() - rightDelta.value.toFloat() < 1)
            rightDelta.value = 0

        if(rightCPS.value.toFloat() + rightDelta.value.toFloat() > 20)
            rightDelta.value = 0
    }

}
