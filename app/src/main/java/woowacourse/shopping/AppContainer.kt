package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.datasource.HistoryDataSource
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.data.db.PetoMarketDatabase
import woowacourse.shopping.data.network.RetrofitProvider
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.data.network.service.CouponService
import woowacourse.shopping.data.network.service.OrderService
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultCouponRepository
import woowacourse.shopping.data.repository.DefaultHistoryRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.loader.HistoryLoader

class AppContainer(
    context: Context,
) {
    private val db = PetoMarketDatabase.getInstance(context)

    private val historyDao = db.historyDao()

    private val historyDataSource = HistoryDataSource(historyDao)

    private val productService: ProductService = RetrofitProvider.productService

    private val cartService: CartService = RetrofitProvider.cartService

    private val cartDataSource = CartDataSource(cartService)

    private val productsDataSource = ProductsDataSource(productService)

    val productRepository: ProductRepository =
        DefaultProductRepository(productsDataSource)

    val historyRepository: HistoryRepository = DefaultHistoryRepository(historyDataSource)

    val cartRepository: CartRepository = DefaultCartRepository(cartDataSource)

    val historyLoader = HistoryLoader(productRepository, historyRepository)

    private val couponService: CouponService = RetrofitProvider.couponService

    private val couponDataSource = CouponDataSource(couponService)

    val couponRepository: CouponRepository = DefaultCouponRepository(couponDataSource)

    private val orderService: OrderService = RetrofitProvider.orderService

    private val orderDataSource = OrderDataSource(orderService)

    val orderRepository: OrderRepository = DefaultOrderRepository(orderDataSource)
}
