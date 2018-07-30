package uk.co.keytree.transportappkt.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import uk.co.keytree.transportappkt.model.TrainNearResult

interface TransportApi {
    @GET("uk/train/stations/near.json")
    fun getNearestStations(@Query("app_id") appId: String,
                           @Query("app_key") appKey: String,
                           @Query("lat") lat: Double,
                           @Query("lon") lon: Double,
                           @Query("page") page: Int = 1,
                           @Query("rpp") rpp: Int = 25) : Observable<TrainNearResult>
}