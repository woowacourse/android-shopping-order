package woowacourse.shopping.data.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(
    private val baseUrl: String,
    private val credentials: String,
) {
    val retrofitProductService: RetrofitProductService by lazy {
        retrofit.create(RetrofitProductService::class.java)
    }

    val retrofitCartService: RetrofitCartService by lazy {
        authRetrofit.create(RetrofitCartService::class.java)
    }

    val retrofitOrderService: RetrofitOrderService by lazy {
        authRetrofit.create(RetrofitOrderService::class.java)
    }

    private val retrofit: Retrofit by lazy {
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
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .build()
    }

    private val okHttpAuthClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", credentials)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    companion object {
        private var instance: RetrofitClient? = null

        fun getInstance(baseUrl: String = "", credentials: String = ""): RetrofitClient {
            if (instance == null || (baseUrl != "" && credentials != "")) {
                instance = RetrofitClient(baseUrl.removeSuffix("/"), credentials)
            }
            return instance as RetrofitClient
        }

        fun getServerUrl(): String = instance?.baseUrl ?: ""
    }
}
