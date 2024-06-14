package woowacourse.shopping.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.coupon.CouponRepositoryImpl
import woowacourse.shopping.data.coupon.remote.RemoteCouponDataSource
import woowacourse.shopping.data.local.AppDatabase
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.order.RemoteOrderDataSource
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recent.RecentProductRepositoryImpl
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import woowacourse.shopping.presentation.ui.detail.ProductDetailViewModel
import woowacourse.shopping.presentation.ui.payment.PaymentViewModel
import woowacourse.shopping.presentation.ui.shopping.ShoppingViewModel

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                ProductDetailViewModel(
                    ProductRepositoryImpl(),
                    CartRepositoryImpl(
                        remoteCartDataSource = RemoteCartDataSource(),
                    ),
                    RecentProductRepositoryImpl(recentDao),
                ) as T
            }

            modelClass.isAssignableFrom(ShoppingViewModel::class.java) -> {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                ShoppingViewModel(
                    productRepository = ProductRepositoryImpl(),
                    recentRepository = RecentProductRepositoryImpl(recentDao),
                    cartRepository =
                        CartRepositoryImpl(
                            remoteCartDataSource = RemoteCartDataSource(),
                        ),
                ) as T
            }

            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                CartViewModel(
                    cartRepository =
                        CartRepositoryImpl(
                            remoteCartDataSource = RemoteCartDataSource(),
                        ),
                    productRepository = ProductRepositoryImpl(),
                    recentRepository = RecentProductRepositoryImpl(recentDao),
                ) as T
            }

            modelClass.isAssignableFrom(PaymentViewModel::class.java) -> {
                val cartRepositoryImpl =
                    CartRepositoryImpl(remoteCartDataSource = RemoteCartDataSource())
                PaymentViewModel(
                    cartRepository = cartRepositoryImpl,
                    orderRepository =
                        OrderRepositoryImpl(
                            RemoteOrderDataSource(),
                            cartRepositoryImpl,
                        ),
                    couponRepository = CouponRepositoryImpl(remoteCouponDataSource = RemoteCouponDataSource()),
                ) as T
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }
}
