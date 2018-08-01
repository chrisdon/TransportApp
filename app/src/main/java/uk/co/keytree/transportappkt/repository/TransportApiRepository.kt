package uk.co.keytree.transportappkt.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uk.co.keytree.transportappkt.model.PlacesResponse
import uk.co.keytree.transportappkt.network.TransportApi

class TransportApiRepository(private val transportApi: TransportApi): ITransportApiRepository {
    override fun loadPlaces(apiId: String, appKey: String, latitude: Double, longitude: Double): Observable<PlacesResponse> {
        return transportApi.getPlaces(apiId, appKey, latitude, longitude).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}