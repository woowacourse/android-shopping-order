package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.repository.CartItemsRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductsRepositoryImpl
import woowacourse.shopping.data.repository.ViewedItemRepositoryImpl
import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.local.recent.ViewedItemDatabase
import woowacourse.shopping.data.source.remote.Client.getCartRetrofitService
import woowacourse.shopping.data.source.remote.Client.getCouponApiService
import woowacourse.shopping.data.source.remote.Client.getOrderRetrofitService
import woowacourse.shopping.data.source.remote.Client.getProductRetrofitService
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.data.source.remote.order.OrderRemoteDataSource
import woowacourse.shopping.data.source.remote.payment.PaymentRemoteDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository

object RepositoryProvider {
    lateinit var productsRepository: ProductsRepository
    lateinit var cartItemRepository: CartItemRepository
    lateinit var viewedItemRepository: ViewedItemRepository
    lateinit var orderRepository: OrderRepository
    lateinit var couponRepository: CouponRepository

    fun init(application: Application) {
        viewedItemRepository =
            ViewedItemRepositoryImpl(ViewedItemDatabase.getInstance(application).viewedItemDao())
        productsRepository =
            ProductsRepositoryImpl(
                ProductsRemoteDataSource(getProductRetrofitService),
                viewedItemRepository,
            )
        cartItemRepository =
            CartItemsRepositoryImpl(
                CartItemsRemoteDataSource(getCartRetrofitService),
                CartItemsLocalDataSource(),
            )
        orderRepository =
            OrderRepositoryImpl(
                OrderRemoteDataSource(getOrderRetrofitService),
            )
        couponRepository =
            CouponRepositoryImpl(
                PaymentRemoteDataSource(getCouponApiService)
            )
    }
}
