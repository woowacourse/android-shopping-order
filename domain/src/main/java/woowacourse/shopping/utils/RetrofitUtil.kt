package woowacourse.shopping.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.service.RetrofitCartService
import woowacourse.shopping.data.service.RetrofitOrderService
import woowacourse.shopping.data.service.RetrofitProductService

class RetrofitUtil private constructor(
    private val baseUrl: String,
    private val credentials: String
) {

    private val normalRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpAuthClient)
            .build()
    }

    val retrofitProductService: RetrofitProductService by lazy {
        normalRetrofit.create(RetrofitProductService::class.java)
    }

    val retrofitCartService: RetrofitCartService by lazy {
        authRetrofit.create(RetrofitCartService::class.java)
    }

    val retrofitOrderService: RetrofitOrderService by lazy {
        authRetrofit.create(RetrofitOrderService::class.java)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build()
    }

    private val okHttpAuthClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).addInterceptor(createdAuthInterceptor())
            .build()
    }

    private fun createdAuthInterceptor(): Interceptor = Interceptor { chain ->
        with(chain) {
            val modifiedRequest = request().newBuilder()
                .header("Authorization", credentials)
                .build()
            proceed(modifiedRequest)
        }
    }

    companion object {
        private var instance: RetrofitUtil? = null

        fun getInstance(baseUrl: String = "", credentials: String = ""): RetrofitUtil {
            if (instance == null) {
                instance = RetrofitUtil(baseUrl.removeSuffix("/"), credentials)
            }
            return instance as RetrofitUtil
        }
    }
}
