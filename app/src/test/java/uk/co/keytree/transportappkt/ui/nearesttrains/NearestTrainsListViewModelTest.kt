package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.view.View
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.model.Member
import uk.co.keytree.transportappkt.repository.ITransportApiRepository
import uk.co.keytree.transportappkt.util.ModelUtils
import uk.co.keytree.transportappkt.util.mock
import uk.co.keytree.transportappkt.utils.TA_APP_ID
import uk.co.keytree.transportappkt.utils.TA_APP_KEY
import java.lang.Exception

class NearestTrainsListViewModelTest {

    val transportApiRepository = mock<ITransportApiRepository>()

    val viewModel by lazy{NearestTrainsListViewModel(transportApiRepository, Schedulers.trampoline(), Schedulers.trampoline())}

    val loadingVisObserverState = mock<Observer<Int>>()
    val errorMessageObserverState = mock<Observer<Int>>()
    val tappedObserverState = mock<Observer<Member>>()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        reset(transportApiRepository, loadingVisObserverState, errorMessageObserverState)
    }

    @Test
    fun loadNearestStationsSuccess() {
        val placesResponse = ModelUtils().getPlacesResponse()
        Mockito.`when`(transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.just(placesResponse))

        viewModel.loadingVisibility.observeForever(loadingVisObserverState)
        viewModel.loadNearestStations(ModelUtils().LATITUDE, ModelUtils().LONGITUDE)

        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)
        val expectedStartVisibility = View.VISIBLE
        val expectedEndVisibility = View.GONE

        argumentCaptor.run {
            verify(loadingVisObserverState, times(2)).onChanged(capture())
            val (startVisibility, endVisibility) = allValues
            assertEquals(expectedStartVisibility, startVisibility)
            assertEquals(expectedEndVisibility, endVisibility)
        }

    }

    @Test
    fun loadNearestStationsError() {

        Mockito.`when`(transportApiRepository.loadPlaces(TA_APP_ID, TA_APP_KEY, ModelUtils().LATITUDE, ModelUtils().LONGITUDE))
                .thenReturn(Observable.error(Exception()))

        viewModel.errorMessage.observeForever(errorMessageObserverState)
        viewModel.loadNearestStations(ModelUtils().LATITUDE, ModelUtils().LONGITUDE)
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)
        val expectedStartErrorMessage = null
        val expectedEndErrorMessage = R.string.train_error

        argumentCaptor.run {
            verify(errorMessageObserverState, times(2)).onChanged(capture())
            val (startErrorMessage, endErrorMessage) = allValues
            assertEquals(expectedStartErrorMessage, startErrorMessage)
            assertEquals(expectedEndErrorMessage, endErrorMessage)
        }
    }

    @Test
    fun onTappedTest() {
        val expectedMember = ModelUtils().getMember()
        viewModel.tapped.observeForever(tappedObserverState)
        viewModel.onMemberTapped(expectedMember)

        val argumentCaptor = ArgumentCaptor.forClass(Member::class.java)
        argumentCaptor.run {
            verify(tappedObserverState, times(1)).onChanged(capture())
            val member = allValues[0]
            assertEquals(expectedMember, member)
        }
    }

}