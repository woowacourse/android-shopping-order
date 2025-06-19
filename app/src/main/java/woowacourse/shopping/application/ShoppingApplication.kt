package woowacourse.shopping.application

import android.app.Application
import woowacourse.shopping.data.local.ShoppingDatabase
import woowacourse.shopping.data.local.history.repository.HistoryRepositoryImpl
import woowacourse.shopping.data.remote.cart.CartClient
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.coupon.CouponClient
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.data.remote.order.OrderClient
import woowacourse.shopping.data.remote.order.OrderRepository
import woowacourse.shopping.data.remote.product.ProductClient
import woowacourse.shopping.data.remote.product.ProductRepository

class ShoppingApplication : Application() {
    private val database by lazy { ShoppingDatabase.getDatabase(applicationContext) }
    val historyRepository by lazy { HistoryRepositoryImpl(database.historyDao()) }
    val productRepository by lazy { ProductRepository(ProductClient.service) }
    val cartRepository by lazy { CartRepository(CartClient.service) }
    val couponRepository by lazy { CouponRepository(CouponClient.service) }
    val orderRepository by lazy { OrderRepository(OrderClient.service) }
}
