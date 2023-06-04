package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object NetworkModuleUsingSerialization {

    private var BASE_URL = "http://3.38.132.180:8080/"

    @OptIn(ExperimentalSerializationApi::class)
    val retrofitNew: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    inline fun <reified T> createNew(): T = retrofitNew.create<T>(T::class.java)

    fun setBaseUrlNew(baseUrl: String) {
        BASE_URL = baseUrl
    }
}
