package woowacourse.shopping.data.client

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.configure.ApplicationConfigure
import woowacourse.shopping.data.service.RetrofitCartService
import woowacourse.shopping.data.service.RetrofitOrderService
import woowacourse.shopping.data.service.RetrofitProductService

class RetrofitClient private constructor(
    private val baseUrl: String,
    private val credentials: String
) {

    val retrofitProductService: RetrofitProductService by lazy {
        normalRetrofit.create(RetrofitProductService::class.java)
    }

    val retrofitCartService: RetrofitCartService by lazy {
        authRetrofit.create(RetrofitCartService::class.java)
    }

    val retrofitOrderService: RetrofitOrderService by lazy {
        authRetrofit.create(RetrofitOrderService::class.java)
    }

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

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor).build()
    }

    private val okHttpAuthClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val authInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            with(chain) {
                val modifiedRequest = request().newBuilder()
                    .header("Authorization", credentials)
                    .build()
                proceed(modifiedRequest)
            }
        }
    }

    companion object {
        private var instance: RetrofitClient? = null

        private fun createInstance(baseUrl: String = "", credentials: String = ""): RetrofitClient {
            return RetrofitClient(baseUrl.removeSuffix("/"), credentials)
        }

        @Synchronized
        fun getInstance(credentials: String = ""): RetrofitClient {
            return instance
                ?: createInstance(ApplicationConfigure.BASE_URL, credentials).also { instance = it }
        }
    }
}
