/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.autoframedupe

import com.kisman.cc.features.plugins.Plugin
import me.yailya.autoframedupe.modules.AutoFrameDupe
import the.kis.devs.api.features.module.ModuleManagerAPI

class Main : Plugin() {
    private val frameDupeModule = AutoFrameDupe()

    override fun load() {
        ModuleManagerAPI.getModules().add(frameDupeModule)
    }

    override fun unload() {
        ModuleManagerAPI.getModules().remove(frameDupeModule)
    }
}