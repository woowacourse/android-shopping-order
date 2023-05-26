package woowacourse.shopping.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.service.RetrofitCartService
import woowacourse.shopping.service.RetrofitProductService

object RetrofitUtil {

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
            instance = Retrofit.Builder() // 객체를 생성해 줍니다.
                .baseUrl(SERVER_IO) // 통신할 서버 주소를 설정합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return instance!!
    }

    private const val SERVER_IO = "http://43.200.169.154:8080"
    private const val SERVER_JITO = "http://13.125.207.155:8080"
}
