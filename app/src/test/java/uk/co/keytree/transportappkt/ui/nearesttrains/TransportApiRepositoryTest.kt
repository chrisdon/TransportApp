package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import uk.co.keytree.transportappkt.model.PlacesResponse
import uk.co.keytree.transportappkt.network.TransportApi
import uk.co.keytree.transportappkt.repository.ITransportApiRepository
import uk.co.keytree.transportappkt.repository.TransportApiRepository
import uk.co.keytree.transportappkt.util.ModelUtils
import uk.co.keytree.transportappkt.utils.TA_APP_ID
import uk.co.keytree.transportappkt.utils.TA_APP_KEY

class TransportApiRepositoryTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    val transportApi = Mockito.mock(TransportApi::class.java)

    val transportApiRepository by lazy { TransportApiRepository(transportApi) }

    @Test
    fun loadPlacesApiCallSuccess() {
        val placesResponse = ModelUtils().getPlacesResponse()

        Mockito.`when`(transportApi.getPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.just(placesResponse))

        transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE)
                .test()
                .assertComplete()
    }

    @Test
    fun loadPlacesApiCallResult() {
        val placesResponse = ModelUtils().getPlacesResponse()

        Mockito.`when`(transportApi.getPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.just(placesResponse))

        transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE)
                .test()
                .assertValue(placesResponse)
    }

    @Test
    fun loadPlacesApiCallError() {
        val response = Throwable("This is an error")
        Mockito.`when`(transportApi.getPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.error(response))

        transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE)
                .test()
                .assertError(response)
    }

}