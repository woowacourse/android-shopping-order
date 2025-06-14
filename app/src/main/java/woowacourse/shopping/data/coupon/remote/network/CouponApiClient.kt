package woowacourse.shopping.data.coupon.remote.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.coupon.remote.dto.BuyXGetYCoupon
import woowacourse.shopping.data.coupon.remote.dto.CouponResponseDto
import woowacourse.shopping.data.coupon.remote.dto.FixedCoupon
import woowacourse.shopping.data.coupon.remote.dto.FreeShippingCoupon
import woowacourse.shopping.data.coupon.remote.dto.PercentageCoupon
import woowacourse.shopping.data.network.ApiClient.MEDIA_TYPE_JSON
import woowacourse.shopping.data.network.ApiClient.provideOkHttpClient

object CouponApiClient {
    val couponModule =
        SerializersModule {
            polymorphic(CouponResponseDto::class) {
                subclass(FixedCoupon::class)
                subclass(BuyXGetYCoupon::class)
                subclass(FreeShippingCoupon::class)
                subclass(PercentageCoupon::class)
            }
        }

    val json =
        Json {
            serializersModule = couponModule
            classDiscriminator = "discountType"
            ignoreUnknownKeys = true
            useArrayPolymorphism = false
        }

    fun getApiClient(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(
                json.asConverterFactory(MEDIA_TYPE_JSON.toMediaType()),
            ).build()
}
