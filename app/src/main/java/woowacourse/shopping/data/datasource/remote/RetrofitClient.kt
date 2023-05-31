package woowacourse.shopping.data.datasource.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var retrofitWithToken: Retrofit? = null

    fun getInstanceWithToken(token: String): Retrofit {
        return retrofitWithToken ?: synchronized(this) {
            val okHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.addHeader("Authorization", "Basic $token")
                    chain.proceed(builder.build())
                })
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ServerInfo.currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitWithToken = retrofit
            retrofit
        }
    }
}
