package uk.co.keytree.transportappkt.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import uk.co.keytree.transportappkt.model.PlacesResponse
import uk.co.keytree.transportappkt.model.TrainNearResult
import uk.co.keytree.transportappkt.utils.PLACES_TYPE

interface TransportApi {
    @GET("uk/train/stations/near.json")
    fun getNearestStations(@Query("app_id") appId: String,
                           @Query("app_key") appKey: String,
                           @Query("lat") lat: Double,
                           @Query("lon") lon: Double,
                           @Query("page") page: Int = 1,
                           @Query("rpp") rpp: Int = 25) : Observable<TrainNearResult>

    @GET("uk/places.json")
    fun getPlaces(@Query("app_id") appId: String,
                           @Query("app_key") appKey: String,
                           @Query("lat") lat: Double,
                           @Query("lon") lon: Double,
                           @Query("type") type: String = PLACES_TYPE) : Observable<PlacesResponse>
}