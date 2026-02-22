package com.example.finalxaurora.app

import android.content.Context
import com.example.finalxaurora.data.SpaceWeatherApi
import com.example.finalxaurora.data.SpaceWeatherRepository
import com.example.finalxaurora.ui.vm.SpaceWeatherViewModel
import com.example.finalxaurora.ui.vm.VmFactory
import com.example.finalxaurora.util.SettingsStore
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AppContainer private constructor(
    val settings: SettingsStore,
    val repo: SpaceWeatherRepository,
    val vmFactory: VmFactory
) {
    companion object {
        fun create(context: Context): AppContainer {
            val prefs = context.getSharedPreferences("finalxaurora_prefs", Context.MODE_PRIVATE)
            val settings = SettingsStore(prefs)

            val okHttp = OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

            val api = SpaceWeatherApi(okHttp)
            val repo = SpaceWeatherRepository(api)

            val factory = VmFactory(
                createVm = {
                    SpaceWeatherViewModel(
                        repo = repo,
                        settings = settings
                    )
                }
            )

            return AppContainer(settings, repo, factory)
        }
    }
}
