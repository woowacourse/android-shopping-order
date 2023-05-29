package woowacourse.shopping.data.util.retrofit

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.service.retrofit.cart.RetrofitCartProductService
import woowacourse.shopping.data.service.retrofit.product.RetrofitProductService
import java.lang.reflect.Type

object RetrofitUtil {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .build()
    }

    fun getProductByRetrofit(baseUrl: String): RetrofitProductService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetrofitProductService::class.java)
    }

    fun getCartProductByRetrofit(baseUrl: String): RetrofitCartProductService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetrofitCartProductService::class.java)
    }

    class NullOnEmptyConverterFactory : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit,
        ): Converter<ResponseBody, *> {
            val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
            return Converter<ResponseBody, Any> {
                if (it.contentLength() == 0L) return@Converter EmptyResponse()
                delegate.convert(it)
            }
        }
    }

    class EmptyResponse()
}
