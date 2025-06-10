package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.repository.CartItemsRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductsRepositoryImpl
import woowacourse.shopping.data.repository.ViewedItemRepositoryImpl
import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.local.recent.ViewedItemDatabase
import woowacourse.shopping.data.source.remote.Client.getCartApiService
import woowacourse.shopping.data.source.remote.Client.getCouponApiService
import woowacourse.shopping.data.source.remote.Client.getOrderApiService
import woowacourse.shopping.data.source.remote.Client.getProductsApiService
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.data.source.remote.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.source.remote.order.OrderRemoteDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.repository.CartItemsRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository

object RepositoryProvider {
    lateinit var productsRepository: ProductsRepository
        private set
    lateinit var cartItemRepository: CartItemsRepository
        private set
    lateinit var viewedItemRepository: ViewedItemRepository
        private set
    lateinit var couponRepository: CouponRepository
        private set
    lateinit var orderRepository: OrderRepository
        private set

    private val cartItemsLocalDataSource by lazy { CartItemsLocalDataSource() }

    fun init(context: Context) {
        productsRepository =
            ProductsRepositoryImpl(
                ProductsRemoteDataSource(getProductsApiService),
                CartItemsRemoteDataSource(getCartApiService),
            )
        cartItemRepository =
            CartItemsRepositoryImpl(
                CartItemsRemoteDataSource(getCartApiService),
                cartItemsLocalDataSource,
            )
        viewedItemRepository =
            ViewedItemRepositoryImpl(ViewedItemDatabase.getInstance(context).viewedItemDao())
        couponRepository = CouponRepositoryImpl(CouponRemoteDataSource(getCouponApiService))
        orderRepository = OrderRepositoryImpl(
            OrderRemoteDataSource(getOrderApiService),
            cartItemsLocalDataSource,
        )
    }
}
