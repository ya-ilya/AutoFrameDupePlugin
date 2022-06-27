/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.autoframedupe.modules

import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.item.EntityItemFrame
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemShulkerBox
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import the.kis.devs.api.features.module.CategoryAPI
import the.kis.devs.api.features.module.ModuleAPI
import kotlin.concurrent.thread

class AutoFrameDupe : ModuleAPI("AutoFrameDupe", "", CategoryAPI.EXPLOIT) {
    private val delayAI = Setting("Delay After Interact", this, 3000.0, 0.0, 4000.0, NumberType.TIME)
    private val delayAA = Setting("Delay After Attack", this, 100.0, 0.0, 4000.0, NumberType.TIME)
    private val searchShulkers = Setting("Search Shulkers", this, true)
    private val timer = Timer()

    init {
        register(delayAI)
        register(delayAA)
        register(searchShulkers)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc.world == null || mc.player == null) return
        val frame = (mc.objectMouseOver.entityHit ?: return toggle())

        if (frame is EntityItemFrame && timer.ended) {
            if (frame.displayedItem.isEmpty) {
                if (searchShulkers.valBoolean
                    && !(mc.player.isCreative && mc.currentScreen is GuiContainer)
                    && mc.player.heldItemMainhand.isEmpty
                ) {
                    val shulker =
                        mc.player.inventoryContainer.inventorySlots.firstOrNull { !it.stack.isEmpty && it.stack.item is ItemShulkerBox }

                    if (shulker != null) {
                        swapItems(shulker.slotNumber, 36 + mc.player.inventory.currentItem)
                    }
                }

                mc.playerController.interactWithEntity(mc.player, frame, EnumHand.MAIN_HAND)
                timer.start(delayAI.valInt)
            } else {
                mc.playerController.attackEntity(mc.player, frame)
                timer.start(delayAA.valInt)
            }

            mc.playerController.updateController()
        } else {
            mc.player.sendMessage(TextComponentString("${ChatFormatting.RED}[AutoFrameDupe] Frame not found in your crosshair. Disabling...${ChatFormatting.RESET}"))

            isToggled = false
        }
    }

    private fun swapItems(slotIdFrom: Int, slotIdTo: Int) {
        inventoryPickup(slotIdFrom)
        inventoryPickup(slotIdTo)

        if (!mc.player.inventory.itemStack.isEmpty) {
            inventoryPickup(slotIdFrom)
        }

        return
    }

    private fun inventoryPickup(slotId: Int) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slotId, 0, ClickType.PICKUP, mc.player)
        mc.playerController.updateController()
    }

    class Timer {
        var ended = true

        fun start(ms: Int) {
            var cMs = ms

            thread {
                ended = false

                while (cMs > 0) {
                    cMs--

                    Thread.sleep(1)
                }

                ended = true
            }
        }
    }
}