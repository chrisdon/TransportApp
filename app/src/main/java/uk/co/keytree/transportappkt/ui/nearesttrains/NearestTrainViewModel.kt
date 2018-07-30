package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.lifecycle.MutableLiveData
import android.view.View
import uk.co.keytree.transportappkt.base.BaseViewModel
import uk.co.keytree.transportappkt.model.Station

class NearestTrainViewModel:BaseViewModel() {
    private val stationName = MutableLiveData<String>()
    private val stationDistance = MutableLiveData<Int>()
    val tapped = MutableLiveData<Station>()
    private lateinit var station: Station

    fun bind(station: Station) {
        stationName.value = station.name
        stationDistance.value = station.distance
        this.station = station
    }

    fun getStationName():MutableLiveData<String>{
        return stationName
    }

    fun getStationDistance():MutableLiveData<Int>{
        return stationDistance
    }

    fun onTapped(view: View) {
        tapped.value = station
    }
}