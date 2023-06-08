package woowacourse.shopping.data.datasource.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    private var retrofitWithToken: Retrofit? = null

    private const val PRE_CREDENTIAL = "Basic "

    fun getInstanceWithToken(token: String): Retrofit {
        return retrofitWithToken ?: synchronized(this) {
            val okHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(
                    Interceptor { chain ->
                        val builder = chain.request().newBuilder()
                        builder.addHeader("Authorization", PRE_CREDENTIAL + token)
                        chain.proceed(builder.build())
                    }
                )
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ServerInfo.currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
            retrofitWithToken = retrofit
            retrofit
        }
    }
}
