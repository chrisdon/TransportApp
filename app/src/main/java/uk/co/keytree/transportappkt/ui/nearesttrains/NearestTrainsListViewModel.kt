package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.base.BaseViewModel
import uk.co.keytree.transportappkt.model.Member
import uk.co.keytree.transportappkt.model.PlacesResponse
import uk.co.keytree.transportappkt.repository.ITransportApiRepository
import uk.co.keytree.transportappkt.utils.TA_APP_ID
import uk.co.keytree.transportappkt.utils.TA_APP_KEY

class NearestTrainsListViewModel(private val transportApiRepository: ITransportApiRepository): BaseViewModel() {
    //@Inject
    //lateinit var transportApi: TransportApi

    private lateinit var subscription: Disposable
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val tapped: MutableLiveData<Member> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadNearestStations(latitude, longitude) }
    val stationListAdapter: TrainListAdapter = TrainListAdapter{member: Member -> onMemberTapped(member)}

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    init {
        //loadNearestStations()
    }

    fun makeCall(latitude: Double, longitude: Double) : Observable<PlacesResponse> {
        return transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, latitude, longitude)
    }

    fun loadNearestStations(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
        subscription = makeCall(latitude, longitude)
                .doOnSubscribe { onRetrieveStationListStart() }
                .doOnTerminate { onRetrieveStationListFinish() }
                .subscribe(
                        { result -> onRetrieveStationListSuccess(result.member) },
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

    private fun onRetrieveStationListSuccess(members:List<Member>){
        stationListAdapter.updatePostList(members)
    }

    private fun onRetrieveStationListError(error: Throwable){
        Log.e("NearestTrainsViewModel", error.localizedMessage, error)
        errorMessage.value = R.string.train_error
        loadingVisibility.value = View.GONE
    }

    private fun onMemberTapped(member: Member) {
        tapped.value = member
    }
}