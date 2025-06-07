package woowacourse.shopping.data.di

import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.data.network.service.CouponService
import woowacourse.shopping.data.network.service.OrderService
import woowacourse.shopping.data.network.service.ProductService

class NetworkModule(
    retrofit: RetrofitModule,
) {
    val productService: ProductService by lazy {
        retrofit.instance.create(ProductService::class.java)
    }

    val cartService: CartService by lazy {
        retrofit.basicAuthInstance.create(CartService::class.java)
    }

    val orderService: OrderService by lazy {
        retrofit.basicAuthInstance.create(OrderService::class.java)
    }

    val couponService: CouponService by lazy {
        retrofit.basicAuthInstance.create(CouponService::class.java)
    }
}
