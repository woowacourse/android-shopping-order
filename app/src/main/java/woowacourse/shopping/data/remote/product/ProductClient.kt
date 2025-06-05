package woowacourse.shopping.data.remote.product

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object ProductClient {
    private val contentType = "application/json".toMediaType()

    private val _retrofitService: ProductService by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ProductService::class.java)
    }

    fun getRetrofitService(): ProductService = _retrofitService
}
