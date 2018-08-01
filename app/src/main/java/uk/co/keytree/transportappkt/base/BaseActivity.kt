package uk.co.keytree.transportappkt.base

import android.support.v7.app.AppCompatActivity
import uk.co.keytree.transportappkt.injection.component.DaggerViewModelInjector
import uk.co.keytree.transportappkt.injection.component.ViewModelInjector
import uk.co.keytree.transportappkt.injection.module.NetworkModule
import uk.co.keytree.transportappkt.ui.nearesttrains.MainActivity

abstract class BaseActivity: AppCompatActivity() {
    private val injector: ViewModelInjector = DaggerViewModelInjector
            .builder()
            .networkModule(NetworkModule)
            .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is MainActivity -> injector.inject(this)
        }
    }
}