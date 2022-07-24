/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.autoframedupe.mixins

import me.yailya.autoframedupe.events.PlayerAttackEvent
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import the.kis.devs.api.KismanAPI

@Mixin(PlayerControllerMP::class)
abstract class MixinPlayerControllerMP {
    @Inject(method = ["attackEntity"], at = [At("HEAD")], cancellable = true)
    fun onAttack(playerIn: EntityPlayer, targetEntity: Entity, ci: CallbackInfo) {
        val event = PlayerAttackEvent(targetEntity)
        KismanAPI.getEventBus().post(event)
        if (event.cancelled) {
            ci.cancel()
        }
    }
}