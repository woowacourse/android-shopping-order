package woowacourse.shopping.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.service.RetrofitCartService
import woowacourse.shopping.data.service.RetrofitProductService

object RetrofitUtil {
    var url: String = ""
        set(value) {
            field = value.removeSuffix("/")
        }

    private var instance: Retrofit? = null

    val retrofitProductService: RetrofitProductService by lazy {
        getRetrofit().create(RetrofitProductService::class.java)
    }

    val retrofitCartService: RetrofitCartService by lazy {
        getRetrofit().create(RetrofitCartService::class.java)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    private fun getRetrofit(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return instance!!
    }
}
