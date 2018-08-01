package uk.co.keytree.transportappkt.injection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import uk.co.keytree.transportappkt.network.TransportApi
import uk.co.keytree.transportappkt.ui.nearesttrains.NearestTrainsListViewModel

class NearestTrainsListViewModelFactory(private val transportApi: TransportApi): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NearestTrainsListViewModel::class.java)) {
            return NearestTrainsListViewModel(transportApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}