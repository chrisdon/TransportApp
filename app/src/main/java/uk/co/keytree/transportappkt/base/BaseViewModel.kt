package uk.co.keytree.transportappkt.base

import android.arch.lifecycle.ViewModel
import uk.co.keytree.transportappkt.injection.component.DaggerViewModelInjector
import uk.co.keytree.transportappkt.injection.component.ViewModelInjector
import uk.co.keytree.transportappkt.injection.module.NetworkModule
import uk.co.keytree.transportappkt.ui.nearesttrains.NearestTrainsListViewModel

abstract class BaseViewModel : ViewModel() {
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
            is NearestTrainsListViewModel -> injector.inject(this)
        }
    }
}