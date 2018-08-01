package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.view.View
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import uk.co.keytree.transportappkt.model.PlacesResponse
import uk.co.keytree.transportappkt.network.TransportApi
import uk.co.keytree.transportappkt.repository.ITransportApiRepository
import uk.co.keytree.transportappkt.util.ModelUtils
import uk.co.keytree.transportappkt.utils.TA_APP_ID
import uk.co.keytree.transportappkt.utils.TA_APP_KEY

class NearestTrainsListViewModelTest {
    @Mock
    private lateinit var transportApi: TransportApi

    @Mock
    private lateinit var transportApiRepository: ITransportApiRepository

    private lateinit var viewModel: NearestTrainsListViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = NearestTrainsListViewModel(transportApiRepository)
    }

    @Test
    fun makePlacesApiCall() {
        val placesResponse = ModelUtils().getPlacesResponse()

        Mockito.`when`(transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.just(placesResponse))

        val testObserver = TestObserver<PlacesResponse>()

        viewModel.makeCall(ModelUtils().LATITUDE, ModelUtils().LONGITUDE)
                .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue{apiPlacesResponse -> apiPlacesResponse.source == "Network Rail, NaPTAN"}
        testObserver.assertValue{apiPlacesResponse -> apiPlacesResponse.member == placesResponse!!.member}

    }

    @Test
    fun loadNearestStationsLoadingVisibility() {
        val placesResponse = ModelUtils().getPlacesResponse()

        Mockito.`when`(transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.just(placesResponse))

        viewModel.loadNearestStations(ModelUtils().LATITUDE, ModelUtils().LONGITUDE)

        assertEquals(View.VISIBLE, viewModel.loadingVisibility.value)
    }


}