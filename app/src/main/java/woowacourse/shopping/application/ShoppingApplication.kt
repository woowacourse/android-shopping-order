package woowacourse.shopping.application

import android.app.Application
import woowacourse.shopping.data.local.ShoppingDatabase
import woowacourse.shopping.data.local.history.repository.HistoryRepositoryImpl
import woowacourse.shopping.data.remote.NetworkClient
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.data.remote.order.OrderRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.goods.GoodsViewModel
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsViewModel
import woowacourse.shopping.feature.payment.PaymentViewModel
import woowacourse.shopping.util.ViewModelFactory

class ShoppingApplication : Application() {
    private val database by lazy { ShoppingDatabase.getDatabase(applicationContext) }

    val goodsFactory by lazy {
        ViewModelFactory {
            GoodsViewModel(
                HistoryRepositoryImpl(database.historyDao()),
                ProductRepository(NetworkClient.productService),
                CartRepository(NetworkClient.cartService),
            )
        }
    }

    val goodsDetailsFactory: (Long) -> ViewModelFactory<GoodsDetailsViewModel> = { id ->
        ViewModelFactory {
            GoodsDetailsViewModel(
                id,
                CartRepository(NetworkClient.cartService),
                HistoryRepositoryImpl(database.historyDao()),
                ProductRepository(NetworkClient.productService),
            )
        }
    }

    val cartFactory by lazy {
        ViewModelFactory {
            CartViewModel(
                CartRepository(NetworkClient.cartService),
                ProductRepository(NetworkClient.productService),
                HistoryRepositoryImpl(database.historyDao()),
            )
        }
    }

    val paymentFactory: (LongArray) -> ViewModelFactory<PaymentViewModel> = { orderIds ->
        ViewModelFactory {
            PaymentViewModel(
                orderIds,
                CouponRepository(NetworkClient.couponService),
                CartRepository(NetworkClient.cartService),
                OrderRepository(NetworkClient.orderService),
            )
        }
    }
}
