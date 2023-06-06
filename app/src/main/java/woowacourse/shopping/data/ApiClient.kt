package woowacourse.shopping.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter
import java.util.concurrent.TimeUnit

class ApiClient private constructor() {

    private val authorizationInterceptor: Interceptor =
        Interceptor { chain ->
            with(chain) {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader("Authorization", getAuthToken())
                        .build()
                )
            }
        }

    private fun getAuthToken() =
        PreferenceUtil(context).getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(authorizationInterceptor)
            .build()

    fun initRetrofitBuilder(baseUrl: String) {
        client = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    companion object {
        lateinit var client: Retrofit

        private lateinit var context: Context

        private var instance: ApiClient? = null
        fun getInstance(_context: Context): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient().also {
                    context = _context
                    instance = it
                }
            }
        }
    }
}
