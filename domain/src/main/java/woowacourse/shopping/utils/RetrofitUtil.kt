package woowacourse.shopping.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.service.RetrofitCartService
import woowacourse.shopping.data.service.RetrofitOrderService
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

    val retrofitOrderService: RetrofitOrderService by lazy {
        getRetrofit().create(RetrofitOrderService::class.java)
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

    fun <T> callback(callback: (Result<T>) -> Unit): retrofit2.Callback<T> {
        return object : retrofit2.Callback<T> {
            override fun onResponse(call: retrofit2.Call<T>, response: retrofit2.Response<T>) {
                if (response.isSuccessful) {
                    when (response.body()) {
                        null -> callback(Result.failure(Throwable("response body is null")))
                        else -> callback(Result.success(response.body()!!))
                    }
                } else {
                    callback(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
                callback(Result.failure(t))
            }
        }
    }
}
