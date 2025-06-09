package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.ShoppingCartDatabase
import woowacourse.shopping.data.TokenProvider
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.network.RetrofitInstance
import woowacourse.shopping.data.repository.CartProductRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductByProductIdUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.GetTotalCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.cart.RemoveFromCartUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetProductsUseCase

class ShoppingApplication : Application() {
    private val database by lazy { ShoppingCartDatabase.getDataBase(this) }
    private val retrofitInstance by lazy { RetrofitInstance(TokenProvider(this)) }

    val recentProductRepository
        by lazy {
            RecentProductRepositoryImpl(
                RecentProductLocalDataSource(database.recentProductDao),
                ProductRemoteDataSource(retrofitInstance.productService),
            )
        }
    private val productRepository
        by lazy { ProductRepositoryImpl(ProductRemoteDataSource(retrofitInstance.productService)) }
    private val cartProductRepository
        by lazy { CartProductRepositoryImpl(CartProductRemoteDataSource(retrofitInstance.cartProductService)) }
    val couponRepository
        by lazy { CouponRepositoryImpl(CouponRemoteDataSource(retrofitInstance.couponService)) }
    val orderRepository
        by lazy { OrderRepositoryImpl(OrderRemoteDataSource(retrofitInstance.orderService)) }

    val getProductsUseCase by lazy { GetProductsUseCase(productRepository) }

    val getCartProductsUseCase by lazy { GetCartProductsUseCase(cartProductRepository) }
    val getCartProductByProductIdUseCase by lazy { GetCartProductByProductIdUseCase(cartProductRepository) }
    val getTotalCartProductQuantityUseCase by lazy { GetTotalCartProductQuantityUseCase(cartProductRepository) }
    val addToCartUseCase by lazy { AddToCartUseCase(cartProductRepository) }
    val removeFromCartUseCase by lazy { RemoveFromCartUseCase(cartProductRepository) }
    val updateCartQuantityUseCase by lazy { UpdateCartQuantityUseCase(cartProductRepository, removeFromCartUseCase) }
}
