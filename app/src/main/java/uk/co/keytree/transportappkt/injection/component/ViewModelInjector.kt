package uk.co.keytree.transportappkt.injection.component

import dagger.Component
import uk.co.keytree.transportappkt.injection.module.NetworkModule
import uk.co.keytree.transportappkt.ui.nearesttrains.MainActivity
import uk.co.keytree.transportappkt.ui.nearesttrains.NearestTrainsListViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {
    /**
     * Injects required dependencies into the specified NearestTrainsViewModel.
     * @param mainActivity: MainActivity in which to inject the dependencies
     */
    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}