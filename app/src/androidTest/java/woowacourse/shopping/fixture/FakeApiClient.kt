package woowacourse.shopping.fixture

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object FakeApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    fun getApiClient(url: String): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()
}
