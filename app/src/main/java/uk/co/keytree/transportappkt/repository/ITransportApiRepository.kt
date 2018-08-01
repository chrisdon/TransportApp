package uk.co.keytree.transportappkt.repository

import io.reactivex.Observable
import uk.co.keytree.transportappkt.model.PlacesResponse

interface ITransportApiRepository {
    fun loadPlaces(apiId: String, appKey: String, latitude: Double, longitude: Double) : Observable<PlacesResponse>
}