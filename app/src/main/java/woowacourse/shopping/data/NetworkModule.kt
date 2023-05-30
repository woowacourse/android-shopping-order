package woowacourse.shopping.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private lateinit var url: String
    // todo private 멤버로 하고싶음..
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(NullOnEmptyConvertFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun setBaseUrl(url: String) {
        this.url = url
    }

    inline fun <reified T> getService(): T {

        return retrofit.create(T::class.java)
    }
}
