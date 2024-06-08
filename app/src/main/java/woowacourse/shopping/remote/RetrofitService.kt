package woowacourse.shopping.remote

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
    val baseUrlInterceptor = BaseUrlInterceptor() //## 2. 커스텀 인터셉터를 만들어서 동적으로 BASE URL 을 변경

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(BuildConfig.BASIC_AUTH_USER_DEV, BuildConfig.BASIC_AUTH_PASSWORD_DEV))
            .addInterceptor(baseUrlInterceptor) // ## 2. 커스텀 인터셉터를 만들어서 동적으로 BASE URL 을 변경
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

    val retrofitService: Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_DEV)
            .client(okHttpClient)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

// ## 2. 커스텀 인터셉터를 만들어서 동적으로 BASE URL 을 변경
class BaseUrlInterceptor : Interceptor {
    @Volatile
    private var baseUrl: HttpUrl? = null

    fun setBaseUrl(url: String) {
        this.baseUrl = url.toHttpUrlOrNull()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        Log.d(TAG, "intercept: originalRequest: $originalRequest")
        Log.d(TAG, "intercept: originalHttpUrl: $originalHttpUrl")

        // Change only the base URL (scheme + host + port)
        val newUrl = baseUrl?.let {
            originalHttpUrl.newBuilder()
                .scheme(it.scheme)
                .host(it.host)
                .port(it.port)
                .build()
        }

        Log.d(TAG, "intercept: newUrl: $newUrl")
        Log.d(TAG, "intercept: baseUrl: $baseUrl")

        val newRequest = newUrl?.let {
            originalRequest.newBuilder().url(it).build()
        } ?: originalRequest

        return chain.proceed(newRequest)
    }

    companion object {
        private const val TAG = "BaseUrlInterceptor"
    }
}
