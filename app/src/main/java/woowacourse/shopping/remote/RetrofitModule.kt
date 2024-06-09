package woowacourse.shopping.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import java.util.concurrent.TimeUnit

object RetrofitModule {
    private const val BASE_URL = BuildConfig.SHOPPING_BASE_URL
    private const val USER_ID = BuildConfig.DUMMY_ID
    private const val USER_PASSWORD = BuildConfig.DUMMY_PASSWORD
    private const val CONNECT_TIME_OUT = 60L
    private const val READ_TIME_OUT = 30L
    private const val WRITE_TIME_OUT = 15L

//
//    private val module = SerializersModule {
//        polymorphic(CouponResponse::class) {
//            subclass(CouponResponse.Fixed5000::class/*, CouponResponse.Fixed5000.serializer()*/)
//            subclass(CouponResponse.Bogo::class/*, CouponResponse.Bogo.serializer()*/)
//            subclass(CouponResponse.Freeshipping::class/*, CouponResponse.Freeshipping.serializer()*/)
//            subclass(CouponResponse.Miraclesale::class/*, CouponResponse.Miraclesale.serializer()*/)
//        }
//    }
    private val json =
        Json {
//            isLenient = true
            ignoreUnknownKeys = true
//            serializersModule = module
//            classDiscriminator = "discountType"
//            useArrayPolymorphism = true
        }

    private val INSTANCE: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient())
            .addConverterFactory(jsonConverterFactory())
            .build()

    fun retrofit(): Retrofit = INSTANCE

    private fun jsonConverterFactory(): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    private fun loggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            },
        )

    private fun httpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor())
            .addInterceptor(AuthInterceptor(USER_ID, USER_PASSWORD))
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS).build()
}
