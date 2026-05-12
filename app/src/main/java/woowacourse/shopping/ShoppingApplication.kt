package woowacourse.shopping

import android.app.Application
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping._archive.di.AppContainer
import woowacourse.shopping.network.ProductResponse
import woowacourse.shopping.network.RetrofitService

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        buildRetrofit()
        AppContainer.init(this)
    }

    fun buildRetrofit() {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        val httpClient = OkHttpClient.Builder()
            .build()

        val retrofitService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(RetrofitService::class.java)

        retrofitService.requestProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body() ?: return
                    Log.d("retrofit", "body: $body")
                }
            }

            override fun onFailure(
                call: Call<ProductResponse>,
                t: Throwable
            ) {
                Log.d("retrofit", "error: $t")
            }

        })
    }

    companion object {
        val baseUrl: String =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"
    }
}
