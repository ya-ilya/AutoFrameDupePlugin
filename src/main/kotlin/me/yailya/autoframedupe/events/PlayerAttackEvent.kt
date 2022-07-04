/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.autoframedupe.events

import net.minecraft.entity.Entity
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
class PlayerAttackEvent(val entity: Entity) : Event()