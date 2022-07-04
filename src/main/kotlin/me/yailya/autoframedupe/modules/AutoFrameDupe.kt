/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.autoframedupe.modules

import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import me.yailya.autoframedupe.events.PlayerAttackEvent
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.item.EntityItemFrame
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemShulkerBox
import net.minecraft.util.EnumHand
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import the.kis.devs.api.features.module.CategoryAPI
import the.kis.devs.api.features.module.ModuleAPI
import the.kis.devs.api.util.chat.cubic.ChatUtilityAPI
import kotlin.concurrent.thread

class AutoFrameDupe : ModuleAPI("AutoFrameDupe", "", CategoryAPI.EXPLOIT) {
    private val delayAI = register(Setting("Delay After Interact", this, 3000.0, 0.0, 4000.0, NumberType.TIME))
    private val delayAA = register(Setting("Delay After Attack", this, 100.0, 0.0, 4000.0, NumberType.TIME))
    private val searchShulkers = register(Setting("Search Shulkers", this, true))
    private val timer = Timer()

    override fun update() {
        if (!timer.ended) return
        if (mc.world == null || mc.player == null) return
        val frame = (mc.objectMouseOver.entityHit ?: return toggle())

        if (frame is EntityItemFrame) {
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
            ChatUtilityAPI.error().printClientModuleMessage("Frame not found in your crosshair. Disabling...")

            isToggled = false
        }
    }

    @SubscribeEvent
    fun onAttack(event: PlayerAttackEvent) {
        if (event.entity == mc.player
            && mc.objectMouseOver.entityHit == event.entity
            && event.entity is EntityItemFrame
            && event.entity.displayedItem.isEmpty
        ) {
            event.isCanceled = true
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