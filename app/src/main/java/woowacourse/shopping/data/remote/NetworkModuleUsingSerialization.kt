package woowacourse.shopping.data.remote

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl

object NetworkModuleUsingSerialization {
    private var BASE_URL = "http://3.38.132.180:8080/"
    private lateinit var applicationContext: Context

    @OptIn(ExperimentalSerializationApi::class)
    val retrofitNew: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            provideOkHttpClient(
                AuthInterceptor(
                    AuthInfoDataSourceImpl.getInstance(
                        applicationContext,
                    ),
                ),
            ),
        )
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    inline fun <reified T> createNew(): T = retrofitNew.create<T>(T::class.java)

    fun setBaseUrlNew(baseUrl: String) {
        BASE_URL = baseUrl
    }

    fun setApplicationContext(context: Context) {
        applicationContext = context
    }

    private fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }
}
