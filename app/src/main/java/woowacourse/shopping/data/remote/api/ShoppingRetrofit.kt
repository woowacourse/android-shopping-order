package woowacourse.shopping.data.remote.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.dto.response.BuyXGetYCoupon
import woowacourse.shopping.data.remote.dto.response.CouponDto
import woowacourse.shopping.data.remote.dto.response.FixedDiscountCoupon
import woowacourse.shopping.data.remote.dto.response.FreeShippingCoupon
import woowacourse.shopping.data.remote.dto.response.PercentageDiscountCoupon
import woowacourse.shopping.data.remote.service.CartItemService
import woowacourse.shopping.data.remote.service.CouponService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.ProductService

object ShoppingRetrofit {
    private val json =
        Json {
            serializersModule =
                SerializersModule {
                    polymorphic(CouponDto::class) {
                        subclass(FixedDiscountCoupon::class)
                        subclass(BuyXGetYCoupon::class)
                        subclass(FreeShippingCoupon::class)
                        subclass(PercentageDiscountCoupon::class)
                    }
                    classDiscriminator = "discountType"
                }
        }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(ShoppingOkHttpClient.INSTANCE)
            .build()
    }

    val productService: ProductService = retrofit.create(ProductService::class.java)

    val cartItemService: CartItemService = retrofit.create(CartItemService::class.java)

    val orderService: OrderService = retrofit.create(OrderService::class.java)

    val couponService: CouponService = retrofit.create(CouponService::class.java)
}
