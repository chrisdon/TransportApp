package uk.co.keytree.transportappkt.injection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uk.co.keytree.transportappkt.network.TransportApi
import uk.co.keytree.transportappkt.repository.TransportApiRepository
import uk.co.keytree.transportappkt.ui.nearesttrains.NearestTrainsListViewModel

class NearestTrainsListViewModelFactory(private val transportApi: TransportApi): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NearestTrainsListViewModel::class.java)) {

            return NearestTrainsListViewModel(TransportApiRepository(transportApi), Schedulers.io(),
                    AndroidSchedulers.mainThread()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}