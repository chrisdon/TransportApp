package uk.co.keytree.transportappkt.injection.module

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.co.keytree.transportappkt.BuildConfig
import uk.co.keytree.transportappkt.network.TransportApi
import uk.co.keytree.transportappkt.utils.BASE_URL
import java.util.logging.Level

@Module
@Suppress("unused")
object NetworkModule {
    /**
     * Provides the Transport service implementation.
     * @param retrofit the Retrofit object used to instantiate the service
     * @return the Transport service implementation.
     */
    @Provides
    @Reusable
    @JvmStatic
    internal fun provideTransportApi(retrofit: Retrofit): TransportApi {
        return retrofit.create(TransportApi::class.java)
    }

    /**
     * Provides the Retrofit object.
     * @return the Retrofit object
     */
    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRetrofitInterface(): Retrofit {

        val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                }).build()
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build()
    }
}