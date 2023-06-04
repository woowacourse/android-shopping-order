package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object NetworkModule {

    private var BASE_URL = "http://3.36.66.250:8080/"
//    internal var retrofit: Retrofit = createRetrofit()
//
//    fun createRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }

    val retrofitNew = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    inline fun <reified T> createNew(): T = retrofitNew.create<T>(T::class.java)

    fun setBaseUrlNew(baseUrl: String) {
        BASE_URL = baseUrl
    }

//    fun setBaseUrl(baseUrl: String) {
//        BASE_URL = baseUrl
//        retrofit = createRetrofit()
//    }
//
//    internal inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)
}
