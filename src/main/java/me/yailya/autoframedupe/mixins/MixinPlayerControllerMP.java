/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.autoframedupe.mixins;

import me.yailya.autoframedupe.events.PlayerAttackEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void onAttack(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        PlayerAttackEvent event = new PlayerAttackEvent(targetEntity);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
