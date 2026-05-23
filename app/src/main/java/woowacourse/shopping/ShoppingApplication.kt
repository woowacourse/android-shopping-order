package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.local.UserAuthDataStore
import woowacourse.shopping.data.local.database.DataBase
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.remote.server.RetrofitProvider
import woowacourse.shopping.data.remote.server.repository.CartRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.CouponRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.OrderRepositoryImpl
import woowacourse.shopping.data.remote.server.repository.ProductRepositoryImpl
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.data.remote.server.service.CouponService
import woowacourse.shopping.data.remote.server.service.OrderService
import woowacourse.shopping.data.remote.server.service.ProductService
import kotlin.jvm.java

class ShoppingApplication : Application() {
    val database by lazy { DataBase.getDatabase(this) }

    val recentlyViewedProductRepository by lazy {
        RecentlyViewedProductRepositoryImpl(database.recentlyViewedProductDao())
    }

    val userAuthDataStore by lazy { UserAuthDataStore(context = this) }

    private val retrofitClient by lazy {
        RetrofitProvider(
            authHeaderProvider = {
                userAuthDataStore.encodedUserAuthInfo.value
            },
        )
    }

    val productService: ProductService by lazy {
        retrofitClient.create(ProductService::class.java)
    }

    val cartService: CartService by lazy {
        retrofitClient.create(CartService::class.java)
    }

    val orderService: OrderService by lazy {
        retrofitClient.create(OrderService::class.java)
    }

    val couponService: CouponService by lazy {
        retrofitClient.create(CouponService::class.java)
    }

    val productRepository by lazy {
        ProductRepositoryImpl(productService)
    }

    val cartRepository by lazy {
        CartRepositoryImpl(cartService)
    }

    val orderRepository by lazy {
        OrderRepositoryImpl(orderService)
    }

    val couponRepository by lazy {
        CouponRepositoryImpl(couponService)
    }
}
