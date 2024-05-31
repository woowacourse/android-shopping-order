package woowacourse.shopping.remote.common

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object RetrofitService {
    private val logging =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor("hxeyexn", "password"))
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
            .baseUrl("http://54.180.95.212:8080")
            .client(okHttpClient)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
