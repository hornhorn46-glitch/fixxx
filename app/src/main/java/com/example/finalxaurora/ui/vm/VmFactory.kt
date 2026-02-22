package com.example.finalxaurora.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VmFactory(
    private val createVm: () -> SpaceWeatherViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpaceWeatherViewModel::class.java)) {
            return createVm() as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
