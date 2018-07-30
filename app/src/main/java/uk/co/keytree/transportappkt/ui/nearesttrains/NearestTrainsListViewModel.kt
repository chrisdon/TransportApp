package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.base.BaseViewModel
import uk.co.keytree.transportappkt.model.Station
import uk.co.keytree.transportappkt.network.TransportApi
import uk.co.keytree.transportappkt.utils.TA_APP_ID
import uk.co.keytree.transportappkt.utils.TA_APP_KEY
import uk.co.keytree.transportappkt.utils.TEST_LAT
import uk.co.keytree.transportappkt.utils.TEST_LON
import javax.inject.Inject

class NearestTrainsListViewModel: BaseViewModel() {
    @Inject
    lateinit var transportApi: TransportApi

    private lateinit var subscription: Disposable
    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadNearestStations() }
    val stationListAdapter: TrainListAdapter = TrainListAdapter()

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    init {
        loadNearestStations()
    }

    private fun loadNearestStations() {
        subscription = transportApi.getNearestStations(TA_APP_ID, TA_APP_KEY, TEST_LAT, TEST_LON)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onRetrieveStationListStart() }
                .doOnTerminate { onRetrieveStationListFinish() }
                .subscribe(
                        { result -> onRetrieveStationListSuccess(result.stations) },
                        { error -> onRetrieveStationListError(error) }
                )
    }

    private fun onRetrieveStationListStart(){
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    private fun onRetrieveStationListFinish(){
        loadingVisibility.value = View.GONE
    }

    private fun onRetrieveStationListSuccess(stationList:List<Station>){
        stationListAdapter.updatePostList(stationList)
    }

    private fun onRetrieveStationListError(error: Throwable){
        Log.e("NearestTrainsViewModel", error.localizedMessage, error);
        errorMessage.value = R.string.train_error
    }
}