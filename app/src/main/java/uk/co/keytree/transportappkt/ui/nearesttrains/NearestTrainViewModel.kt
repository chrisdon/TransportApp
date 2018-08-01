package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.lifecycle.MutableLiveData
import uk.co.keytree.transportappkt.base.BaseViewModel
import uk.co.keytree.transportappkt.model.Member

class NearestTrainViewModel:BaseViewModel() {
    private val stationName = MutableLiveData<String>()
    private val stationDistance = MutableLiveData<Int>()
    private lateinit var member: Member

    fun bind(member: Member) {
        stationName.value = member.name
        stationDistance.value = member.distance
        this.member = member
    }

    fun getStationName():MutableLiveData<String>{
        return stationName
    }

    fun getStationDistance():MutableLiveData<Int>{
        return stationDistance
    }
}