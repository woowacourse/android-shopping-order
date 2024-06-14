package woowacourse.shopping.remote

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import java.lang.reflect.Type

object RetrofitService {
    private val logging =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Volatile
    private var baseUrl: HttpUrl =
        BuildConfig.BASE_URL_DEV.toHttpUrlOrNull() ?: throw IllegalStateException("BASE_URL_DEV is invalid")

    private val baseUrlInterceptor =
        Interceptor { chain ->
            val originalRequest = chain.request()
            val originalHttpUrl = originalRequest.url

            Log.d(TAG, "intercept: originalRequest: $originalRequest originalHttpUrl: $originalHttpUrl")

            val newUrl =
                baseUrl.let {
                    originalHttpUrl.newBuilder()
                        .scheme(it.scheme)
                        .host(it.host)
                        .port(it.port)
                        .build()
                }

            val newRequest =
                newUrl.let {
                    originalRequest.newBuilder().url(it).build()
                }
            Log.d(TAG, "intercept: newUrl: $newUrl, baseUrl: $baseUrl, newRequest: $newRequest ")

            chain.proceed(newRequest)
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(BuildConfig.BASIC_AUTH_USER_DEV, BuildConfig.BASIC_AUTH_PASSWORD_DEV))
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(logging)
            .build()

    private val nullOnEmptyConverterFactory =
        object : Converter.Factory() {
            fun converterFactory() = this

            override fun responseBodyConverter(
                type: Type,
                annotations: Array<out Annotation>,
                retrofit: Retrofit,
            ) = object : Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter =
                    retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

                override fun convert(value: ResponseBody) =
                    if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
            }
        }

    fun createRetorift(url: String = BuildConfig.BASE_URL_DEV): Retrofit {
        baseUrl = url.toHttpUrlOrNull() ?: throw IllegalStateException("BASE_URL_DEV is invalid")
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private const val TAG = "RetrofitService"
}
