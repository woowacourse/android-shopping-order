package woowacourse.shopping.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    fun <T> callback(callback: (Result<T>) -> Unit): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    when (response.body()) {
                        null -> callback(Result.failure(Throwable("response body is null")))
                        else -> callback(Result.success(response.body()!!))
                    }
                } else {
                    callback(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback(Result.failure(t))
            }
        }
    }

    fun <T> callbackWithNoBody(block: (Result<T>?) -> Unit): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    block(null)
                } else {
                    block(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                block(Result.failure(t))
            }
        }
    }

    fun callbackWithLocationHeader(callback: (Result<Long>) -> Unit): Callback<Unit> {
        return object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val orderId = response.headers()["Location"]
                        ?.substringAfterLast("/")
                        ?.toLong()
                        ?: return callback(Result.failure(Throwable("location header is null")))
                    callback(Result.success(orderId))
                } else {
                    callback(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(Result.failure(t))
            }
        }
    }
}
