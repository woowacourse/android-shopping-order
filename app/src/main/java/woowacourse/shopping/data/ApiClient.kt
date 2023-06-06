package woowacourse.shopping.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter

object ApiClient {
    lateinit var client: Retrofit

    fun initClient(baseUrl: String, sharedPreferencesDb: SharedPreferencesDb) {
        client = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(AppInterceptor(sharedPreferencesDb)))
            .build()
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor(private val sharedPreferencesDb: SharedPreferencesDb) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader(
                    "Authorization",
                    sharedPreferencesDb.getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")
                )
                .build()
            proceed(newRequest)
        }
    }
}
