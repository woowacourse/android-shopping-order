package woowacourse.shopping.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModuleUsingGson {

    private var BASE_URL = "http://3.36.66.250:8080/"
    internal var retrofit: Retrofit = createRetrofit()

    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun setBaseUrl(baseUrl: String) {
        BASE_URL = baseUrl
        retrofit = createRetrofit()
    }

    internal inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)
}
