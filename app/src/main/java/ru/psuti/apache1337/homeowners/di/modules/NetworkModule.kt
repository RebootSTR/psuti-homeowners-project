package ru.psuti.apache1337.homeowners.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.AppCookieJar
import ru.psuti.apache1337.homeowners.data.profile.remote.ProfileService
import javax.inject.Singleton

//const val KEYSTORE_RESOURCE = R.raw.keystore0
//const val KEYSTORE_PASSWORD = "222324"
//const val KEYSTORE_TYPE = "PKCS12"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    fun providesBaseUrl(prefs: AppSharedPreferences): String {
        val url = prefs.backendUrl.get()
        return if (url.endsWith("/")) {
            url
        } else {
            "$url/"
        }
    }


    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String, gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
//        socketFactory: SSLSocketFactory,
//        x509TrustManager: X509TrustManager,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
//            .sslSocketFactory(socketFactory, x509TrustManager)
            .addInterceptor(httpLoggingInterceptor)
            .cookieJar(AppCookieJar())
            .build()
    }

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }


    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

//    @Provides
//    @Singleton
//    fun provideTrustManager(@ApplicationContext context: Context): X509TrustManager {
//
//        val trustManagerFactory =
//            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//
//        val keyStoreInputStream: InputStream = context.resources.openRawResource(KEYSTORE_RESOURCE)
//
//        // Creating a KeyStore containing our trusted CAs
//        val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_TYPE)
//        keyStore.load(keyStoreInputStream, KEYSTORE_PASSWORD.toCharArray())
//
//        trustManagerFactory.init(keyStore)
//
//        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
//        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
//            "Unexpected default trust managers:" + Arrays.toString(
//                trustManagers
//            )
//        }
//        return trustManagers[0] as X509TrustManager
//    }
//
//
//    @Provides
//    @Singleton
//    fun getSocketFactory(x509TrustManager: X509TrustManager): SSLSocketFactory {
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, arrayOf(x509TrustManager), null)
//        return sslContext.socketFactory
//    }
}